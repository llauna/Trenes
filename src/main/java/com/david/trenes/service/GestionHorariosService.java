package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Ruta;
import com.david.trenes.model.Tren;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionHorariosService {

    private final HorarioService horarioService;
    private final RutaService rutaService;
    private final TrenService trenService;

    public void crearHorariosProgramados() {
        log.info("Creando horarios programados para el sistema");
        
        // Obtener rutas activas
        List<Ruta> rutasActivas = rutaService.findByEstado(Ruta.EstadoRuta.ACTIVA);
        
        for (Ruta ruta : rutasActivas) {
            crearHorariosParaRuta(ruta);
        }
        
        log.info("Horarios programados creados exitosamente");
    }

    private void crearHorariosParaRuta(Ruta ruta) {
        if (ruta.getFrecuencia() == null) {
            log.warn("La ruta {} no tiene configuración de frecuencia", ruta.getCodigoRuta());
            return;
        }

        // Crear horarios para los próximos 7 días
        LocalDateTime fechaBase = LocalDateTime.now().toLocalDate().atStartOfDay();
        
        for (int dia = 0; dia < 7; dia++) {
            LocalDateTime fechaActual = fechaBase.plusDays(dia);
            
            if (debeCrearHorarioParaDia(fechaActual, ruta.getFrecuencia())) {
                crearHorariosDiarios(ruta, fechaActual);
            }
        }
    }

    private boolean debeCrearHorarioParaDia(LocalDateTime fecha, Ruta.FrecuenciaRuta frecuencia) {
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1 = Lunes, 7 = Domingo
        
        switch (diaSemana) {
            case 1: return frecuencia.getLunes();
            case 2: return frecuencia.getMartes();
            case 3: return frecuencia.getMiercoles();
            case 4: return frecuencia.getJueves();
            case 5: return frecuencia.getViernes();
            case 6: return frecuencia.getSabado();
            case 7: return frecuencia.getDomingo();
            default: return false;
        }
    }

    private void crearHorariosDiarios(Ruta ruta, LocalDateTime fecha) {
        LocalTime horaPrimeraSalida = LocalTime.parse(ruta.getFrecuencia().getHoraPrimeraSalida());
        LocalTime horaUltimaSalida = LocalTime.parse(ruta.getFrecuencia().getHoraUltimaSalida());
        int intervaloMinutos = ruta.getFrecuencia().getIntervaloMinutos();
        int serviciosDia = ruta.getFrecuencia().getServiciosDia();

        for (int servicio = 0; servicio < serviciosDia; servicio++) {
            LocalTime horaSalida = horaPrimeraSalida.plusMinutes(servicio * intervaloMinutos);
            
            if (horaSalida.isAfter(horaUltimaSalida)) {
                break;
            }

            LocalDateTime fechaSalida = fecha.with(horaSalida);
            LocalDateTime fechaLlegada = fechaSalida.plusMinutes(ruta.getTiempoEstimadoMinutos());

            // Buscar tren disponible según el tipo de ruta
            Tren.TipoTren tipoTrenRequerido = obtenerTipoTrenParaRuta(ruta.getTipoRuta());
            List<Tren> trenesDisponibles = trenService.findByTipoTren(tipoTrenRequerido);
            
            // Filtrar trenes en estado DETENIDO
            trenesDisponibles = trenesDisponibles.stream()
                    .filter(tren -> tren.getEstadoActual() == Tren.EstadoTren.DETENIDO)
                    .collect(java.util.stream.Collectors.toList());
            
            if (trenesDisponibles.isEmpty()) {
                log.warn("No hay trenes disponibles para la ruta {} en el horario {}", ruta.getCodigoRuta(), fechaSalida);
                continue;
            }

            Tren trenAsignado = trenesDisponibles.get(0); // Asignar el primer tren disponible

            Horario horario = crearHorario(ruta, trenAsignado, fechaSalida, fechaLlegada, servicio + 1);
            horarioService.save(horario);
            
            log.info("Horario creado: {} - {} a {}", horario.getCodigoServicio(), horario.getFechaSalida(), horario.getFechaLlegada());
        }
    }

    private Tren.TipoTren obtenerTipoTrenParaRuta(Ruta.TipoRuta tipoRuta) {
        switch (tipoRuta) {
            case EXPRESO:
            case INTERNACIONAL:
                return Tren.TipoTren.ALTA_VELOCIDAD;
            case REGIONAL:
                return Tren.TipoTren.REGIONAL;
            case CERCANIAS:
                return Tren.TipoTren.CERCANIAS;
            case CARGA:
                return Tren.TipoTren.CARGA;
            case TURISTICO:
                return Tren.TipoTren.TURISTICO;
            case ESPECIAL:
                return Tren.TipoTren.ESPECIAL;
            default:
                return Tren.TipoTren.REGIONAL;
        }
    }

    private Horario crearHorario(Ruta ruta, Tren tren, LocalDateTime fechaSalida, LocalDateTime fechaLlegada, int numeroServicio) {
        List<Horario.ParadaHorario> paradasHorario = new ArrayList<>();
        
        // Crear paradas basadas en las paradas de la ruta
        for (Ruta.ParadaRuta paradaRuta : ruta.getEstacionesIntermedias()) {
            LocalDateTime horaLlegadaParada = fechaSalida.plusMinutes(
                (long) (paradaRuta.getKilometro() / ruta.getDistanciaTotalKm() * ruta.getTiempoEstimadoMinutos())
            );
            LocalDateTime horaSalidaParada = horaLlegadaParada.plusMinutes(paradaRuta.getTiempoParadaMinutos());
            
            Horario.ParadaHorario paradaHorario = Horario.ParadaHorario.builder()
                    .estacionId(paradaRuta.getEstacionId())
                    .nombreEstacion(paradaRuta.getNombreEstacion())
                    .orden(paradaRuta.getOrden())
                    .horaLlegadaProgramada(paradaRuta.getOrden() == 1 ? null : horaLlegadaParada)
                    .horaSalidaProgramada(paradaRuta.getOrden() == ruta.getEstacionesIntermedias().size() ? null : horaSalidaParada)
                    .tiempoParadaMinutos(paradaRuta.getTiempoParadaMinutos())
                    .paradaObligatoria(paradaRuta.getObligatoria())
                    .andenAsignado(null) // Se asignará dinámicamente
                    .estado(Horario.EstadoParada.PENDIENTE)
                    .retrasoMinutos(0)
                    .build();
            
            paradasHorario.add(paradaHorario);
        }

        // Crear clases de servicio según el tipo de tren
        List<Horario.ClaseServicio> clases = crearClasesServicio(tren);

        Horario horario = Horario.builder()
                .id(UUID.randomUUID().toString())
                .codigoServicio(generarCodigoServicio(ruta, fechaSalida, numeroServicio))
                .trenId(tren.getId())
                .rutaId(ruta.getId())
                .numeroServicio(String.format("%03d", numeroServicio))
                .tipoServicio(obtenerTipoServicio(ruta.getTipoRuta()))
                .fechaSalida(fechaSalida)
                .fechaLlegada(fechaLlegada)
                .estacionOrigenId(ruta.getEstacionOrigenId())
                .estacionDestinoId(ruta.getEstacionDestinoId())
                .paradas(paradasHorario)
                .conductorId(null) // Se asignará dinámicamente
                .conductorSuplenteId(null)
                .estado(Horario.EstadoHorario.PROGRAMADO)
                .frecuencia(null) // No se usa en horarios individuales
                .capacidadPasajeros(tren.getCapacidadPasajeros())
                .pasajerosActuales(0)
                .ocupacionPorcentaje(0.0)
                .tarifa(ruta.getTarifaBase())
                .clases(clases)
                .observaciones("Horario generado automáticamente")
                .incidencias(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        return horario;
    }

    private String generarCodigoServicio(Ruta ruta, LocalDateTime fechaSalida, int numeroServicio) {
        return String.format("%s-%s-%02d%02d-%03d",
                ruta.getCodigoRuta(),
                fechaSalida.getYear(),
                fechaSalida.getMonthValue(),
                fechaSalida.getDayOfMonth(),
                numeroServicio);
    }

    private Horario.TipoServicio obtenerTipoServicio(Ruta.TipoRuta tipoRuta) {
        switch (tipoRuta) {
            case EXPRESO:
            case INTERNACIONAL:
                return Horario.TipoServicio.REGULAR;
            case REGIONAL:
            case CERCANIAS:
                return Horario.TipoServicio.REGULAR;
            case CARGA:
                return Horario.TipoServicio.CARGA;
            case TURISTICO:
                return Horario.TipoServicio.TURISTICO;
            case ESPECIAL:
                return Horario.TipoServicio.ESPECIAL;
            default:
                return Horario.TipoServicio.REGULAR;
        }
    }

    private List<Horario.ClaseServicio> crearClasesServicio(Tren tren) {
        List<Horario.ClaseServicio> clases = new ArrayList<>();
        
        // Contar vagones por tipo
        int vagonesPrimera = 0;
        int vagonesTurista = 0;
        int vagonesRestaurante = 0;
        
        if (tren.getVagones() != null) {
            for (Tren.Vagon vagon : tren.getVagones()) {
                switch (vagon.getTipo()) {
                    case PASAJEROS_PRIMERA:
                        vagonesPrimera++;
                        break;
                    case PASAJEROS_TURISTA:
                        vagonesTurista++;
                        break;
                    case RESTAURANTE:
                    case CAFETERIA:
                        vagonesRestaurante++;
                        break;
                }
            }
        }
        
        // Crear clase primera si hay vagones de primera clase
        if (vagonesPrimera > 0) {
            clases.add(Horario.ClaseServicio.builder()
                    .nombre("Primera")
                    .capacidad(vagonesPrimera * 40) // Asumiendo 40 pasajeros por vagón de primera
                    .tarifa(tren.getTipoTren() == Tren.TipoTren.ALTA_VELOCIDAD ? 150.0 : 25.0)
                    .pasajerosActuales(0)
                    .disponible(true)
                    .build());
        }
        
        // Crear clase turista si hay vagones turista
        if (vagonesTurista > 0) {
            clases.add(Horario.ClaseServicio.builder()
                    .nombre("Turista")
                    .capacidad(vagonesTurista * 60) // Asumiendo 60 pasajeros por vagón turista
                    .tarifa(tren.getTipoTren() == Tren.TipoTren.ALTA_VELOCIDAD ? 85.0 : 12.0)
                    .pasajerosActuales(0)
                    .disponible(true)
                    .build());
        }
        
        // Crear clase restaurante si hay vagones restaurante
        if (vagonesRestaurante > 0) {
            clases.add(Horario.ClaseServicio.builder()
                    .nombre("Restaurante")
                    .capacidad(vagonesRestaurante * 30) // Asumiendo 30 comensales por vagón restaurante
                    .tarifa(0.0) // El restaurante se paga por consumo, no por billete
                    .pasajerosActuales(0)
                    .disponible(true)
                    .build());
        }
        
        return clases;
    }

    public boolean actualizarEstadoParada(String horarioId, Integer orden, Horario.EstadoParada nuevoEstado) {
        try {
            Horario horario = horarioService.findById(horarioId).orElse(null);
            if (horario != null && horario.getParadas() != null) {
                for (Horario.ParadaHorario parada : horario.getParadas()) {
                    if (parada.getOrden().equals(orden)) {
                        parada.setEstado(nuevoEstado);
                        horario.setFechaActualizacion(LocalDateTime.now());
                        horarioService.save(horario);
                        log.info("Estado de parada {} del horario {} actualizado a: {}", orden, horarioId, nuevoEstado);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error al actualizar estado de parada: {}", horarioId, e);
            return false;
        }
    }

    public List<Horario> obtenerHorariosPorRuta(String rutaId) {
        return horarioService.findByRutaId(rutaId);
    }

    public List<Horario> obtenerHorariosPorTren(String trenId) {
        return horarioService.findByTrenId(trenId);
    }

    public List<Horario> obtenerHorariosPorEstado(Horario.EstadoHorario estado) {
        return horarioService.findByEstado(estado);
    }

    public boolean actualizarEstadoHorario(String horarioId, Horario.EstadoHorario nuevoEstado) {
        try {
            Horario horario = horarioService.findById(horarioId).orElse(null);
            if (horario != null) {
                horario.setEstado(nuevoEstado);
                horario.setFechaActualizacion(LocalDateTime.now());
                horarioService.save(horario);
                log.info("Estado del horario {} actualizado a: {}", horarioId, nuevoEstado);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error al actualizar estado del horario: {}", horarioId, e);
            return false;
        }
    }
}
