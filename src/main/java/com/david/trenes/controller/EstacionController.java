package com.david.trenes.controller;

import com.david.trenes.model.Estacion;
import com.david.trenes.service.EstacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class EstacionController {
    
    private final EstacionService estacionService;
    
    @GetMapping
    public ResponseEntity<List<Estacion>> findAll() {
        log.info("Obteniendo todas las estaciones");
        List<Estacion> estaciones = estacionService.findAll();
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Estacion> findById(@PathVariable String id) {
        log.info("Obteniendo estación por ID: {}", id);
        return estacionService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigoEstacion}")
    public ResponseEntity<Estacion> findByCodigoEstacion(@PathVariable String codigoEstacion) {
        log.info("Obteniendo estación por código: {}", codigoEstacion);
        return estacionService.findByCodigoEstacion(codigoEstacion)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nombre")
    public ResponseEntity<List<Estacion>> findByNombreContaining(@RequestParam String nombre) {
        log.info("Obteniendo estaciones por nombre: {}", nombre);
        List<Estacion> estaciones = estacionService.findByNombreContaining(nombre);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/ciudad")
    public ResponseEntity<List<Estacion>> findByCiudad(@RequestParam String ciudad) {
        log.info("Obteniendo estaciones por ciudad: {}", ciudad);
        List<Estacion> estaciones = estacionService.findByCiudad(ciudad);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/provincia")
    public ResponseEntity<List<Estacion>> findByProvincia(@RequestParam String provincia) {
        log.info("Obteniendo estaciones por provincia: {}", provincia);
        List<Estacion> estaciones = estacionService.findByProvincia(provincia);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/tipo/{tipoEstacion}")
    public ResponseEntity<List<Estacion>> findByTipoEstacion(@PathVariable Estacion.TipoEstacion tipoEstacion) {
        log.info("Obteniendo estaciones por tipo: {}", tipoEstacion);
        List<Estacion> estaciones = estacionService.findByTipoEstacion(tipoEstacion);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Estacion>> findByCategoria(@PathVariable Estacion.CategoriaEstacion categoria) {
        log.info("Obteniendo estaciones por categoría: {}", categoria);
        List<Estacion> estaciones = estacionService.findByCategoria(categoria);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Estacion>> findEstacionesActivas() {
        log.info("Obteniendo estaciones activas");
        List<Estacion> estaciones = estacionService.findEstacionesActivas();
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/accesibles")
    public ResponseEntity<List<Estacion>> findEstacionesAccesibles() {
        log.info("Obteniendo estaciones accesibles");
        List<Estacion> estaciones = estacionService.findEstacionesAccesibles();
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/coordenadas")
    public ResponseEntity<List<Estacion>> findByCoordenadasRango(
            @RequestParam Double latMin, @RequestParam Double latMax,
            @RequestParam Double lonMin, @RequestParam Double lonMax) {
        log.info("Obteniendo estaciones en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        List<Estacion> estaciones = estacionService.findByCoordenadasRango(latMin, latMax, lonMin, lonMax);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/capacidad-estacionamiento-minima/{capacidad}")
    public ResponseEntity<List<Estacion>> findByCapacidadEstacionamientoMinima(@PathVariable Integer capacidad) {
        log.info("Obteniendo estaciones con capacidad de estacionamiento mínima: {}", capacidad);
        List<Estacion> estaciones = estacionService.findByCapacidadEstacionamientoMinima(capacidad);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/servicio/{tipoServicio}")
    public ResponseEntity<List<Estacion>> findByServicioDisponible(@PathVariable Estacion.TipoServicio tipoServicio) {
        log.info("Obteniendo estaciones con servicio disponible: {}", tipoServicio);
        List<Estacion> estaciones = estacionService.findByServicioDisponible(tipoServicio);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/anden/{tipoAnden}")
    public ResponseEntity<List<Estacion>> findByAndenTipoDisponible(@PathVariable Estacion.TipoAnden tipoAnden) {
        log.info("Obteniendo estaciones con andenes tipo {} disponibles", tipoAnden);
        List<Estacion> estaciones = estacionService.findByAndenTipoDisponible(tipoAnden);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/numero-andenes/{numeroAndenes}")
    public ResponseEntity<List<Estacion>> findByNumeroAndenes(@PathVariable Integer numeroAndenes) {
        log.info("Obteniendo estaciones con {} andenes", numeroAndenes);
        List<Estacion> estaciones = estacionService.findByNumeroAndenes(numeroAndenes);
        return ResponseEntity.ok(estaciones);
    }
    
    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<Estacion>> findBySupervisorId(@PathVariable String supervisorId) {
        log.info("Obteniendo estaciones supervisadas por: {}", supervisorId);
        List<Estacion> estaciones = estacionService.findBySupervisorId(supervisorId);
        return ResponseEntity.ok(estaciones);
    }
    
    @PostMapping
    public ResponseEntity<Estacion> create(@Valid @RequestBody Estacion estacion) {
        log.info("Creando nueva estación: {}", estacion.getCodigoEstacion());
        
        if (estacionService.existsByCodigoEstacion(estacion.getCodigoEstacion())) {
            return ResponseEntity.badRequest().build();
        }
        
        Estacion nuevaEstacion = estacionService.save(estacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEstacion);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Estacion> update(@PathVariable String id, @Valid @RequestBody Estacion estacion) {
        log.info("Actualizando estación con ID: {}", id);
        
        if (!estacionService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Estacion estacionActualizada = estacionService.update(id, estacion);
        return ResponseEntity.ok(estacionActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando estación con ID: {}", id);
        
        if (!estacionService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        estacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Estacion> cambiarEstado(@PathVariable String id, @RequestParam boolean activo) {
        log.info("Cambiando estado de estación {} a activo: {}", id, activo);
        
        return estacionService.findById(id)
            .map(estacion -> {
                Estacion estacionActualizada = estacionService.cambiarEstado(id, activo);
                return ResponseEntity.ok(estacionActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/personal")
    public ResponseEntity<Estacion> actualizarPersonal(@PathVariable String id, @RequestParam Integer personalActivo) {
        log.info("Actualizando personal activo de estación {}: {}", id, personalActivo);
        
        return estacionService.findById(id)
            .map(estacion -> {
                Estacion estacionActualizada = estacionService.actualizarPersonal(id, personalActivo);
                return ResponseEntity.ok(estacionActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/supervisor")
    public ResponseEntity<Estacion> asignarSupervisor(@PathVariable String id, @RequestParam String supervisorId) {
        log.info("Asignando supervisor {} a estación: {}", supervisorId, id);
        
        return estacionService.findById(id)
            .map(estacion -> {
                Estacion estacionActualizada = estacionService.asignarSupervisor(id, supervisorId);
                return ResponseEntity.ok(estacionActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoEstacion}")
    public ResponseEntity<Long> getCountByTipoEstacion(@PathVariable Estacion.TipoEstacion tipoEstacion) {
        log.info("Obteniendo contador de estaciones por tipo: {}", tipoEstacion);
        Long count = estacionService.countByTipoEstacion(tipoEstacion);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-categoria/{categoria}")
    public ResponseEntity<Long> getCountByCategoria(@PathVariable Estacion.CategoriaEstacion categoria) {
        log.info("Obteniendo contador de estaciones por categoría: {}", categoria);
        Long count = estacionService.countByCategoria(categoria);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-ciudad")
    public ResponseEntity<Long> getCountByCiudad(@RequestParam String ciudad) {
        log.info("Obteniendo contador de estaciones por ciudad: {}", ciudad);
        Long count = estacionService.countByCiudad(ciudad);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-activas")
    public ResponseEntity<Long> getCountActivas() {
        log.info("Obteniendo contador de estaciones activas");
        Long count = estacionService.countActivas();
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-accesibles")
    public ResponseEntity<Long> getCountAccesibles() {
        log.info("Obteniendo contador de estaciones accesibles");
        Long count = estacionService.countAccesibles();
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/exists/codigo/{codigoEstacion}")
    public ResponseEntity<Boolean> existsByCodigoEstacion(@PathVariable String codigoEstacion) {
        log.info("Verificando si existe estación con código: {}", codigoEstacion);
        boolean exists = estacionService.existsByCodigoEstacion(codigoEstacion);
        return ResponseEntity.ok(exists);
    }
}
