package com.david.trenes.controller;

import com.david.trenes.model.Via;
import com.david.trenes.service.ViaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class ViaController {
    
    private final ViaService viaService;
    
    @GetMapping
    public ResponseEntity<List<Via>> findAll() {
        log.info("Obteniendo todas las vías");
        List<Via> vias = viaService.findAll();
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Via> findById(@PathVariable String id) {
        log.info("Obteniendo vía por ID: {}", id);
        return viaService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigoVia}")
    public ResponseEntity<Via> findByCodigoVia(@PathVariable String codigoVia) {
        log.info("Obteniendo vía por código: {}", codigoVia);
        return viaService.findByCodigoVia(codigoVia)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<Via>> findByEstacion(@PathVariable String estacionId) {
        log.info("Obteniendo vías conectadas a estación: {}", estacionId);
        List<Via> vias = viaService.findByEstacion(estacionId);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Via>> findByEstado(@PathVariable Via.EstadoVia estado) {
        log.info("Obteniendo vías por estado: {}", estado);
        List<Via> vias = viaService.findByEstado(estado);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/tipo/{tipoVia}")
    public ResponseEntity<List<Via>> findByTipoVia(@PathVariable Via.TipoVia tipoVia) {
        log.info("Obteniendo vías por tipo: {}", tipoVia);
        List<Via> vias = viaService.findByTipoVia(tipoVia);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Via>> findViasActivas() {
        log.info("Obteniendo vías activas");
        List<Via> vias = viaService.findViasActivas();
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/electrificadas")
    public ResponseEntity<List<Via>> findViasElectrificadas() {
        log.info("Obteniendo vías electrificadas");
        List<Via> vias = viaService.findViasElectrificadas();
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/mantenimiento")
    public ResponseEntity<List<Via>> findViasRequierenMantenimiento(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(7)}") LocalDateTime fecha) {
        log.info("Obteniendo vías que requieren mantenimiento antes de: {}", fecha);
        List<Via> vias = viaService.findViasRequierenMantenimiento(fecha);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/coordenadas")
    public ResponseEntity<List<Via>> findByCoordenadasRango(
            @RequestParam Double latMin, @RequestParam Double latMax,
            @RequestParam Double lonMin, @RequestParam Double lonMax) {
        log.info("Obteniendo vías en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        List<Via> vias = viaService.findByCoordenadasRango(latMin, latMax, lonMin, lonMax);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/longitud-minima/{longitudMinima}")
    public ResponseEntity<List<Via>> findByLongitudMinima(@PathVariable Double longitudMinima) {
        log.info("Obteniendo vías con longitud mínima: {} km", longitudMinima);
        List<Via> vias = viaService.findViasPorLongitudMinima(longitudMinima);
        return ResponseEntity.ok(vias);
    }
    
    @GetMapping("/velocidad-minima/{velocidadMinima}")
    public ResponseEntity<List<Via>> findByVelocidadMinima(@PathVariable Integer velocidadMinima) {
        log.info("Obteniendo vías con velocidad mínima: {} km/h", velocidadMinima);
        List<Via> vias = viaService.findViasPorVelocidadMinima(velocidadMinima);
        return ResponseEntity.ok(vias);
    }
    
    @PostMapping
    public ResponseEntity<Via> create(@Valid @RequestBody Via via) {
        log.info("Creando nueva vía: {}", via.getCodigoVia());
        
        if (viaService.existsByCodigoVia(via.getCodigoVia())) {
            return ResponseEntity.badRequest().build();
        }
        
        Via nuevaVia = viaService.save(via);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVia);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Via> update(@PathVariable String id, @Valid @RequestBody Via via) {
        log.info("Actualizando vía con ID: {}", id);
        
        if (!viaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Via viaActualizada = viaService.update(id, via);
        return ResponseEntity.ok(viaActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando vía con ID: {}", id);
        
        if (!viaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        viaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Via> cambiarEstado(@PathVariable String id, @RequestParam Via.EstadoVia nuevoEstado) {
        log.info("Cambiando estado de vía {} a: {}", id, nuevoEstado);
        
        return viaService.findById(id)
            .map(via -> {
                Via viaActualizada = viaService.cambiarEstado(id, nuevoEstado);
                return ResponseEntity.ok(viaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/programar-mantenimiento")
    public ResponseEntity<Via> programarMantenimiento(
            @PathVariable String id, 
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaMantenimiento) {
        log.info("Programando mantenimiento para vía {} en: {}", id, fechaMantenimiento);
        
        return viaService.findById(id)
            .map(via -> {
                Via viaActualizada = viaService.programarMantenimiento(id, fechaMantenimiento);
                return ResponseEntity.ok(viaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/total-longitud")
    public ResponseEntity<Double> getTotalLongitud() {
        log.info("Obteniendo longitud total de vías");
        Double totalLongitud = viaService.sumLongitudTotal();
        return ResponseEntity.ok(totalLongitud != null ? totalLongitud : 0.0);
    }
    
    @GetMapping("/estadisticas/longitud-por-estado/{estado}")
    public ResponseEntity<Double> getLongitudPorEstado(@PathVariable Via.EstadoVia estado) {
        log.info("Obteniendo longitud total de vías por estado: {}", estado);
        Double longitud = viaService.sumLongitudPorEstado(estado);
        return ResponseEntity.ok(longitud != null ? longitud : 0.0);
    }
    
    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<Long> getCountByEstado(@PathVariable Via.EstadoVia estado) {
        log.info("Obteniendo contador de vías por estado: {}", estado);
        Long count = viaService.countByEstado(estado);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoVia}")
    public ResponseEntity<Long> getCountByTipoVia(@PathVariable Via.TipoVia tipoVia) {
        log.info("Obteniendo contador de vías por tipo: {}", tipoVia);
        Long count = viaService.countByTipoVia(tipoVia);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/exists/codigo/{codigoVia}")
    public ResponseEntity<Boolean> existsByCodigoVia(@PathVariable String codigoVia) {
        log.info("Verificando si existe vía con código: {}", codigoVia);
        boolean exists = viaService.existsByCodigoVia(codigoVia);
        return ResponseEntity.ok(exists);
    }
}
