package com.david.trenes.service;

import com.david.trenes.model.*;
import com.david.trenes.repository.*;
import com.david.trenes.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompraMasivaBilletesService {

    private final BilleteService billeteService;
    private final BilleteRepository billeteRepository;
    private final PasajeroRepository pasajeroRepository;
    private final HorarioRepository horarioRepository;
    private final TrenRepository trenRepository;
    private final EstacionRepository estacionRepository;
    private final CurrentUserService currentUserService;
    private final Random random = new Random();

    /**
     * Realiza compra masiva de billetes para pasajeros
     */
    public CompraMasivaResponse comprarBilletesMasivos(int cantidadPasajeros) {
        log.info("Iniciando compra masiva de billetes para {} pasajeros", cantidadPasajeros);

        // 1. Obtener pasajeros disponibles
        List<Pasajero> pasajeros = obtenerPasajerosDisponibles(cantidadPasajeros);
        log.info("Pasajeros disponibles para compra: {}", pasajeros.size());
        
        if (pasajeros.isEmpty()) {
            throw new IllegalStateException("No hay pasajeros disponibles para comprar billetes");
        }

        // 2. Obtener horarios disponibles
        List<Horario> horariosDisponibles = obtenerHorariosDisponibles();
        log.info("Horarios disponibles: {}", horariosDisponibles.size());
        
        if (horariosDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay horarios disponibles para venta de billetes");
        }

        // 3. Obtener estaciones para rutas
        List<Estacion> estaciones = estacionRepository.findByActivoTrue();
        log.info("Estaciones disponibles: {}", estaciones.size());
        
        if (estaciones.size() < 2) {
            throw new IllegalStateException("Se necesitan al menos 2 estaciones para generar rutas");
        }

        // 4. Realizar compras
        List<CompraIndividual> comprasRealizadas = new ArrayList<>();
        
        log.info("Iniciando proceso de compra para {} pasajeros", pasajeros.size());
        
        for (Pasajero pasajero : pasajeros) {
            try {
                CompraIndividual compra = realizarCompraIndividual(pasajero, horariosDisponibles, estaciones);
                comprasRealizadas.add(compra);

                if (Boolean.TRUE.equals(compra.getExitosa())) {
                    log.info("Compra exitosa para pasajero {} - billete {}",
                            pasajero.getNombre(), compra.getCodigoBillete());
                } else {
                    log.warn("Compra fallida para pasajero {}: {}",
                            pasajero.getNombre(), compra.getMensaje());
                }

            } catch (Exception e) {
                log.error("Error al comprar billete para pasajero {}: {}", 
                        pasajero.getId(), e.getMessage(), e);
                
                CompraIndividual compraFallida = CompraIndividual.builder()
                        .pasajeroId(pasajero.getId())
                        .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                        .exitosa(false)
                        .mensaje("Error: " + e.getMessage())
                        .build();
                comprasRealizadas.add(compraFallida);
            }
        }

        // 5. Generar respuesta (contando sobre lo realmente procesado)
        int exitosas = (int) comprasRealizadas.stream().filter(c -> Boolean.TRUE.equals(c.getExitosa())).count();
        int fallidas = comprasRealizadas.size() - exitosas;

        if (exitosas + fallidas != comprasRealizadas.size()) {
            log.warn("Inconsistencia en conteo: exitosas={}, fallidas={}, totalCompras={}",
                    exitosas, fallidas, comprasRealizadas.size());
        }

        log.info("Resumen compra masiva: solicitados={}, procesados={}, exitosas={}, fallidas={}",
                cantidadPasajeros, comprasRealizadas.size(), exitosas, fallidas);

        return CompraMasivaResponse.builder()
                .totalPasajeros(comprasRealizadas.size())
                .comprasExitosas(exitosas)
                .comprasFallidas(fallidas)
                .compras(comprasRealizadas)
                .build();
    }

    /**
     * Obtiene pasajeros disponibles para comprar billetes
     */
    private List<Pasajero> obtenerPasajerosDisponibles(int cantidad) {
        List<Pasajero> todosLosPasajeros = pasajeroRepository.findByUsuarioId(currentUserService.getCurrentUsuarioId());

        // Filtrar pasajeros activos
        List<Pasajero> pasajerosActivos = todosLosPasajeros.stream()
                .filter(p -> p.getActivo() != null && p.getActivo())
                .collect(Collectors.toList());

        // Filtrar pasajeros que ya tienen billetes comprados
        List<Pasajero> pasajerosSinBilletes = pasajerosActivos.stream()
                .filter(p -> !tieneBilletesComprados(p.getId()))
                .limit(cantidad)
                .collect(Collectors.toList());

        // Si no hay suficientes sin billetes, completar con otros ACTIVOS (aunque ya tengan billetes)
        if (pasajerosSinBilletes.size() < cantidad) {
            int faltantes = cantidad - pasajerosSinBilletes.size();
            List<Pasajero> adicionales = pasajerosActivos.stream()
                    .filter(p -> !pasajerosSinBilletes.contains(p))
                    .limit(faltantes)
                    .collect(Collectors.toList());
            pasajerosSinBilletes.addAll(adicionales);
        }

        return pasajerosSinBilletes.stream()
                .limit(cantidad)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un pasajero ya tiene billetes comprados
     */
    private boolean tieneBilletesComprados(String pasajeroId) {
        try {
            // Usar directamente el repository para evitar problemas de contexto
            List<Billete> billetes = billeteRepository.findByPasajeroId(pasajeroId);
            return !billetes.isEmpty();
        } catch (Exception e) {
            log.warn("Error al verificar billetes del pasajero {}: {}", pasajeroId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene horarios disponibles para compra
     */
    private List<Horario> obtenerHorariosDisponibles() {
        LocalDateTime ahora = LocalDateTime.now();
        
        List<Horario> horariosActivos = horarioRepository.findByActivoTrue().stream()
                .filter(h -> h.getFechaSalida() != null && h.getFechaSalida().isAfter(ahora))
                .collect(Collectors.toList());
        
        // Si no hay horarios futuros, usar todos los activos
        if (horariosActivos.isEmpty()) {
            log.warn("No hay horarios futuros disponibles, usando todos los horarios activos");
            return horarioRepository.findByActivoTrue();
        }
        
        return horariosActivos;
    }

    /**
     * Realiza la compra para un pasajero individual
     */
    private CompraIndividual realizarCompraIndividual(Pasajero pasajero, List<Horario> horarios, List<Estacion> estaciones) {
        try {
            Horario horarioSeleccionado = horarios.get(random.nextInt(horarios.size()));

            List<Estacion> estacionesValidas = obtenerEstacionesValidasParaHorario(horarioSeleccionado, estaciones);
            if (estacionesValidas.size() < 2) {
                estacionesValidas = obtenerEstacionesDesdeOrigenDestino(horarioSeleccionado, estaciones);
            }
            if (estacionesValidas.size() < 2) {
                return CompraIndividual.builder()
                        .pasajeroId(pasajero.getId())
                        .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                        .exitosa(false)
                        .mensaje("No hay estaciones válidas para este horario")
                        .build();
            }

            Estacion[] estacionesRuta = seleccionarOrigenDestinoOrdenados(horarioSeleccionado, estacionesValidas);
            Estacion origen = estacionesRuta[0];
            Estacion destino = estacionesRuta[1];

            if (origen == null || destino == null) {
                return CompraIndividual.builder()
                        .pasajeroId(pasajero.getId())
                        .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                        .exitosa(false)
                        .mensaje("No se pudo seleccionar origen/destino válidos")
                        .build();
            }

            // Intentar primero una clase aleatoria, y si falla por plazas en PRIMERA, caer a TURISTA
            String[] clases = {"TURISTA", "PRIMERA"};
            String claseInicial = clases[random.nextInt(clases.length)];

            for (String claseIntento : List.of(claseInicial, "TURISTA")) {
                try {
                    List<Billete> billetesComprados = billeteService.comprarVariosMasivo(
                            horarioSeleccionado.getId(),
                            List.of(pasajero.getId()),
                            origen.getId(),
                            destino.getId(),
                            claseIntento
                    );

                    if (!billetesComprados.isEmpty()) {
                        Billete billete = billetesComprados.get(0);
                        return CompraIndividual.builder()
                                .pasajeroId(pasajero.getId())
                                .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                                .exitosa(true)
                                .billeteId(billete.getId())
                                .codigoBillete(billete.getCodigoBillete())
                                .horarioId(horarioSeleccionado.getId())
                                .origen(origen.getNombre())
                                .destino(destino.getNombre())
                                .clase(claseIntento)
                                .precio(billete.getPrecioTotal())
                                .mensaje("Compra exitosa")
                                .build();
                    }
                } catch (org.springframework.web.server.ResponseStatusException ex) {
                    // Si fue conflicto por plazas y estábamos en PRIMERA, probamos TURISTA
                    if (ex.getStatusCode().value() == 409 && "PRIMERA".equalsIgnoreCase(claseIntento)) {
                        continue;
                    }
                    throw ex;
                }
            }

            return CompraIndividual.builder()
                    .pasajeroId(pasajero.getId())
                    .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                    .exitosa(false)
                    .mensaje("No se pudo generar billete")
                    .build();

        } catch (Exception e) {
            return CompraIndividual.builder()
                    .pasajeroId(pasajero.getId())
                    .pasajeroNombre(pasajero.getNombre() + " " + pasajero.getApellidos())
                    .exitosa(false)
                    .mensaje("Error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Obtiene estaciones válidas para un horario específico
     */
    private List<Estacion> obtenerEstacionesValidasParaHorario(Horario horario, List<Estacion> todasLasEstaciones) {
        if (horario.getParadas() == null || horario.getParadas().isEmpty()) {
            return List.of();
        }
        
        Set<String> estacionesEnHorario = horario.getParadas().stream()
                .map(Horario.ParadaHorario::getEstacionId)
                .collect(Collectors.toSet());
        
        return todasLasEstaciones.stream()
                .filter(e -> estacionesEnHorario.contains(e.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estaciones desde los campos origen/destino del horario
     */
    private List<Estacion> obtenerEstacionesDesdeOrigenDestino(Horario horario, List<Estacion> todasLasEstaciones) {
        List<Estacion> resultado = new ArrayList<>();
        
        if (horario.getEstacionOrigenId() != null && !horario.getEstacionOrigenId().isBlank()) {
            todasLasEstaciones.stream()
                    .filter(e -> horario.getEstacionOrigenId().equals(e.getId()))
                    .findFirst()
                    .ifPresent(resultado::add);
        }
        
        if (horario.getEstacionDestinoId() != null && !horario.getEstacionDestinoId().isBlank()) {
            todasLasEstaciones.stream()
                    .filter(e -> horario.getEstacionDestinoId().equals(e.getId()))
                    .findFirst()
                    .ifPresent(resultado::add);
        }
        
        return resultado;
    }

    /**
     * Selecciona origen y destino respetando el orden de paradas del horario
     */
    private Estacion[] seleccionarOrigenDestinoOrdenados(Horario horario, List<Estacion> estacionesValidas) {
        if (horario.getParadas() == null || horario.getParadas().isEmpty()) {
            // Si no hay paradas, seleccionar aleatoriamente
            if (estacionesValidas.size() >= 2) {
                Estacion origen = estacionesValidas.get(random.nextInt(estacionesValidas.size()));
                Estacion destino;
                do {
                    destino = estacionesValidas.get(random.nextInt(estacionesValidas.size()));
                } while (destino.getId().equals(origen.getId()));
                return new Estacion[]{origen, destino};
            }
            return new Estacion[]{null, null};
        }

        // Ordenar paradas por orden
        List<Horario.ParadaHorario> paradasOrdenadas = horario.getParadas().stream()
                .filter(p -> p.getEstacionId() != null && !p.getEstacionId().isBlank())
                .filter(p -> p.getOrden() != null)
                .sorted(Comparator.comparing(Horario.ParadaHorario::getOrden))
                .collect(Collectors.toList());

        if (paradasOrdenadas.size() < 2) {
            log.warn("Horario {} tiene menos de 2 paradas válidas", horario.getId());
            return new Estacion[]{null, null};
        }

        Map<String, Estacion> estacionMap = estacionesValidas.stream()
                .collect(Collectors.toMap(Estacion::getId, e -> e));

        for (int intentos = 0; intentos < 50; intentos++) {
            int indiceOrigen = random.nextInt(paradasOrdenadas.size() - 1);
            int indiceDestino = indiceOrigen + 1 + random.nextInt(paradasOrdenadas.size() - indiceOrigen - 1);

            var paradaOrigen = paradasOrdenadas.get(indiceOrigen);
            var paradaDestino = paradasOrdenadas.get(indiceDestino);

            String origenId = paradaOrigen.getEstacionId();
            String destinoId = paradaDestino.getEstacionId();

            // Evita origen==destino (aunque estén en paradas distintas)
            if (origenId.equals(destinoId)) {
                continue;
            }

            Estacion origen = estacionMap.get(origenId);
            Estacion destino = estacionMap.get(destinoId);

            Integer ordenOrigen = paradaOrigen.getOrden();
            Integer ordenDestino = paradaDestino.getOrden();

            if (origen != null && destino != null && ordenOrigen < ordenDestino) {
                log.info("Selección válida: origen={} (orden={}), destino={} (orden={})",
                        origenId, ordenOrigen, destinoId, ordenDestino);
                return new Estacion[]{origen, destino};
            }
        }

        return new Estacion[]{null, null};
    }

    /**
     * DTO para respuesta de compra masiva
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class CompraMasivaResponse {
        private Integer totalPasajeros;
        private Integer comprasExitosas;
        private Integer comprasFallidas;
        private List<CompraIndividual> compras;
    }

    /**
     * DTO para compra individual
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class CompraIndividual {
        private String pasajeroId;
        private String pasajeroNombre;
        private Boolean exitosa;
        private String billeteId;
        private String codigoBillete;
        private String horarioId;
        private String origen;
        private String destino;
        private String clase;
        private Double precio;
        private String mensaje;
    }
}
