package com.david.trenes.service;

import com.david.trenes.model.Ruta;
import com.david.trenes.repository.RutaRepository;
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
public class RutaService {
    
    private final RutaRepository rutaRepository;
    
    public List<Ruta> findAll() {
        log.debug("Buscando todas las rutas");
        return rutaRepository.findAll();
    }
    
    public Optional<Ruta> findById(String id) {
        log.debug("Buscando ruta por ID: {}", id);
        return rutaRepository.findById(id);
    }
    
    public Optional<Ruta> findByCodigoRuta(String codigoRuta) {
        log.debug("Buscando ruta por código: {}", codigoRuta);
        return rutaRepository.findByCodigoRuta(codigoRuta);
    }
    
    public List<Ruta> findByNombreContaining(String nombre) {
        log.debug("Buscando rutas por nombre: {}", nombre);
        return rutaRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<Ruta> findByTipoRuta(Ruta.TipoRuta tipoRuta) {
        log.debug("Buscando rutas por tipo: {}", tipoRuta);
        return rutaRepository.findByTipoRuta(tipoRuta);
    }
    
    public List<Ruta> findByEstado(Ruta.EstadoRuta estado) {
        log.debug("Buscando rutas por estado: {}", estado);
        return rutaRepository.findByEstado(estado);
    }
    
    public List<Ruta> findRutasActivas() {
        log.debug("Buscando rutas activas");
        return rutaRepository.findByActivoTrue();
    }
    
    public List<Ruta> findByEstaciones(String estacionOrigenId, String estacionDestinoId) {
        log.debug("Buscando rutas entre estaciones {} y {}", estacionOrigenId, estacionDestinoId);
        return rutaRepository.findByEstacionOrigenIdAndEstacionDestinoId(estacionOrigenId, estacionDestinoId);
    }
    
    public List<Ruta> findByEstacionOrigen(String estacionOrigenId) {
        log.debug("Buscando rutas con origen en estación: {}", estacionOrigenId);
        return rutaRepository.findByEstacionOrigenIdOrEstacionDestinoId(estacionOrigenId);
    }
    
    public List<Ruta> findByEstacionDestino(String estacionDestinoId) {
        log.debug("Buscando rutas con destino en estación: {}", estacionDestinoId);
        return rutaRepository.findByEstacionOrigenIdOrEstacionDestinoId(estacionDestinoId);
    }
    
    public List<Ruta> findByEstacionIntermedia(String estacionId) {
        log.debug("Buscando rutas que pasan por estación intermedia: {}", estacionId);
        return rutaRepository.findByEstacionIntermedia(estacionId);
    }
    
    public List<Ruta> findByViaId(String viaId) {
        log.debug("Buscando rutas que usan la vía: {}", viaId);
        return rutaRepository.findByViaId(viaId);
    }
    
    public List<Ruta> findByDistanciaMinima(Double distanciaMinima) {
        log.debug("Buscando rutas con distancia mínima: {} km", distanciaMinima);
        return rutaRepository.findByDistanciaMinima(distanciaMinima);
    }
    
    public List<Ruta> findByDistanciaMaxima(Double distanciaMaxima) {
        log.debug("Buscando rutas con distancia máxima: {} km", distanciaMaxima);
        return rutaRepository.findByDistanciaMaxima(distanciaMaxima);
    }
    
    public List<Ruta> findByTiempoMaximo(Integer tiempoMaximo) {
        log.debug("Buscando rutas con tiempo máximo: {} minutos", tiempoMaximo);
        return rutaRepository.findByTiempoMaximo(tiempoMaximo);
    }
    
    public List<Ruta> findByVelocidadPromedioMinima(Double velocidadMinima) {
        log.debug("Buscando rutas con velocidad promedio mínima: {} km/h", velocidadMinima);
        return rutaRepository.findByVelocidadPromedioMinima(velocidadMinima);
    }
    
    public List<Ruta> findByPrioridadMinima(Integer prioridadMinima) {
        log.debug("Buscando rutas con prioridad mínima: {}", prioridadMinima);
        return rutaRepository.findByPrioridadMinima(prioridadMinima);
    }
    
    public List<Ruta> findByTarifaMaxima(Double tarifaMaxima) {
        log.debug("Buscando rutas con tarifa máxima: {}", tarifaMaxima);
        return rutaRepository.findByTarifaMaxima(tarifaMaxima);
    }
    
    public List<Ruta> findByZona(String zona) {
        log.debug("Buscando rutas en zona: {}", zona);
        return rutaRepository.findByZona(zona);
    }
    
    public List<Ruta> findRutasLunes() {
        log.debug("Buscando rutas que operan los lunes");
        return rutaRepository.findRutasLunes();
    }
    
    public List<Ruta> findRutasDomingo() {
        log.debug("Buscando rutas que operan los domingos");
        return rutaRepository.findRutasDomingo();
    }
    
    public List<Ruta> findByServiciosDiaMinimo(Integer serviciosMinimo) {
        log.debug("Buscando rutas con al menos {} servicios por día", serviciosMinimo);
        return rutaRepository.findByServiciosDiaMinimo(serviciosMinimo);
    }
    
    public List<Ruta> findByNumeroParadas(Integer numeroParadas) {
        log.debug("Buscando rutas con {} paradas", numeroParadas);
        return rutaRepository.findByNumeroParadas(numeroParadas);
    }
    
    public List<Ruta> findByRestriccionActiva(Ruta.TipoRestriccion tipoRestriccion) {
        log.debug("Buscando rutas con restricción activa: {}", tipoRestriccion);
        return rutaRepository.findByRestriccionActiva(tipoRestriccion);
    }
    
    public Ruta save(Ruta ruta) {
        log.info("Guardando ruta: {}", ruta.getCodigoRuta());
        
        if (ruta.getFechaCreacion() == null) {
            ruta.setFechaCreacion(LocalDateTime.now());
        }
        ruta.setFechaActualizacion(LocalDateTime.now());
        
        return rutaRepository.save(ruta);
    }
    
    public Ruta update(String id, Ruta rutaActualizada) {
        log.info("Actualizando ruta con ID: {}", id);
        
        return rutaRepository.findById(id)
            .map(rutaExistente -> {
                rutaActualizada.setId(id);
                rutaActualizada.setFechaCreacion(rutaExistente.getFechaCreacion());
                rutaActualizada.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(rutaActualizada);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public void deleteById(String id) {
        log.info("Eliminando ruta con ID: {}", id);
        
        if (!rutaRepository.existsById(id)) {
            throw new RuntimeException("Ruta no encontrada con ID: " + id);
        }
        
        rutaRepository.deleteById(id);
    }
    
    public boolean existsByCodigoRuta(String codigoRuta) {
        return rutaRepository.existsByCodigoRuta(codigoRuta);
    }
    
    public long countByTipoRuta(Ruta.TipoRuta tipoRuta) {
        return rutaRepository.countByTipoRuta(tipoRuta);
    }
    
    public long countByEstado(Ruta.EstadoRuta estado) {
        return rutaRepository.countByEstado(estado);
    }
    
    public long countActivas() {
        return rutaRepository.countByActivoTrue();
    }
    
    public Double sumDistanciaTotal() {
        return rutaRepository.sumDistanciaTotal();
    }
    
    public Double sumDistanciaPorEstado(Ruta.EstadoRuta estado) {
        return rutaRepository.sumDistanciaPorEstado(estado);
    }
    
    public Double avgTiempoEstimado() {
        return rutaRepository.avgTiempoEstimado();
    }
    
    public Ruta cambiarEstado(String id, Ruta.EstadoRuta nuevoEstado) {
        log.info("Cambiando estado de ruta {} a: {}", id, nuevoEstado);
        
        return rutaRepository.findById(id)
            .map(ruta -> {
                ruta.setEstado(nuevoEstado);
                ruta.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(ruta);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public Ruta actualizarTarifa(String id, Double nuevaTarifa) {
        log.info("Actualizando tarifa de ruta {} a: {}", id, nuevaTarifa);
        
        return rutaRepository.findById(id)
            .map(ruta -> {
                ruta.setTarifaBase(nuevaTarifa);
                ruta.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(ruta);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public Ruta actualizarPrioridad(String id, Integer nuevaPrioridad) {
        log.info("Actualizando prioridad de ruta {} a: {}", id, nuevaPrioridad);
        
        return rutaRepository.findById(id)
            .map(ruta -> {
                ruta.setPrioridad(nuevaPrioridad);
                ruta.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(ruta);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public Ruta agregarRestriccion(String id, Ruta.RestriccionRuta restriccion) {
        log.info("Agregando restricción a ruta: {}", id);
        
        return rutaRepository.findById(id)
            .map(ruta -> {
                ruta.getRestricciones().add(restriccion);
                ruta.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(ruta);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public Ruta removerRestriccion(String id, String tipoRestriccion) {
        log.info("Removiendo restricción {} de ruta: {}", tipoRestriccion, id);
        
        return rutaRepository.findById(id)
            .map(ruta -> {
                ruta.getRestricciones().removeIf(r -> r.getTipo().toString().equals(tipoRestriccion));
                ruta.setFechaActualizacion(LocalDateTime.now());
                return rutaRepository.save(ruta);
            })
            .orElseThrow(() -> new RuntimeException("Ruta no encontrada con ID: " + id));
    }
    
    public List<Ruta> buscarRutasAlternativas(String estacionOrigenId, String estacionDestinoId, Ruta.TipoRuta tipoRuta) {
        log.debug("Buscando rutas alternativas entre {} y {} de tipo {}", estacionOrigenId, estacionDestinoId, tipoRuta);
        
        return rutaRepository.findByEstacionOrigenIdAndEstacionDestinoId(estacionOrigenId, estacionDestinoId)
            .stream()
            .filter(ruta -> ruta.getTipoRuta() == tipoRuta && ruta.getActivo() && ruta.getEstado() == Ruta.EstadoRuta.ACTIVA)
            .toList();
    }
}
