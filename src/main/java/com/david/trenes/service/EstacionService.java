package com.david.trenes.service;

import com.david.trenes.model.Estacion;
import com.david.trenes.repository.EstacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EstacionService {
    
    private final EstacionRepository estacionRepository;
    
    public List<Estacion> findAll() {
        log.debug("Buscando todas las estaciones");
        return estacionRepository.findAll();
    }
    
    public Optional<Estacion> findById(String id) {
        log.debug("Buscando estación por ID: {}", id);
        return estacionRepository.findById(id);
    }
    
    public Optional<Estacion> findByCodigoEstacion(String codigoEstacion) {
        log.debug("Buscando estación por código: {}", codigoEstacion);
        return estacionRepository.findByCodigoEstacion(codigoEstacion);
    }
    
    public List<Estacion> findByNombreContaining(String nombre) {
        log.debug("Buscando estaciones por nombre: {}", nombre);
        return estacionRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<Estacion> findByCiudad(String ciudad) {
        log.debug("Buscando estaciones por ciudad: {}", ciudad);
        return estacionRepository.findByCiudadContainingIgnoreCase(ciudad);
    }
    
    public List<Estacion> findByProvincia(String provincia) {
        log.debug("Buscando estaciones por provincia: {}", provincia);
        return estacionRepository.findByProvinciaContainingIgnoreCase(provincia);
    }
    
    public List<Estacion> findByTipoEstacion(Estacion.TipoEstacion tipoEstacion) {
        log.debug("Buscando estaciones por tipo: {}", tipoEstacion);
        return estacionRepository.findByTipoEstacion(tipoEstacion);
    }
    
    public List<Estacion> findByCategoria(Estacion.CategoriaEstacion categoria) {
        log.debug("Buscando estaciones por categoría: {}", categoria);
        return estacionRepository.findByCategoria(categoria);
    }
    
    public List<Estacion> findEstacionesActivas() {
        log.debug("Buscando estaciones activas");
        return estacionRepository.findByActivoTrue();
    }
    
    public List<Estacion> findEstacionesAccesibles() {
        log.debug("Buscando estaciones accesibles");
        return estacionRepository.findByAccesibilidadTrue();
    }
    
    public List<Estacion> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax) {
        log.debug("Buscando estaciones en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        return estacionRepository.findByCoordenadasRango(latMin, latMax, lonMin, lonMax);
    }
    
    public List<Estacion> findByCapacidadEstacionamientoMinima(Integer capacidad) {
        log.debug("Buscando estaciones con capacidad de estacionamiento mínima: {}", capacidad);
        return estacionRepository.findByCapacidadEstacionamientoMinima(capacidad);
    }
    
    public List<Estacion> findByServicioDisponible(Estacion.TipoServicio tipoServicio) {
        log.debug("Buscando estaciones con servicio disponible: {}", tipoServicio);
        return estacionRepository.findByServicioDisponible(tipoServicio);
    }
    
    public List<Estacion> findByViaConectada(List<String> viaIds) {
        log.debug("Buscando estaciones conectadas a vías: {}", viaIds);
        return estacionRepository.findByViaConectada(viaIds);
    }
    
    public List<Estacion> findByAndenTipoDisponible(Estacion.TipoAnden tipoAnden) {
        log.debug("Buscando estaciones con andenes tipo {} disponibles", tipoAnden);
        return estacionRepository.findByAndenTipoDisponible(tipoAnden);
    }
    
    public Estacion save(Estacion estacion) {
        log.info("Guardando estación: {}", estacion.getCodigoEstacion());
        
        if (estacion.getFechaCreacion() == null) {
            estacion.setFechaCreacion(java.time.LocalDateTime.now());
        }
        estacion.setFechaActualizacion(java.time.LocalDateTime.now());
        
        return estacionRepository.save(estacion);
    }
    
    public Estacion update(String id, Estacion estacionActualizada) {
        log.info("Actualizando estación con ID: {}", id);
        
        return estacionRepository.findById(id)
            .map(estacionExistente -> {
                estacionActualizada.setId(id);
                estacionActualizada.setFechaCreacion(estacionExistente.getFechaCreacion());
                estacionActualizada.setFechaActualizacion(java.time.LocalDateTime.now());
                return estacionRepository.save(estacionActualizada);
            })
            .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));
    }
    
    public void deleteById(String id) {
        log.info("Eliminando estación con ID: {}", id);
        
        if (!estacionRepository.existsById(id)) {
            throw new RuntimeException("Estación no encontrada con ID: " + id);
        }
        
        estacionRepository.deleteById(id);
    }
    
    public boolean existsByCodigoEstacion(String codigoEstacion) {
        return estacionRepository.existsByCodigoEstacion(codigoEstacion);
    }
    
    public long countByTipoEstacion(Estacion.TipoEstacion tipoEstacion) {
        return estacionRepository.countByTipoEstacion(tipoEstacion);
    }
    
    public long countByCategoria(Estacion.CategoriaEstacion categoria) {
        return estacionRepository.countByCategoria(categoria);
    }
    
    public long countByCiudad(String ciudad) {
        return estacionRepository.countByCiudad(ciudad);
    }
    
    public long countActivas() {
        return estacionRepository.countActivas();
    }
    
    public long countAccesibles() {
        return estacionRepository.countAccesibles();
    }
    
    public Estacion cambiarEstado(String id, boolean activo) {
        log.info("Cambiando estado de estación {} a activo: {}", id, activo);
        
        return estacionRepository.findById(id)
            .map(estacion -> {
                estacion.setActivo(activo);
                estacion.setFechaActualizacion(java.time.LocalDateTime.now());
                return estacionRepository.save(estacion);
            })
            .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));
    }
    
    public Estacion actualizarPersonal(String id, Integer personalActivo) {
        log.info("Actualizando personal activo de estación {}: {}", id, personalActivo);
        
        return estacionRepository.findById(id)
            .map(estacion -> {
                estacion.setPersonalActivo(personalActivo);
                estacion.setFechaActualizacion(java.time.LocalDateTime.now());
                return estacionRepository.save(estacion);
            })
            .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));
    }
    
    public Estacion asignarSupervisor(String id, String supervisorId) {
        log.info("Asignando supervisor {} a estación: {}", supervisorId, id);
        
        return estacionRepository.findById(id)
            .map(estacion -> {
                estacion.setSupervisorId(supervisorId);
                estacion.setFechaActualizacion(java.time.LocalDateTime.now());
                return estacionRepository.save(estacion);
            })
            .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));
    }
    
    public List<Estacion> findByNumeroAndenes(Integer numeroAndenes) {
        log.debug("Buscando estaciones con {} andenes", numeroAndenes);
        return estacionRepository.findByNumeroAndenes(numeroAndenes);
    }
    
    public List<Estacion> findBySupervisorId(String supervisorId) {
        log.debug("Buscando estaciones supervisadas por: {}", supervisorId);
        return estacionRepository.findBySupervisorId(supervisorId);
    }
}
