package com.david.trenes.service;

import com.david.trenes.model.Via;
import com.david.trenes.repository.ViaRepository;
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
public class ViaService {
    
    private final ViaRepository viaRepository;
    
    public List<Via> findAll() {
        log.debug("Buscando todas las vías");
        return viaRepository.findAll();
    }
    
    public Optional<Via> findById(String id) {
        log.debug("Buscando vía por ID: {}", id);
        return viaRepository.findById(id);
    }
    
    public Optional<Via> findByCodigoVia(String codigoVia) {
        log.debug("Buscando vía por código: {}", codigoVia);
        return viaRepository.findByCodigoVia(codigoVia);
    }
    
    public List<Via> findByEstacion(String estacionId) {
        log.debug("Buscando vías conectadas a estación: {}", estacionId);
        return viaRepository.findByEstacionOrigenIdOrEstacionDestinoId(estacionId, estacionId);
    }
    
    public List<Via> findByEstado(Via.EstadoVia estado) {
        log.debug("Buscando vías por estado: {}", estado);
        return viaRepository.findByEstado(estado);
    }
    
    public List<Via> findByTipoVia(Via.TipoVia tipoVia) {
        log.debug("Buscando vías por tipo: {}", tipoVia);
        return viaRepository.findByTipoVia(tipoVia);
    }
    
    public List<Via> findViasActivas() {
        log.debug("Buscando vías activas");
        return viaRepository.findViasActivas();
    }
    
    public List<Via> findViasElectrificadas() {
        log.debug("Buscando vías electrificadas");
        return viaRepository.findByElectrificadaTrue();
    }
    
    public List<Via> findViasRequierenMantenimiento(LocalDateTime fecha) {
        log.debug("Buscando vías que requieren mantenimiento antes de: {}", fecha);
        return viaRepository.findViasRequierenMantenimiento(fecha);
    }
    
    public List<Via> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax) {
        log.debug("Buscando vías en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        return viaRepository.findByCoordenadasRango(latMin, latMax, lonMin, lonMax);
    }
    
    public Via save(Via via) {
        log.info("Guardando vía: {}", via.getCodigoVia());
        
        if (via.getFechaCreacion() == null) {
            via.setFechaCreacion(LocalDateTime.now());
        }
        via.setFechaActualizacion(LocalDateTime.now());
        
        return viaRepository.save(via);
    }
    
    public Via update(String id, Via viaActualizada) {
        log.info("Actualizando vía con ID: {}", id);
        
        return viaRepository.findById(id)
            .map(viaExistente -> {
                viaActualizada.setId(id);
                viaActualizada.setFechaCreacion(viaExistente.getFechaCreacion());
                viaActualizada.setFechaActualizacion(LocalDateTime.now());
                return viaRepository.save(viaActualizada);
            })
            .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + id));
    }
    
    public void deleteById(String id) {
        log.info("Eliminando vía con ID: {}", id);
        
        if (!viaRepository.existsById(id)) {
            throw new RuntimeException("Vía no encontrada con ID: " + id);
        }
        
        viaRepository.deleteById(id);
    }
    
    public boolean existsByCodigoVia(String codigoVia) {
        return viaRepository.existsByCodigoVia(codigoVia);
    }
    
    public long countByEstado(Via.EstadoVia estado) {
        return viaRepository.countByEstado(estado);
    }
    
    public long countByTipoVia(Via.TipoVia tipoVia) {
        return viaRepository.countByTipoVia(tipoVia);
    }
    
    public Double sumLongitudTotal() {
        return viaRepository.sumLongitudTotal();
    }
    
    public Double sumLongitudPorEstado(Via.EstadoVia estado) {
        return viaRepository.sumLongitudPorEstado(estado);
    }
    
    public Via cambiarEstado(String id, Via.EstadoVia nuevoEstado) {
        log.info("Cambiando estado de vía {} a: {}", id, nuevoEstado);
        
        return viaRepository.findById(id)
            .map(via -> {
                via.setEstado(nuevoEstado);
                via.setFechaActualizacion(LocalDateTime.now());
                
                if (nuevoEstado == Via.EstadoVia.MANTENIMIENTO) {
                    via.setFechaUltimaMantenimiento(LocalDateTime.now());
                }
                
                return viaRepository.save(via);
            })
            .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + id));
    }
    
    public Via programarMantenimiento(String id, LocalDateTime fechaMantenimiento) {
        log.info("Programando mantenimiento para vía {} en: {}", id, fechaMantenimiento);
        
        return viaRepository.findById(id)
            .map(via -> {
                via.setProximaMantenimiento(fechaMantenimiento);
                via.setFechaActualizacion(LocalDateTime.now());
                return viaRepository.save(via);
            })
            .orElseThrow(() -> new RuntimeException("Vía no encontrada con ID: " + id));
    }
    
    public List<Via> findViasPorLongitudMinima(Double longitudMinima) {
        log.debug("Buscando vías con longitud mínima: {} km", longitudMinima);
        return viaRepository.findByLongitudMinima(longitudMinima);
    }
    
    public List<Via> findViasPorVelocidadMinima(Integer velocidadMinima) {
        log.debug("Buscando vías con velocidad mínima: {} km/h", velocidadMinima);
        return viaRepository.findByVelocidadMinima(velocidadMinima);
    }
    
    public List<Via> findViasPorSeñal(List<String> señalIds) {
        log.debug("Buscando vías por señales: {}", señalIds);
        return viaRepository.findBySeñalId(señalIds);
    }
}
