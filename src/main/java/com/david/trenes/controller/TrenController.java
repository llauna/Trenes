package com.david.trenes.controller;

import com.david.trenes.model.Tren;
import com.david.trenes.model.Via;
import com.david.trenes.service.TrenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trenes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TrenController {
    
    private final TrenService trenService;
    
    @GetMapping
    public ResponseEntity<List<Tren>> findAll() {
        log.info("Obteniendo todos los trenes");
        List<Tren> trenes = trenService.findAll();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tren> findById(@PathVariable String id) {
        log.info("Obteniendo tren por ID: {}", id);
        return trenService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero/{numeroTren}")
    public ResponseEntity<Tren> findByNumeroTren(@PathVariable String numeroTren) {
        log.info("Obteniendo tren por número: {}", numeroTren);
        return trenService.findByNumeroTren(numeroTren)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Tren> findByMatricula(@PathVariable String matricula) {
        log.info("Obteniendo tren por matrícula: {}", matricula);
        return trenService.findByMatricula(matricula)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tipo/{tipoTren}")
    public ResponseEntity<List<Tren>> findByTipoTren(@PathVariable Tren.TipoTren tipoTren) {
        log.info("Obteniendo trenes por tipo: {}", tipoTren);
        List<Tren> trenes = trenService.findByTipoTren(tipoTren);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Tren>> findByEstado(@PathVariable Tren.EstadoTren estado) {
        log.info("Obteniendo trenes por estado: {}", estado);
        List<Tren> trenes = trenService.findByEstado(estado);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Tren>> findTrenesActivos() {
        log.info("Obteniendo trenes activos");
        List<Tren> trenes = trenService.findTrenesActivos();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/operativos")
    public ResponseEntity<List<Tren>> findTrenesOperativos() {
        log.info("Obteniendo trenes operativos");
        List<Tren> trenes = trenService.findTrenesOperativos();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/en-marcha")
    public ResponseEntity<List<Tren>> findTrenesEnMarcha() {
        log.info("Obteniendo trenes en marcha");
        List<Tren> trenes = trenService.findTrenesEnMarcha();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/en-estacion")
    public ResponseEntity<List<Tren>> findTrenesEnEstacion() {
        log.info("Obteniendo trenes en estación");
        List<Tren> trenes = trenService.findTrenesEnEstacion();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/con-incidencias")
    public ResponseEntity<List<Tren>> findTrenesConIncidenciasActivas() {
        log.info("Obteniendo trenes con incidencias activas");
        List<Tren> trenes = trenService.findTrenesConIncidenciasActivas();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/via/{viaId}")
    public ResponseEntity<List<Tren>> findByViaActual(@PathVariable String viaId) {
        log.info("Obteniendo trenes en vía: {}", viaId);
        List<Tren> trenes = trenService.findByViaActual(viaId);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Tren>> findByRutaActual(@PathVariable String rutaId) {
        log.info("Obteniendo trenes en ruta: {}", rutaId);
        List<Tren> trenes = trenService.findByRutaActual(rutaId);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<List<Tren>> findByConductor(@PathVariable String conductorId) {
        log.info("Obteniendo trenes asignados a conductor: {}", conductorId);
        List<Tren> trenes = trenService.findByConductor(conductorId);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/coordenadas")
    public ResponseEntity<List<Tren>> findByUbicacionRango(
            @RequestParam Double latMin, @RequestParam Double latMax,
            @RequestParam Double lonMin, @RequestParam Double lonMax) {
        log.info("Obteniendo trenes en rango de coordenadas: lat[{},{}], lon[{},{}]", latMin, latMax, lonMin, lonMax);
        List<Tren> trenes = trenService.findByUbicacionRango(latMin, latMax, lonMin, lonMax);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/revision-pendiente")
    public ResponseEntity<List<Tren>> findTrenesRequierenRevision(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(30)}") LocalDateTime fecha) {
        log.info("Obteniendo trenes que requieren revisión antes de: {}", fecha);
        List<Tren> trenes = trenService.findTrenesRequierenRevision(fecha);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/capacidad-pasajeros-minima/{capacidad}")
    public ResponseEntity<List<Tren>> findByCapacidadPasajerosMinima(@PathVariable Integer capacidad) {
        log.info("Obteniendo trenes con capacidad mínima de {} pasajeros", capacidad);
        List<Tren> trenes = trenService.findByCapacidadPasajerosMinima(capacidad);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/capacidad-carga-minima/{capacidad}")
    public ResponseEntity<List<Tren>> findByCapacidadCargaMinima(@PathVariable Double capacidad) {
        log.info("Obteniendo trenes con capacidad mínima de carga: {} toneladas", capacidad);
        List<Tren> trenes = trenService.findByCapacidadCargaMinima(capacidad);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/velocidad-minima/{velocidad}")
    public ResponseEntity<List<Tren>> findByVelocidadMaximaMinima(@PathVariable Integer velocidad) {
        log.info("Obteniendo trenes con velocidad máxima mínima: {} km/h", velocidad);
        List<Tren> trenes = trenService.findByVelocidadMaximaMinima(velocidad);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/fabricante/{fabricante}")
    public ResponseEntity<List<Tren>> findByFabricante(@PathVariable String fabricante) {
        log.info("Obteniendo trenes del fabricante: {}", fabricante);
        List<Tren> trenes = trenService.findByFabricante(fabricante);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<Tren>> findByModelo(@PathVariable String modelo) {
        log.info("Obteniendo trenes del modelo: {}", modelo);
        List<Tren> trenes = trenService.findByModelo(modelo);
        return ResponseEntity.ok(trenes);
    }
    
    @PostMapping
    public ResponseEntity<Tren> create(@Valid @RequestBody Tren tren) {
        log.info("Creando nuevo tren: {}", tren.getNumeroTren());
        
        if (trenService.existsByNumeroTren(tren.getNumeroTren())) {
            return ResponseEntity.badRequest().build();
        }
        
        if (trenService.existsByMatricula(tren.getMatricula())) {
            return ResponseEntity.badRequest().build();
        }
        
        Tren nuevoTren = trenService.save(tren);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTren);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tren> update(@PathVariable String id, @Valid @RequestBody Tren tren) {
        log.info("Actualizando tren con ID: {}", id);
        
        if (!trenService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Tren trenActualizado = trenService.update(id, tren);
        return ResponseEntity.ok(trenActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando tren con ID: {}", id);
        
        if (!trenService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        trenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Tren> cambiarEstado(@PathVariable String id, @RequestParam Tren.EstadoTren nuevoEstado) {
        log.info("Cambiando estado de tren {} a: {}", id, nuevoEstado);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.cambiarEstado(id, nuevoEstado);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/conductor")
    public ResponseEntity<Tren> asignarConductor(@PathVariable String id, @RequestParam String conductorId) {
        log.info("Asignando conductor {} a tren: {}", conductorId, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.asignarConductor(id, conductorId);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ruta")
    public ResponseEntity<Tren> asignarRuta(@PathVariable String id, @RequestParam String rutaId) {
        log.info("Asignando ruta {} a tren: {}", rutaId, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.asignarRuta(id, rutaId);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ubicacion")
    public ResponseEntity<Tren> actualizarUbicacion(
            @PathVariable String id,
            @RequestParam Double latitud, @RequestParam Double longitud, @RequestParam Double altitud,
            @RequestParam String viaId, @RequestParam Double kilometro) {
        log.info("Actualizando ubicación de tren {}: vía {}, km {}", id, viaId, kilometro);
        
        return trenService.findById(id)
            .map(tren -> {
                Via.Coordenada ubicacion = Via.Coordenada.builder()
                    .latitud(latitud)
                    .longitud(longitud)
                    .altitud(altitud)
                    .build();
                
                Tren trenActualizado = trenService.actualizarUbicacion(id, ubicacion, viaId, kilometro);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/kilometraje")
    public ResponseEntity<Tren> registrarKilometraje(@PathVariable String id, @RequestParam Double kilometrosAdicionales) {
        log.info("Registrando {} km adicionales al tren: {}", kilometrosAdicionales, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.registrarKilometraje(id, kilometrosAdicionales);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/programar-revision")
    public ResponseEntity<Tren> programarRevision(
            @PathVariable String id,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaRevision) {
        log.info("Programando revisión para tren {} en: {}", id, fechaRevision);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.programarRevision(id, fechaRevision);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/completar-revision")
    public ResponseEntity<Tren> completarRevision(@PathVariable String id) {
        log.info("Completando revisión para tren: {}", id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.completarRevision(id);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoTren}")
    public ResponseEntity<Long> getCountByTipoTren(@PathVariable Tren.TipoTren tipoTren) {
        log.info("Obteniendo contador de trenes por tipo: {}", tipoTren);
        Long count = trenService.countByTipoTren(tipoTren);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<Long> getCountByEstado(@PathVariable Tren.EstadoTren estado) {
        log.info("Obteniendo contador de trenes por estado: {}", estado);
        Long count = trenService.countByEstado(estado);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-activos")
    public ResponseEntity<Long> getCountActivos() {
        log.info("Obteniendo contador de trenes activos");
        Long count = trenService.countActivos();
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/kilometraje-total")
    public ResponseEntity<Double> getKilometrajeTotal() {
        log.info("Obteniendo kilometraje total de trenes");
        Double total = trenService.sumKilometrajeTotal();
        return ResponseEntity.ok(total != null ? total : 0.0);
    }
    
    @GetMapping("/estadisticas/capacidad-total-pasajeros")
    public ResponseEntity<Integer> getCapacidadTotalPasajeros() {
        log.info("Obteniendo capacidad total de pasajeros");
        Integer total = trenService.sumCapacidadTotalPasajeros();
        return ResponseEntity.ok(total != null ? total : 0);
    }
    
    @GetMapping("/exists/numero/{numeroTren}")
    public ResponseEntity<Boolean> existsByNumeroTren(@PathVariable String numeroTren) {
        log.info("Verificando si existe tren con número: {}", numeroTren);
        boolean exists = trenService.existsByNumeroTren(numeroTren);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists/matricula/{matricula}")
    public ResponseEntity<Boolean> existsByMatricula(@PathVariable String matricula) {
        log.info("Verificando si existe tren con matrícula: {}", matricula);
        boolean exists = trenService.existsByMatricula(matricula);
        return ResponseEntity.ok(exists);
    }
}
