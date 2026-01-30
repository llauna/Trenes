package com.david.trenes.service;

import com.david.trenes.dto.TrenPosicionResponse;
import com.david.trenes.model.Ruta;
import com.david.trenes.model.Tren;
import com.david.trenes.model.Via;
import com.david.trenes.repository.RutaRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.repository.ViaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrenService {
    
    private final TrenRepository trenRepository;
    private final RutaRepository rutaRepository;
    private final ViaRepository viaRepository;
    
    public List<Tren> findAll() {
        log.debug("Buscando todos los trenes");
        return trenRepository.findAll();
    }
    
    public Optional<Tren> findById(String id) {
        log.debug("Buscando tren por ID: {}", id);
        return trenRepository.findById(id);
    }
    
    public Optional<Tren> findByNumeroTren(String numeroTren) {
        log.debug("Buscando tren por número: {}", numeroTren);
        return trenRepository.findByNumeroTren(numeroTren);
    }
    
    public Optional<Tren> findByMatricula(String matricula) {
        log.debug("Buscando tren por matrícula: {}", matricula);
        return trenRepository.findByMatricula(matricula);
    }
    
    public List<Tren> findByTipoTren(Tren.TipoTren tipoTren) {
        log.debug("Buscando trenes por tipo: {}", tipoTren);
        return trenRepository.findByTipoTren(tipoTren);
    }
    
    public List<Tren> findByEstado(Tren.EstadoTren estadoActual) {
        log.debug("Buscando trenes por estado: {}", estadoActual);
        return trenRepository.findByEstadoActual(estadoActual);
    }
    
    public List<Tren> findTrenesActivos() {
        log.debug("Buscando trenes activos");
        return trenRepository.findByActivoTrue();
    }
    
    public List<Tren> findTrenesOperativos() {
        log.debug("Buscando trenes operativos");
        return trenRepository.findTrenesOperativos();
    }
    
    public List<Tren> findTrenesEnMarcha() {
        log.debug("Buscando trenes en marcha");
        return trenRepository.findTrenesEnMarcha();
    }
    
    public List<Tren> findTrenesEnEstacion() {
        log.debug("Buscando trenes en estación");
        return trenRepository.findTrenesEnEstacion();
    }
    
    public List<Tren> findTrenesConIncidenciasActivas() {
        log.debug("Buscando trenes con incidencias activas");
        return trenRepository.findTrenesConIncidenciasActivas();
    }
    
    public List<Tren> findByViaActual(String viaId) {
        log.debug("Buscando trenes en vía: {}", viaId);
        return trenRepository.findByViaActualId(viaId);
    }
    
    public List<Tren> findByRutaActual(String rutaId) {
        log.debug("Buscando trenes en ruta: {}", rutaId);
        return trenRepository.findByRutaActualId(rutaId);
    }
    
    public List<Tren> findByConductor(String conductorId) {
        log.debug("Buscando trenes asignados a conductor: {}", conductorId);
        return trenRepository.findByConductorActualId(conductorId);
    }
    
    public List<Tren> findByUbicacionRango(Double latMin, Double latMax, Double lonMin, Double lonMax) {
        log.debug("Buscando trenes en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        return trenRepository.findByUbicacionRango(latMin, latMax, lonMin, lonMax);
    }
    
    public List<Tren> findTrenesRequierenRevision(LocalDateTime fecha) {
        log.debug("Buscando trenes que requieren revisión antes de: {}", fecha);
        return trenRepository.findTrenesRequierenRevision(fecha);
    }
    
    public List<Tren> findByKilometrosDesdeUltimaRevisionMinimos(Integer kilometros) {
        log.debug("Buscando trenes con {} km desde última revisión", kilometros);
        return trenRepository.findByKilometrosDesdeUltimaRevisionMinimos(kilometros);
    }
    
    public List<Tren> findByCapacidadPasajerosMinima(Integer capacidadMinima) {
        log.debug("Buscando trenes con capacidad mínima de {} pasajeros", capacidadMinima);
        return trenRepository.findByCapacidadPasajerosMinima(capacidadMinima);
    }
    
    public List<Tren> findByCapacidadCargaMinima(Double capacidadMinima) {
        log.debug("Buscando trenes con capacidad mínima de carga: {} toneladas", capacidadMinima);
        return trenRepository.findByCapacidadCargaMinima(capacidadMinima);
    }
    
    public List<Tren> findByVelocidadMaximaMinima(Integer velocidadMinima) {
        log.debug("Buscando trenes con velocidad máxima mínima: {} km/h", velocidadMinima);
        return trenRepository.findByVelocidadMaximaMinima(velocidadMinima);
    }
    
    public List<Tren> findByFabricante(String fabricante) {
        log.debug("Buscando trenes del fabricante: {}", fabricante);
        return trenRepository.findByFabricante(fabricante);
    }
    
    public List<Tren> findByModelo(String modelo) {
        log.debug("Buscando trenes del modelo: {}", modelo);
        return trenRepository.findByModelo(modelo);
    }
    
    public Tren save(Tren tren) {
        log.info("Guardando tren: {}", tren.getNumeroTren());
        
        if (tren.getFechaCreacion() == null) {
            tren.setFechaCreacion(LocalDateTime.now());
        }
        tren.setFechaActualizacion(LocalDateTime.now());
        
        return trenRepository.save(tren);
    }
    
    public Tren update(String id, Tren trenActualizado) {
        log.info("Actualizando tren con ID: {}", id);
        
        return trenRepository.findById(id)
            .map(trenExistente -> {
                trenActualizado.setId(id);
                trenActualizado.setFechaCreacion(trenExistente.getFechaCreacion());
                trenActualizado.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(trenActualizado);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public void deleteById(String id) {
        log.info("Eliminando tren con ID: {}", id);
        
        if (!trenRepository.existsById(id)) {
            throw new RuntimeException("Tren no encontrado con ID: " + id);
        }
        
        trenRepository.deleteById(id);
    }
    
    public boolean existsByNumeroTren(String numeroTren) {
        return trenRepository.existsByNumeroTren(numeroTren);
    }
    
    public boolean existsByMatricula(String matricula) {
        return trenRepository.existsByMatricula(matricula);
    }
    
    public long countByTipoTren(Tren.TipoTren tipoTren) {
        return trenRepository.countByTipoTren(tipoTren);
    }
    
    public long countByEstado(Tren.EstadoTren estadoActual) {
        return trenRepository.countByEstadoActual(estadoActual);
    }
    
    public long countActivos() {
        return trenRepository.countByActivoTrue();
    }
    
    public Double sumKilometrajeTotal() {
        return trenRepository.sumKilometrajeTotal();
    }
    
    public Integer sumCapacidadTotalPasajeros() {
        return trenRepository.sumCapacidadTotalPasajeros();
    }
    
    public Tren cambiarEstado(String id, Tren.EstadoTren nuevoEstado) {
        log.info("Cambiando estado de tren {} a: {}", id, nuevoEstado);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setEstadoActual(nuevoEstado);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren asignarConductor(String id, String conductorId) {
        log.info("Asignando conductor {} a tren: {}", conductorId, id);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setConductorActualId(conductorId);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren actualizarUbicacion(String id, Via.Coordenada ubicacion, String viaId, Double kilometro) {
        log.info("Actualizando ubicación de tren {}: vía {}, km {}", id, viaId, kilometro);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setUbicacionActual(ubicacion);
                tren.setViaActualId(viaId);
                tren.setKilometroActual(kilometro);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren asignarRuta(String id, String rutaId) {
        log.info("Asignando ruta {} a tren: {}", rutaId, id);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setRutaActualId(rutaId);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren registrarKilometraje(String id, Double kilometrosAdicionales) {
        log.info("Registrando {} km adicionales al tren: {}", kilometrosAdicionales, id);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setKilometrajeTotal(tren.getKilometrajeTotal() + kilometrosAdicionales);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren programarRevision(String id, LocalDateTime fechaRevision) {
        log.info("Programando revisión para tren {} en: {}", id, fechaRevision);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setProximaRevision(fechaRevision);
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }
    
    public Tren completarRevision(String id) {
        log.info("Completando revisión para tren: {}", id);
        
        return trenRepository.findById(id)
            .map(tren -> {
                tren.setFechaUltimaRevision(LocalDateTime.now());
                tren.setKilometrosUltimaRevision(tren.getKilometrajeTotal());
                tren.setFechaActualizacion(LocalDateTime.now());
                return trenRepository.save(tren);
            })
            .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + id));
    }

    public Tren iniciarViaje(String trenId, String rutaId, Double velocidadCruceroKmh) {
        if (velocidadCruceroKmh == null || velocidadCruceroKmh <= 0) {
            throw new IllegalArgumentException("La velocidadCruceroKmh debe ser > 0");
        }

        Tren tren = trenRepository.findById(trenId)
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + trenId));

        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + rutaId));

        if (ruta.getActivo() == null || !ruta.getActivo()) {
            throw new IllegalStateException("La ruta no está activa");
        }
        if (ruta.getVias() == null || ruta.getVias().isEmpty()) {
            throw new IllegalStateException("La ruta no tiene vías asociadas");
        }

        if (tren.getVelocidadMaxima() != null) {
            velocidadCruceroKmh = Math.min(velocidadCruceroKmh, tren.getVelocidadMaxima().doubleValue());
        }

        tren.setRutaActualId(rutaId);
        tren.setEstadoActual(Tren.EstadoTren.EN_MARCHA);
        tren.setFechaInicioViaje(LocalDateTime.now());
        tren.setVelocidadCruceroKmh(velocidadCruceroKmh);

        // Estación actual = estación origen al arrancar
        tren.setEstacionActualId(ruta.getEstacionOrigenId());

        tren.setFechaActualizacion(LocalDateTime.now());

        tren.setViaActualId(null);
        tren.setKilometroActual(null);
        tren.setUbicacionActual(null);

        return trenRepository.save(tren);
    }

    @Transactional
    public TrenPosicionResponse getPosicionActual(String trenId) {
        Tren tren = trenRepository.findById(trenId)
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + trenId));

        // Si ya finalizó, devolvemos lo persistido (NO depende del reloj)
        if (tren.getEstadoActual() == Tren.EstadoTren.FINALIZADO) {
            Via.Coordenada u = tren.getUbicacionActual();

            Double lat = (u != null) ? u.getLatitud() : null;
            Double lon = (u != null) ? u.getLongitud() : null;
            Double alt = (u != null) ? u.getAltitud() : null;

            return TrenPosicionResponse.builder()
                    .trenId(tren.getId())
                    .rutaId(tren.getRutaActualId())
                    .viaId(tren.getViaActualId())
                    .estacionActualId(tren.getEstacionActualId())
                    // como al finalizar estacionActualId == destino, sirve como destino también
                    .estacionDestinoId(tren.getEstacionActualId())
                    .kilometroEnVia(tren.getKilometroActual())
                    .latitud(lat != null ? lat : 0.0)
                    .longitud(lon != null ? lon : 0.0)
                    .altitud(alt != null ? alt : 0.0)
                    .velocidadKmh(0.0)
                    .segundosDesdeInicio(0L)
                    .distanciaTotalRecorridaKm(0.0)
                    .build();
        }

        if (tren.getRutaActualId() == null) {
            throw new IllegalStateException("El tren no tiene ruta asignada");
        }
        if (tren.getFechaInicioViaje() == null || tren.getVelocidadCruceroKmh() == null) {
            throw new IllegalStateException("El tren no tiene viaje iniciado (fechaInicioViaje/velocidadCruceroKmh)");
        }

        Ruta ruta = rutaRepository.findById(tren.getRutaActualId())
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + tren.getRutaActualId()));

        List<Ruta.ViaRuta> viasRuta = ruta.getVias().stream()
                .sorted(Comparator.comparing(Ruta.ViaRuta::getOrden, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        long segundos = Math.max(0, Duration.between(tren.getFechaInicioViaje(), LocalDateTime.now()).getSeconds());
        double horas = segundos / 3600.0;
        double distanciaRecorridaKm = tren.getVelocidadCruceroKmh() * horas;

        double longitudTotalRutaKm = 0.0;
        for (Ruta.ViaRuta vr : viasRuta) {
            Via viaTmp = viaRepository.findById(vr.getViaId())
                    .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + vr.getViaId()));
            if (viaTmp.getLongitudKm() != null && viaTmp.getLongitudKm() > 0) {
                longitudTotalRutaKm += viaTmp.getLongitudKm();
            }
        }

        boolean haFinalizado = longitudTotalRutaKm > 0 && distanciaRecorridaKm >= longitudTotalRutaKm;

        double acumulado = 0.0;
        Ruta.ViaRuta viaRutaActual = null;
        double kmEnVia = 0.0;

        for (Ruta.ViaRuta vr : viasRuta) {
            Via viaTmp = viaRepository.findById(vr.getViaId())
                    .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + vr.getViaId()));

            double longitudKmTmp = viaTmp.getLongitudKm() != null ? viaTmp.getLongitudKm() : 0.0;
            if (longitudKmTmp <= 0) continue;

            if (distanciaRecorridaKm <= acumulado + longitudKmTmp) {
                viaRutaActual = vr;
                kmEnVia = distanciaRecorridaKm - acumulado;
                break;
            }
            acumulado += longitudKmTmp;
        }

        if (viaRutaActual == null) {
            Ruta.ViaRuta ultima = viasRuta.get(viasRuta.size() - 1);
            final String ultimaViaId = ultima.getViaId();

            Via ultimaVia = viaRepository.findById(ultimaViaId)
                    .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + ultimaViaId));

            double longitudKmTmp = ultimaVia.getLongitudKm() != null ? ultimaVia.getLongitudKm() : 0.0;
            viaRutaActual = ultima;
            kmEnVia = Math.max(0.0, longitudKmTmp);
        }

        final String viaIdActual = viaRutaActual.getViaId();

        Via via = viaRepository.findById(viaIdActual)
                .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + viaIdActual));

        Via.Coordenada inicio = via.getCoordenadaInicio();
        Via.Coordenada fin = via.getCoordenadaFin();

        double longitudKm = via.getLongitudKm() != null ? via.getLongitudKm() : 0.0;
        double t = (longitudKm > 0) ? Math.min(1.0, Math.max(0.0, kmEnVia / longitudKm)) : 0.0;

        double lat = lerp(inicio != null ? inicio.getLatitud() : null, fin != null ? fin.getLatitud() : null, t);
        double lon = lerp(inicio != null ? inicio.getLongitud() : null, fin != null ? fin.getLongitud() : null, t);
        double alt = lerp(inicio != null ? inicio.getAltitud() : null, fin != null ? fin.getAltitud() : null, t);

        if (haFinalizado) {
            log.info("Tren {} ha llegado al final de la ruta {} -> FINALIZADO", trenId, tren.getRutaActualId());

            tren.setEstadoActual(Tren.EstadoTren.FINALIZADO);
            tren.setEstacionActualId(ruta.getEstacionDestinoId());

            tren.setViaActualId(via.getId());
            tren.setKilometroActual(longitudKm);

            Via.Coordenada finReal = (fin != null)
                    ? fin
                    : Via.Coordenada.builder().latitud(lat).longitud(lon).altitud(alt).build();

            tren.setUbicacionActual(finReal);
            tren.setFechaActualizacion(LocalDateTime.now());

            trenRepository.save(tren);

            // devolver clavado al final
            kmEnVia = longitudKm;
            lat = finReal.getLatitud() != null ? finReal.getLatitud() : lat;
            lon = finReal.getLongitud() != null ? finReal.getLongitud() : lon;
            alt = finReal.getAltitud() != null ? finReal.getAltitud() : alt;

            return TrenPosicionResponse.builder()
                    .trenId(tren.getId())
                    .rutaId(tren.getRutaActualId())
                    .viaId(via.getId())
                    .estacionActualId(tren.getEstacionActualId())
                    .estacionDestinoId(ruta.getEstacionDestinoId())
                    .kilometroEnVia(kmEnVia)
                    .latitud(lat)
                    .longitud(lon)
                    .altitud(alt)
                    .velocidadKmh(0.0)
                    .segundosDesdeInicio(segundos)
                    .distanciaTotalRecorridaKm(distanciaRecorridaKm)
                    .build();
        }

        return TrenPosicionResponse.builder()
                .trenId(tren.getId())
                .rutaId(tren.getRutaActualId())
                .viaId(via.getId())
                .estacionActualId(tren.getEstacionActualId())
                .estacionDestinoId(ruta.getEstacionDestinoId())
                .kilometroEnVia(kmEnVia)
                .latitud(lat)
                .longitud(lon)
                .altitud(alt)
                .velocidadKmh(tren.getVelocidadCruceroKmh())
                .segundosDesdeInicio(segundos)
                .distanciaTotalRecorridaKm(distanciaRecorridaKm)
                .build();
    }

    private double lerp(Double a, Double b, double t) {
        if (a == null && b == null) return 0.0;
        if (a == null) return b;
        if (b == null) return a;
        return a + (b - a) * t;
    }
}
