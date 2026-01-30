package com.david.trenes.service;

import com.david.trenes.model.Tren;
import com.david.trenes.model.Via;
import com.david.trenes.repository.TrenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrenService {
    
    private final TrenRepository trenRepository;
    
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
}
