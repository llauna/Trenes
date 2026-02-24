package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Ruta;
import com.david.trenes.model.Tren;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestionHorariosService {

    private final HorarioService horarioService;
    private final RutaService rutaService;
    private final TrenService trenService;

    // Índices para rotación de trenes por tipo
    private final java.util.Map<Tren.TipoTren, Integer> indicesRotacion = new java.util.concurrent.ConcurrentHashMap<>();

    // Margen entre servicios para el mismo tren (limpieza, cambio de tripulación, maniobras, etc.)
    private static final java.time.Duration MARGEN_ENTRE_SERVICIOS = java.time.Duration.ofMinutes(15);

    public void crearHorariosProgramados() {
        log.info("Creando horarios programados para el sistema");

        List<Ruta> rutasActivas = rutaService.findByEstado(Ruta.EstadoRuta.ACTIVA);
        if (rutasActivas == null || rutasActivas.isEmpty()) {
            // fallback por si tu data está usando "activo=true" y el estado no cuadra
            rutasActivas = rutaService.findRutasActivas();
        }

        log.info("Se encontraron {} rutas a procesar", rutasActivas == null ? 0 : rutasActivas.size());

        if (rutasActivas == null) {
            return;
        }

        for (Ruta ruta : rutasActivas) {
            log.info("Procesando ruta: {} - {}", ruta.getCodigoRuta(), ruta.getNombre());
            crearHorariosParaRuta(ruta);
        }

        log.info("Horarios programados creados (proceso finalizado)");
    }

    private void crearHorariosParaRuta(Ruta ruta) {
        if (ruta.getFrecuencia() == null) {
            log.warn("La ruta {} no tiene configuración de frecuencia", ruta.getCodigoRuta());
            return;
        }

        LocalDateTime fechaBase = LocalDateTime.now().toLocalDate().atStartOfDay();

        for (int dia = 0; dia < 7; dia++) {
            LocalDateTime fechaActual = fechaBase.plusDays(dia);

            if (debeCrearHorarioParaDia(fechaActual, ruta.getFrecuencia())) {
                crearHorariosDiarios(ruta, fechaActual);
            }
        }
    }

    private void crearHorariosDiarios(Ruta ruta, LocalDateTime fecha) {
        log.info("Creando horarios diarios para ruta: {} en fecha: {}", ruta.getCodigoRuta(), fecha.toLocalDate());

        if (ruta.getFrecuencia() == null) {
            log.warn("La ruta {} no tiene configuración de frecuencia", ruta.getCodigoRuta());
            return;
        }

        LocalTime horaPrimeraSalida = LocalTime.parse(ruta.getFrecuencia().getHoraPrimeraSalida());
        LocalTime horaUltimaSalida = LocalTime.parse(ruta.getFrecuencia().getHoraUltimaSalida());
        int intervaloMinutos = ruta.getFrecuencia().getIntervaloMinutos();
        int serviciosDia = ruta.getFrecuencia().getServiciosDia();

        log.info("Configuración ruta {} - Servicios día: {}, Intervalo: {} min, Hora inicio: {}, Hora fin: {}",
                ruta.getCodigoRuta(), serviciosDia, intervaloMinutos, horaPrimeraSalida, horaUltimaSalida);

        for (int servicio = 0; servicio < serviciosDia; servicio++) {
            LocalTime horaSalida = horaPrimeraSalida.plusMinutes((long) servicio * intervaloMinutos);

            if (horaSalida.isAfter(horaUltimaSalida)) {
                break;
            }

            LocalDateTime fechaSalida = fecha.with(horaSalida);
            LocalDateTime fechaLlegada = fechaSalida.plusMinutes(ruta.getTiempoEstimadoMinutos());

            Tren.TipoTren tipoTrenRequerido = obtenerTipoTrenParaRuta(ruta.getTipoRuta());
            log.info("Tipo de tren requerido para ruta {}: {}", ruta.getCodigoRuta(), tipoTrenRequerido);

            Tren trenAsignado = obtenerTrenRotativo(tipoTrenRequerido, fechaSalida, fechaLlegada);

            if (trenAsignado == null) {
                log.warn("No hay trenes disponibles para la ruta {} en el horario {} (ocupados por solape)",
                        ruta.getCodigoRuta(), fechaSalida);
                continue;
            }

            Horario horario = crearHorario(ruta, trenAsignado, fechaSalida, fechaLlegada, servicio + 1);
            horarioService.save(horario);

            log.info("Horario creado: {} - {} a {}", horario.getCodigoServicio(), horario.getFechaSalida(), horario.getFechaLlegada());
        }
    }

    public Map<String, Object> iniciarServicioPorHorario(String horarioId, Double velocidadCruceroKmh, boolean force) {
        if (horarioId == null || horarioId.isBlank()) {
            throw new IllegalArgumentException("horarioId es obligatorio");
        }

        Horario horario = horarioService.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + horarioId));

        if (horario.getActivo() == null || !horario.getActivo()) {
            throw new IllegalStateException("El horario no está activo");
        }

        if (horario.getEstado() != Horario.EstadoHorario.PROGRAMADO && horario.getEstado() != Horario.EstadoHorario.RETRASADO) {
            throw new IllegalStateException("El horario debe estar PROGRAMADO o RETRASADO para iniciar. Estado actual=" + horario.getEstado());
        }

        LocalDateTime ahora = LocalDateTime.now();

        // Regla de negocio típica: solo iniciar si estamos dentro de la ventana del servicio (o force=true)
        boolean dentroVentana = !ahora.isBefore(horario.getFechaSalida()) && ahora.isBefore(horario.getFechaLlegada());
        if (!force && !dentroVentana) {
            throw new IllegalStateException("Fuera de ventana: ahora=" + ahora
                    + ", salida=" + horario.getFechaSalida()
                    + ", llegada=" + horario.getFechaLlegada()
                    + ". Usa force=true si quieres arrancarlo igualmente.");
        }

        Tren tren = trenService.findById(horario.getTrenId())
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + horario.getTrenId()));

        // 1) Actualizar horario
        horario.setEstado(Horario.EstadoHorario.EN_MARCHA);
        horario.setFechaActualizacion(ahora);
        horarioService.save(horario);

        // 2) Sincronizar tren con el horario
        tren.setEstadoActual(Tren.EstadoTren.EN_MARCHA);
        tren.setRutaActualId(horario.getRutaId());
        tren.setEstacionActualId(horario.getEstacionOrigenId());

        // Opcional: si vas a usar estos campos para simulación/telemetría
        tren.setFechaInicioViaje(ahora);
        if (velocidadCruceroKmh != null && velocidadCruceroKmh > 0) {
            if (tren.getVelocidadMaxima() != null) {
                velocidadCruceroKmh = Math.min(velocidadCruceroKmh, tren.getVelocidadMaxima().doubleValue());
            }
            tren.setVelocidadCruceroKmh(velocidadCruceroKmh);
        }

        tren.setFechaActualizacion(ahora);
        trenService.save(tren);

        log.warn("INICIO SERVICIO: horario={} -> EN_MARCHA, tren={} -> EN_MARCHA", horario.getCodigoServicio(), tren.getNumeroTren());

        return Map.of(
                "timestamp", ahora,
                "horarioId", horario.getId(),
                "codigoServicio", horario.getCodigoServicio(),
                "trenId", tren.getId(),
                "numeroTren", tren.getNumeroTren(),
                "rutaId", horario.getRutaId(),
                "estadoHorario", horario.getEstado().name(),
                "estadoTren", tren.getEstadoActual().name()
        );
    }

    public Map<String, Object> obtenerCandidatosInicioHorarios(
            int minutosAntes,
            int minutosDespues,
            int max,
            boolean incluirRetrasados
    ) {
        LocalDateTime ahora = LocalDateTime.now();

        int antes = Math.max(0, minutosAntes);
        int despues = Math.max(0, minutosDespues);
        int limite = (max <= 0) ? 50 : Math.min(max, 500);

        LocalDateTime desde = ahora.minusMinutes(antes);
        LocalDateTime hasta = ahora.plusMinutes(despues);

        // Reutilizamos lo que ya existe en HorarioService/Repository
        List<Horario> enVentana = horarioService.findByFechaSalidaBetween(desde, hasta);

        List<Map<String, Object>> candidatos = enVentana.stream()
                .filter(h -> h != null)
                .filter(h -> h.getActivo() != null && h.getActivo())
                .filter(h -> h.getTrenId() != null && !h.getTrenId().isBlank())
                .filter(h -> {
                    if (h.getEstado() == Horario.EstadoHorario.PROGRAMADO) return true;
                    return incluirRetrasados && h.getEstado() == Horario.EstadoHorario.RETRASADO;
                })
                .sorted(Comparator.comparing(Horario::getFechaSalida))
                .limit(limite)
                .map(h -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("horarioId", h.getId());
                    m.put("codigoServicio", h.getCodigoServicio());
                    m.put("estado", h.getEstado() != null ? h.getEstado().name() : null);
                    m.put("activo", h.getActivo());
                    m.put("trenId", h.getTrenId());
                    m.put("rutaId", h.getRutaId());
                    m.put("estacionOrigenId", h.getEstacionOrigenId());
                    m.put("estacionDestinoId", h.getEstacionDestinoId());
                    m.put("fechaSalida", h.getFechaSalida());
                    m.put("fechaLlegada", h.getFechaLlegada());
                    return m;
                })
                .toList();

        return Map.of(
                "timestamp", ahora,
                "ventana", Map.of(
                        "desde", desde,
                        "hasta", hasta,
                        "minutosAntes", antes,
                        "minutosDespues", despues
                ),
                "max", limite,
                "incluyeRetrasados", incluirRetrasados,
                "totalEnVentana", enVentana.size(),
                "candidatos", candidatos
        );
    }


    private Tren obtenerTrenRotativo(Tren.TipoTren tipoTren, LocalDateTime salida, LocalDateTime llegada) {
        log.info("Buscando tren rotativo tipo: {} para intervalo: {} -> {}", tipoTren, salida, llegada);

        List<Tren> trenesDelTipo = trenService.findByTipoTren(tipoTren);
        log.info("Se encontraron {} trenes del tipo {}", trenesDelTipo.size(), tipoTren);

        List<Tren> trenesDisponibles = trenesDelTipo.stream()
                .filter(tren -> tren.getEstadoActual() == Tren.EstadoTren.DETENIDO)
                .filter(tren -> !trenTieneSolape(tren.getId(), salida, llegada))
                .toList();

        log.info("Trenes disponibles después de filtrar por solape: {}", trenesDisponibles.size());

        if (trenesDisponibles.isEmpty()) {
            return null;
        }

        int indiceActual = indicesRotacion.getOrDefault(tipoTren, 0);
        Tren trenSeleccionado = trenesDisponibles.get(indiceActual % trenesDisponibles.size());
        indicesRotacion.put(tipoTren, (indiceActual + 1) % trenesDisponibles.size());

        log.info("Tren asignado rotativamente: {} (tipo: {}, índice: {})",
                trenSeleccionado.getNumeroTren(), tipoTren, indiceActual);

        return trenSeleccionado;
    }

    private boolean trenTieneSolape(String trenId, LocalDateTime nuevaSalida, LocalDateTime nuevaLlegada) {
        List<Horario> horariosDelTren = horarioService.findByTrenId(trenId);

        LocalDateTime nuevaSalidaConMargen = nuevaSalida.minus(MARGEN_ENTRE_SERVICIOS);
        LocalDateTime nuevaLlegadaConMargen = nuevaLlegada.plus(MARGEN_ENTRE_SERVICIOS);

        return horariosDelTren.stream()
                .filter(h -> h.getEstado() == Horario.EstadoHorario.PROGRAMADO || h.getEstado() == Horario.EstadoHorario.EN_MARCHA)
                .anyMatch(h -> {
                    LocalDateTime existenteSalida = h.getFechaSalida();
                    LocalDateTime existenteLlegada = h.getFechaLlegada();

                    LocalDateTime existenteSalidaConMargen = existenteSalida.minus(MARGEN_ENTRE_SERVICIOS);
                    LocalDateTime existenteLlegadaConMargen = existenteLlegada.plus(MARGEN_ENTRE_SERVICIOS);

                    return nuevaSalidaConMargen.isBefore(existenteLlegadaConMargen)
                            && existenteSalidaConMargen.isBefore(nuevaLlegadaConMargen);
                });
    }

    private boolean debeCrearHorarioParaDia(LocalDateTime fecha, Ruta.FrecuenciaRuta frecuencia) {
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1 = Lunes, 7 = Domingo

        return switch (diaSemana) {
            case 1 -> frecuencia.getLunes();
            case 2 -> frecuencia.getMartes();
            case 3 -> frecuencia.getMiercoles();
            case 4 -> frecuencia.getJueves();
            case 5 -> frecuencia.getViernes();
            case 6 -> frecuencia.getSabado();
            case 7 -> frecuencia.getDomingo();
            default -> false;
        };
    }

    private Tren.TipoTren obtenerTipoTrenParaRuta(Ruta.TipoRuta tipoRuta) {
        return switch (tipoRuta) {
            case EXPRESO, INTERNACIONAL -> Tren.TipoTren.ALTA_VELOCIDAD;
            case REGIONAL -> Tren.TipoTren.REGIONAL;
            case CERCANIAS -> Tren.TipoTren.CERCANIAS;
            case CARGA -> Tren.TipoTren.CARGA;
            case TURISTICO -> Tren.TipoTren.TURISTICO;
            case ESPECIAL -> Tren.TipoTren.ESPECIAL;
            default -> Tren.TipoTren.REGIONAL;
        };
    }

    private Horario crearHorario(Ruta ruta, Tren tren, LocalDateTime fechaSalida, LocalDateTime fechaLlegada, int numeroServicio) {
        log.info("Creando horario para ruta: {} con tren: {}", ruta.getCodigoRuta(), tren.getNumeroTren());

        List<Ruta.ParadaRuta> estacionesIntermedias = ruta.getEstacionesIntermedias();
        if (estacionesIntermedias == null) {
            estacionesIntermedias = new ArrayList<>();
            log.warn("La ruta {} no tiene estaciones intermedias, creando lista vacía", ruta.getCodigoRuta());
        }
        log.info("Estaciones intermedias en ruta: {}", estacionesIntermedias.size());

        // 1) Normalizar paradas: ORIGEN + INTERMEDIAS + DESTINO, sin duplicados
        String origenId = ruta.getEstacionOrigenId();
        String destinoId = ruta.getEstacionDestinoId();

        if (origenId == null || origenId.isBlank() || destinoId == null || destinoId.isBlank()) {
            log.warn("Ruta {} inconsistente: estacionOrigenId/estacionDestinoId vacíos", ruta.getCodigoRuta());
        }

        Map<String, Ruta.ParadaRuta> paradasPorEstacion = new LinkedHashMap<>();

        // ORIGEN (si existe)
        if (origenId != null && !origenId.isBlank()) {
            paradasPorEstacion.put(origenId, Ruta.ParadaRuta.builder()
                    .estacionId(origenId)
                    .nombreEstacion("Origen")
                    .orden(1)
                    .kilometro(0.0)
                    .tiempoParadaMinutos(0)
                    .obligatoria(true)
                    .build());
        }

        // INTERMEDIAS (ordenadas, excluyendo origen/destino y evitando duplicados)
        estacionesIntermedias.stream()
                .filter(p -> p != null && p.getEstacionId() != null && !p.getEstacionId().isBlank())
                .sorted(Comparator.comparing(p -> p.getOrden() == null ? Integer.MAX_VALUE : p.getOrden()))
                .forEach(p -> {
                    String estId = p.getEstacionId();
                    if (estId.equals(origenId) || estId.equals(destinoId)) return;
                    paradasPorEstacion.putIfAbsent(estId, p);
                });

        // DESTINO (si existe)
        if (destinoId != null && !destinoId.isBlank()) {
            paradasPorEstacion.put(destinoId, Ruta.ParadaRuta.builder()
                    .estacionId(destinoId)
                    .nombreEstacion("Destino")
                    .orden(9999)
                    .kilometro(ruta.getDistanciaTotalKm() == null ? 0.0 : ruta.getDistanciaTotalKm())
                    .tiempoParadaMinutos(0)
                    .obligatoria(true)
                    .build());
        }

        List<Ruta.ParadaRuta> paradasRutaNormalizadas = new ArrayList<>(paradasPorEstacion.values());

        // Reasignar orden 1..N
        for (int i = 0; i < paradasRutaNormalizadas.size(); i++) {
            Ruta.ParadaRuta p = paradasRutaNormalizadas.get(i);
            p.setOrden(i + 1);
        }

        if (paradasRutaNormalizadas.size() >= 2) {
            String firstId = paradasRutaNormalizadas.get(0).getEstacionId();
            String lastId = paradasRutaNormalizadas.get(paradasRutaNormalizadas.size() - 1).getEstacionId();

            if (!firstId.equals(origenId) || !lastId.equals(destinoId)) {
                log.warn("Ruta {}: paradas normalizadas no cuadran con cabecera (origen={}, destino={}, first={}, last={})",
                        ruta.getCodigoRuta(), origenId, destinoId, firstId, lastId);
            }
        }

        // 2) Construir ParadaHorario con reglas correctas para primera/última
        List<Horario.ParadaHorario> paradasHorario = new ArrayList<>();

        for (int i = 0; i < paradasRutaNormalizadas.size(); i++) {
            Ruta.ParadaRuta paradaRuta = paradasRutaNormalizadas.get(i);

            boolean esPrimera = (i == 0);
            boolean esUltima = (i == paradasRutaNormalizadas.size() - 1);

            LocalDateTime horaLlegadaParada = esPrimera ? null : fechaSalida.plusMinutes(
                    (long) (paradaRuta.getKilometro() / ruta.getDistanciaTotalKm() * ruta.getTiempoEstimadoMinutos())
            );

            LocalDateTime horaSalidaParada = esUltima ? null : (horaLlegadaParada == null ? fechaSalida : horaLlegadaParada)
                    .plusMinutes(paradaRuta.getTiempoParadaMinutos() == null ? 0 : paradaRuta.getTiempoParadaMinutos());

            Horario.ParadaHorario paradaHorario = Horario.ParadaHorario.builder()
                    .estacionId(paradaRuta.getEstacionId())
                    .nombreEstacion(paradaRuta.getNombreEstacion())
                    .orden(paradaRuta.getOrden())
                    .horaLlegadaProgramada(horaLlegadaParada)
                    .horaSalidaProgramada(horaSalidaParada)
                    .tiempoParadaMinutos(paradaRuta.getTiempoParadaMinutos())
                    .paradaObligatoria(paradaRuta.getObligatoria())
                    .andenAsignado(null)
                    .estado(Horario.EstadoParada.PENDIENTE)
                    .retrasoMinutos(0)
                    .build();

            paradasHorario.add(paradaHorario);
        }

        log.info("Paradas creadas (normalizadas): {}", paradasHorario.size());

        List<Horario.ClaseServicio> clases = crearClasesServicio(tren);
        log.info("Clases creadas: {}", clases.size());

        Horario horario = Horario.builder()
                .id(UUID.randomUUID().toString())
                .codigoServicio(generarCodigoServicio(ruta, fechaSalida, numeroServicio))
                .trenId(tren.getId())
                .rutaId(ruta.getId())
                .numeroServicio(String.format("%03d", numeroServicio))
                .tipoServicio(obtenerTipoServicio(ruta.getTipoRuta()))
                .fechaSalida(fechaSalida)
                .fechaLlegada(fechaLlegada)
                .estacionOrigenId(origenId)
                .estacionDestinoId(destinoId)
                .paradas(paradasHorario)
                .conductorId(null)
                .conductorSuplenteId(null)
                .estado(Horario.EstadoHorario.PROGRAMADO)
                .frecuencia(null)
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

        log.info("Horario creado con código: {}", horario.getCodigoServicio());
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

    private Tren obtenerTrenRotativo(Tren.TipoTren tipoTren, java.time.LocalDate fecha) {
        log.info("Buscando tren rotativo tipo: {} para fecha: {}", tipoTren, fecha);
        
        // Obtener todos los trenes del tipo requerido
        List<Tren> trenesDelTipo = trenService.findByTipoTren(tipoTren);
        log.info("Se encontraron {} trenes del tipo {}", trenesDelTipo.size(), tipoTren);
        
        // Filtrar trenes disponibles (DETENIDO y sin horario para este día)
        List<Tren> trenesDisponibles = trenesDelTipo.stream()
                .filter(tren -> {
                    boolean detenido = tren.getEstadoActual() == Tren.EstadoTren.DETENIDO;
                    boolean sinHorario = !trenTieneHorarioAsignadoParaDia(tren.getId(), fecha);
                    log.debug("Tren {} - Estado: {}, Sin horario: {}", tren.getNumeroTren(), tren.getEstadoActual(), sinHorario);
                    return detenido && sinHorario;
                })
                .collect(java.util.stream.Collectors.toList());
        
        log.info("Trenes disponibles después de filtrar: {}", trenesDisponibles.size());
        
        if (trenesDisponibles.isEmpty()) {
            log.warn("No hay trenes disponibles del tipo {} para la fecha {}", tipoTren, fecha);
            return null;
        }
        
        // Obtener índice de rotación para este tipo de tren
        int indiceActual = indicesRotacion.getOrDefault(tipoTren, 0);
        
        // Seleccionar tren de forma rotativa
        Tren trenSeleccionado = trenesDisponibles.get(indiceActual % trenesDisponibles.size());
        
        // Actualizar índice para próxima asignación
        indicesRotacion.put(tipoTren, (indiceActual + 1) % trenesDisponibles.size());
        
        log.info("Tren asignado rotativamente: {} (tipo: {}, índice: {})", 
                trenSeleccionado.getNumeroTren(), tipoTren, indiceActual);
        
        return trenSeleccionado;
    }

    private boolean trenTieneHorarioAsignadoParaDia(String trenId, java.time.LocalDate fecha) {
        List<Horario> horariosDelTren = horarioService.findByTrenId(trenId);
        
        return horariosDelTren.stream()
                .anyMatch(horario -> {
                    LocalDateTime fechaSalida = horario.getFechaSalida();
                    return fechaSalida.toLocalDate().equals(fecha) && 
                           (horario.getEstado() == Horario.EstadoHorario.PROGRAMADO ||
                            horario.getEstado() == Horario.EstadoHorario.EN_MARCHA);
                });
    }

    public List<Horario> obtenerHorariosPorRuta(String rutaId) {
        return horarioService.findByRutaId(rutaId);
    }

    public List<Horario> obtenerHorariosPorTren(String trenId) {
        return horarioService.findByTrenId(trenId);
    }

    public List<Horario> obtenerHorariosActivosPorTren(String trenId) {
        return horarioService.findByTrenIdActivos(trenId);
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

    public Map<String, Object> verificarConsistenciaParadasConRuta() {
        log.info("Iniciando verificación de consistencia entre paradas de horarios y rutas");
        
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> inconsistencias = new ArrayList<>();
        int horariosVerificados = 0;
        int horariosConsistentes = 0;
        
        List<Horario> todosHorarios = horarioService.findAll();
        
        for (Horario horario : todosHorarios) {
            horariosVerificados++;
            
            Map<String, Object> verificacionHorario = verificarConsistenciaHorarioRuta(horario);
            
            Boolean esConsistente = (Boolean) verificacionHorario.get("consistente");
            if (esConsistente) {
                horariosConsistentes++;
            } else {
                inconsistencias.add(verificacionHorario);
            }
        }
        
        resultado.put("horariosVerificados", horariosVerificados);
        resultado.put("horariosConsistentes", horariosConsistentes);
        resultado.put("horariosInconsistentes", horariosVerificados - horariosConsistentes);
        resultado.put("inconsistencias", inconsistencias);
        resultado.put("porcentajeConsistencia", horariosVerificados > 0 ? 
            (double) horariosConsistentes / horariosVerificados * 100 : 0.0);
        
        log.info("Verificación completada: {}/{} horarios consistentes ({:.2f}%)", 
            horariosConsistentes, horariosVerificados, 
            horariosVerificados > 0 ? (double) horariosConsistentes / horariosVerificados * 100 : 0.0);
        
        return resultado;
    }
    
    private Map<String, Object> verificarConsistenciaHorarioRuta(Horario horario) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("horarioId", horario.getId());
        resultado.put("codigoServicio", horario.getCodigoServicio());
        resultado.put("rutaId", horario.getRutaId());
        
        Optional<Ruta> rutaOpt = rutaService.findById(horario.getRutaId());
        if (rutaOpt.isEmpty()) {
            resultado.put("consistente", false);
            resultado.put("error", "Ruta no encontrada");
            return resultado;
        }
        
        Ruta ruta = rutaOpt.get();
        List<String> problemas = new ArrayList<>();
        
        // 1. Verificar origen y destino
        if (!horario.getEstacionOrigenId().equals(ruta.getEstacionOrigenId())) {
            problemas.add(String.format("Origen mismatch: horario=%s, ruta=%s", 
                horario.getEstacionOrigenId(), ruta.getEstacionOrigenId()));
        }
        
        if (!horario.getEstacionDestinoId().equals(ruta.getEstacionDestinoId())) {
            problemas.add(String.format("Destino mismatch: horario=%s, ruta=%s", 
                horario.getEstacionDestinoId(), ruta.getEstacionDestinoId()));
        }
        
        // 2. Construir lista esperada de paradas de la ruta
        List<String> paradasEsperadas = new ArrayList<>();
        
        // Origen
        if (ruta.getEstacionOrigenId() != null) {
            paradasEsperadas.add(ruta.getEstacionOrigenId());
        }
        
        // Intermedias (ordenadas)
        if (ruta.getEstacionesIntermedias() != null) {
            ruta.getEstacionesIntermedias().stream()
                .filter(p -> p != null && p.getEstacionId() != null)
                .sorted(Comparator.comparing(p -> p.getOrden() == null ? Integer.MAX_VALUE : p.getOrden()))
                .forEach(p -> {
                    String estId = p.getEstacionId();
                    if (!estId.equals(ruta.getEstacionOrigenId()) && 
                        !estId.equals(ruta.getEstacionDestinoId())) {
                        paradasEsperadas.add(estId);
                    }
                });
        }
        
        // Destino
        if (ruta.getEstacionDestinoId() != null) {
            paradasEsperadas.add(ruta.getEstacionDestinoId());
        }
        
        // 3. Comparar con paradas del horario
        List<String> paradasHorario = new ArrayList<>();
        if (horario.getParadas() != null) {
            paradasHorario = horario.getParadas().stream()
                .filter(p -> p != null && p.getEstacionId() != null)
                .sorted(Comparator.comparing(p -> p.getOrden() == null ? Integer.MAX_VALUE : p.getOrden()))
                .map(Horario.ParadaHorario::getEstacionId)
                .toList();
        }
        
        // 4. Verificar coincidencia exacta
        if (!paradasEsperadas.equals(paradasHorario)) {
            problemas.add(String.format("Paradas mismatch. Esperadas: %s, Encontradas: %s", 
                paradasEsperadas, paradasHorario));
        }
        
        // 5. Verificar orden y tiempos
        if (horario.getParadas() != null && !horario.getParadas().isEmpty()) {
            for (int i = 0; i < horario.getParadas().size(); i++) {
                Horario.ParadaHorario parada = horario.getParadas().get(i);
                
                // Verificar orden consecutivo
                if (!parada.getOrden().equals(i + 1)) {
                    problemas.add(String.format("Orden incorrecto en parada %s: esperado=%d, actual=%d", 
                        parada.getEstacionId(), i + 1, parada.getOrden()));
                }
                
                // Verificar lógica de tiempos (primera no debe tener llegada, última no debe tener salida)
                boolean esPrimera = (i == 0);
                boolean esUltima = (i == horario.getParadas().size() - 1);
                
                if (esPrimera && parada.getHoraLlegadaProgramada() != null) {
                    problemas.add(String.format("Primera parada %s no debe tener hora de llegada", 
                        parada.getEstacionId()));
                }
                
                if (esUltima && parada.getHoraSalidaProgramada() != null) {
                    problemas.add(String.format("Última parada %s no debe tener hora de salida", 
                        parada.getEstacionId()));
                }
                
                // Verificar secuencia temporal
                if (!esPrimera && i > 0) {
                    Horario.ParadaHorario paradaAnterior = horario.getParadas().get(i - 1);
                    if (paradaAnterior.getHoraSalidaProgramada() != null && 
                        parada.getHoraLlegadaProgramada() != null) {
                        if (parada.getHoraLlegadaProgramada().isBefore(paradaAnterior.getHoraSalidaProgramada())) {
                            problemas.add(String.format("Inconsistencia temporal: %s llega antes de que %s salga", 
                                parada.getEstacionId(), paradaAnterior.getEstacionId()));
                        }
                    }
                }
            }
        }
        
        resultado.put("consistente", problemas.isEmpty());
        resultado.put("problemas", problemas);
        resultado.put("paradasEsperadas", paradasEsperadas);
        resultado.put("paradasEncontradas", paradasHorario);
        
        return resultado;
    }

    public Map<String, Object> monitorizarEstadoTrenesTiempoReal() {
        log.info("Iniciando monitorización en tiempo real de todos los trenes");

        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> estadosTrenes = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // Obtener todos los trenes
        List<Tren> todosTrenes = trenService.findAll();

        int trenesEnMarcha = 0;
        int trenesDetenidos = 0;
        int trenesEnMantenimiento = 0;
        int incidentesDetectados = 0;

        Map<String, Integer> contadorPorEstado = new HashMap<>();

        for (Tren tren : todosTrenes) {
            Map<String, Object> estadoTren = analizarEstadoTrenActual(tren, ahora);
            estadosTrenes.add(estadoTren);

            String estado = (String) estadoTren.get("estadoOperativo");
            if (estado == null) estado = "DESCONOCIDO";

            contadorPorEstado.merge(estado, 1, Integer::sum);

            switch (estado) {
                case "EN_MARCHA" -> trenesEnMarcha++;
                case "DETENIDO" -> trenesDetenidos++;
                case "MANTENIMIENTO" -> trenesEnMantenimiento++;
                default -> { /* se cuenta en contadorPorEstado */ }
            }

            if (Boolean.TRUE.equals(estadoTren.get("tieneIncidentes"))) {
                incidentesDetectados++;
            }
        }

        resultado.put("timestamp", ahora);
        resultado.put("totalTrenes", todosTrenes.size());
        resultado.put("trenesEnMarcha", trenesEnMarcha);
        resultado.put("trenesDetenidos", trenesDetenidos);
        resultado.put("trenesEnMantenimiento", trenesEnMantenimiento);
        resultado.put("incidentesDetectados", incidentesDetectados);
        resultado.put("contadorPorEstado", contadorPorEstado);
        resultado.put("estadosTrenes", estadosTrenes);

        log.info("Monitorización completada: {} trenes totales, {} en marcha, {} incidentes",
                todosTrenes.size(), trenesEnMarcha, incidentesDetectados);

        return resultado;
    }
    
    private Map<String, Object> analizarEstadoTrenActual(Tren tren, LocalDateTime ahora) {
        Map<String, Object> estado = new HashMap<>();
        
        estado.put("trenId", tren.getId());
        estado.put("numeroTren", tren.getNumeroTren());
        estado.put("tipoTren", tren.getTipoTren());
        estado.put("estadoActual", tren.getEstadoActual());
        estado.put("capacidadPasajeros", tren.getCapacidadPasajeros());
        estado.put("ultimaActualizacion", tren.getFechaActualizacion());
        
        // Obtener horarios activos del tren
        List<Horario> horariosActivos = obtenerHorariosActivosPorTren(tren.getId());
        
        if (horariosActivos.isEmpty()) {
            estado.put("estadoOperativo", "SIN_SERVICIO");
            estado.put("posicionActual", "Depósito/Taller");
            estado.put("proximoServicio", null);
            estado.put("tieneIncidentes", false);
            estado.put("incidentes", new ArrayList<>());
            return estado;
        }
        
        // Buscar el horario actual o el más próximo
        Horario horarioActual = encontrarHorarioActual(horariosActivos, ahora);
        
        if (horarioActual == null) {
            // No hay horario en este momento, buscar el próximo
            horarioActual = encontrarProximoHorario(horariosActivos, ahora);
            if (horarioActual != null) {
                estado.put("estadoOperativo", "PREPARANDO_SALIDA");
                estado.put("posicionActual", "Estación origen: " + horarioActual.getEstacionOrigenId());
                estado.put("proximoServicio", horarioActual.getCodigoServicio());
                estado.put("horaSalida", horarioActual.getFechaSalida());
            } else {
                estado.put("estadoOperativo", "SERVICIO_COMPLETADO");
                estado.put("posicionActual", "Depósito");
                estado.put("proximoServicio", null);
            }
        } else {
            // El tren está en servicio
            estado.put("estadoOperativo", "EN_MARCHA");
            estado.put("horarioActual", horarioActual.getCodigoServicio());
            estado.put("rutaId", horarioActual.getRutaId());
            estado.put("estacionOrigen", horarioActual.getEstacionOrigenId());
            estado.put("estacionDestino", horarioActual.getEstacionDestinoId());
            estado.put("fechaSalida", horarioActual.getFechaSalida());
            estado.put("fechaLlegada", horarioActual.getFechaLlegada());
            
            // Determinar posición actual
            Map<String, Object> posicion = determinarPosicionActual(horarioActual, ahora);
            estado.put("posicionActual", posicion.get("descripcion"));
            estado.put("paradaActual", posicion.get("paradaActual"));
            estado.put("proximaParada", posicion.get("proximaParada"));
            estado.put("progresoPorcentaje", posicion.get("progresoPorcentaje"));
            estado.put("kilometrosRecorridos", posicion.get("kilometrosRecorridos"));
            estado.put("kilometrosTotales", posicion.get("kilometrosTotales"));
        }
        
        // Analizar incidentes
        List<Map<String, Object>> incidentes = analizarIncidentesTren(tren, horariosActivos, ahora);
        estado.put("tieneIncidentes", !incidentes.isEmpty());
        estado.put("incidentes", incidentes);
        
        return estado;
    }
    
    private Horario encontrarHorarioActual(List<Horario> horarios, LocalDateTime ahora) {
        return horarios.stream()
            .filter(h -> h.getFechaSalida().isBefore(ahora) && h.getFechaLlegada().isAfter(ahora))
            .filter(h -> h.getEstado() == Horario.EstadoHorario.EN_MARCHA || 
                        h.getEstado() == Horario.EstadoHorario.PROGRAMADO ||
                        h.getEstado() == Horario.EstadoHorario.RETRASADO)
            .findFirst()
            .orElse(null);
    }
    
    private Horario encontrarProximoHorario(List<Horario> horarios, LocalDateTime ahora) {
        return horarios.stream()
            .filter(h -> h.getFechaSalida().isAfter(ahora))
            .filter(h -> h.getEstado() == Horario.EstadoHorario.PROGRAMADO ||
                        h.getEstado() == Horario.EstadoHorario.RETRASADO)
            .min(Comparator.comparing(Horario::getFechaSalida))
            .orElse(null);
    }
    
    private Map<String, Object> determinarPosicionActual(Horario horario, LocalDateTime ahora) {
        Map<String, Object> posicion = new HashMap<>();
        
        if (horario.getParadas() == null || horario.getParadas().isEmpty()) {
            posicion.put("descripcion", "Ruta sin paradas definidas");
            posicion.put("paradaActual", null);
            posicion.put("proximaParada", null);
            posicion.put("progresoPorcentaje", 0.0);
            posicion.put("kilometrosRecorridos", 0.0);
            posicion.put("kilometrosTotales", 0.0);
            return posicion;
        }
        
        // Calcular progreso temporal
        LocalDateTime salida = horario.getFechaSalida();
        LocalDateTime llegada = horario.getFechaLlegada();
        long duracionTotal = java.time.Duration.between(salida, llegada).toMinutes();
        long tiempoTranscurrido = java.time.Duration.between(salida, ahora).toMinutes();
        
        double progresoTemporal = Math.max(0.0, Math.min(100.0, (double) tiempoTranscurrido / duracionTotal * 100));
        
        // Encontrar parada actual y próxima
        Horario.ParadaHorario paradaActual = null;
        Horario.ParadaHorario proximaParada = null;
        
        for (int i = 0; i < horario.getParadas().size(); i++) {
            Horario.ParadaHorario parada = horario.getParadas().get(i);
            
            if (parada.getHoraSalidaProgramada() != null && ahora.isBefore(parada.getHoraSalidaProgramada())) {
                proximaParada = parada;
                if (i > 0) {
                    paradaActual = horario.getParadas().get(i - 1);
                }
                break;
            }
            
            if (i == horario.getParadas().size() - 1) {
                paradaActual = parada;
                proximaParada = null;
            }
        }
        
        // Calcular distancia (asumiendo distribución uniforme)
        double distanciaTotal = 100.0; // km base, se puede ajustar según ruta real
        double kilometrosRecorridos = distanciaTotal * (progresoTemporal / 100.0);
        
        posicion.put("descripcion", paradaActual != null ? 
            String.format("Entre %s y %s", 
                paradaActual.getNombreEstacion(), 
                proximaParada != null ? proximaParada.getNombreEstacion() : "Destino final") :
            "En ruta hacia próxima parada");
        posicion.put("paradaActual", paradaActual);
        posicion.put("proximaParada", proximaParada);
        posicion.put("progresoPorcentaje", Math.round(progresoTemporal * 100.0) / 100.0);
        posicion.put("kilometrosRecorridos", Math.round(kilometrosRecorridos * 100.0) / 100.0);
        posicion.put("kilometrosTotales", distanciaTotal);
        
        return posicion;
    }
    
    private List<Map<String, Object>> analizarIncidentesTren(Tren tren, List<Horario> horarios, LocalDateTime ahora) {
        List<Map<String, Object>> incidentes = new ArrayList<>();
        
        // 1. Verificar retrasos
        for (Horario horario : horarios) {
            if (horario.getEstado() == Horario.EstadoHorario.RETRASADO) {
                Map<String, Object> incidente = new HashMap<>();
                incidente.put("tipo", "RETRASO");
                incidente.put("descripcion", "El tren presenta retraso en su servicio");
                incidente.put("horario", horario.getCodigoServicio());
                incidente.put("severidad", "MEDIA");
                incidentes.add(incidente);
            }
            
            // 2. Verificar paradas omitidas
            if (horario.getParadas() != null) {
                for (Horario.ParadaHorario parada : horario.getParadas()) {
                    if (parada.getEstado() == Horario.EstadoParada.OMITIDA) {
                        Map<String, Object> incidente = new HashMap<>();
                        incidente.put("tipo", "PARADA_OMITIDA");
                        incidente.put("descripcion", String.format("Parada omitida en %s", parada.getNombreEstacion()));
                        incidente.put("horario", horario.getCodigoServicio());
                        incidente.put("estacion", parada.getEstacionId());
                        incidente.put("severidad", "ALTA");
                        incidentes.add(incidente);
                    }
                }
            }
            
            // 3. Verificar si el tren está demasiado tiempo sin comunicación
            if (horario.getEstado() == Horario.EstadoHorario.EN_MARCHA) {
                LocalDateTime ultimaActualizacion = tren.getFechaActualizacion();
                if (ultimaActualizacion != null) {
                    long minutosSinActualizacion = java.time.Duration.between(ultimaActualizacion, ahora).toMinutes();
                    if (minutosSinActualizacion > 30) { // Más de 30 minutos sin actualización
                        Map<String, Object> incidente = new HashMap<>();
                        incidente.put("tipo", "SIN_COMUNICACION");
                        incidente.put("descripcion", String.format("Sin comunicación por %d minutos", minutosSinActualizacion));
                        incidente.put("horario", horario.getCodigoServicio());
                        incidente.put("severidad", "ALTA");
                        incidentes.add(incidente);
                    }
                }
            }
        }
        
        // 4. Verificar estado del tren
        if (tren.getEstadoActual() == Tren.EstadoTren.MANTENIMIENTO) {
            Map<String, Object> incidente = new HashMap<>();
            incidente.put("tipo", "MANTENIMIENTO");
            incidente.put("descripcion", "Tren actualmente en mantenimiento");
            incidente.put("severidad", "BAJA");
            incidentes.add(incidente);
        }
        
        return incidentes;
    }
}
