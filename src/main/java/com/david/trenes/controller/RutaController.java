package com.david.trenes.controller;

import com.david.trenes.model.Ruta;
import com.david.trenes.service.RutaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class RutaController {
    
    private final RutaService rutaService;
    
    @GetMapping
    public ResponseEntity<List<Ruta>> findAll() {
        log.info("Obteniendo todas las rutas");
        List<Ruta> rutas = rutaService.findAll();
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Ruta> findById(@PathVariable String id) {
        log.info("Obteniendo ruta por ID: {}", id);
        return rutaService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigoRuta}")
    public ResponseEntity<Ruta> findByCodigoRuta(@PathVariable String codigoRuta) {
        log.info("Obteniendo ruta por código: {}", codigoRuta);
        return rutaService.findByCodigoRuta(codigoRuta)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/nombre")
    public ResponseEntity<List<Ruta>> findByNombreContaining(@RequestParam String nombre) {
        log.info("Obteniendo rutas por nombre: {}", nombre);
        List<Ruta> rutas = rutaService.findByNombreContaining(nombre);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/tipo/{tipoRuta}")
    public ResponseEntity<List<Ruta>> findByTipoRuta(@PathVariable Ruta.TipoRuta tipoRuta) {
        log.info("Obteniendo rutas por tipo: {}", tipoRuta);
        List<Ruta> rutas = rutaService.findByTipoRuta(tipoRuta);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Ruta>> findByEstado(@PathVariable Ruta.EstadoRuta estado) {
        log.info("Obteniendo rutas por estado: {}", estado);
        List<Ruta> rutas = rutaService.findByEstado(estado);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Ruta>> findRutasActivas() {
        log.info("Obteniendo rutas activas");
        List<Ruta> rutas = rutaService.findRutasActivas();
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/entre-estaciones")
    public ResponseEntity<List<Ruta>> findByEstaciones(
            @RequestParam String estacionOrigenId,
            @RequestParam String estacionDestinoId) {
        log.info("Obteniendo rutas entre estaciones {} y {}", estacionOrigenId, estacionDestinoId);
        List<Ruta> rutas = rutaService.findByEstaciones(estacionOrigenId, estacionDestinoId);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/origen/{estacionOrigenId}")
    public ResponseEntity<List<Ruta>> findByEstacionOrigen(@PathVariable String estacionOrigenId) {
        log.info("Obteniendo rutas con origen en estación: {}", estacionOrigenId);
        List<Ruta> rutas = rutaService.findByEstacionOrigen(estacionOrigenId);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/destino/{estacionDestinoId}")
    public ResponseEntity<List<Ruta>> findByEstacionDestino(@PathVariable String estacionDestinoId) {
        log.info("Obteniendo rutas con destino en estación: {}", estacionDestinoId);
        List<Ruta> rutas = rutaService.findByEstacionDestino(estacionDestinoId);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/intermedia/{estacionId}")
    public ResponseEntity<List<Ruta>> findByEstacionIntermedia(@PathVariable String estacionId) {
        log.info("Obteniendo rutas que pasan por estación intermedia: {}", estacionId);
        List<Ruta> rutas = rutaService.findByEstacionIntermedia(estacionId);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/via/{viaId}")
    public ResponseEntity<List<Ruta>> findByViaId(@PathVariable String viaId) {
        log.info("Obteniendo rutas que usan la vía: {}", viaId);
        List<Ruta> rutas = rutaService.findByViaId(viaId);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/distancia-minima/{distanciaMinima}")
    public ResponseEntity<List<Ruta>> findByDistanciaMinima(@PathVariable Double distanciaMinima) {
        log.info("Obteniendo rutas con distancia mínima: {} km", distanciaMinima);
        List<Ruta> rutas = rutaService.findByDistanciaMinima(distanciaMinima);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/distancia-maxima/{distanciaMaxima}")
    public ResponseEntity<List<Ruta>> findByDistanciaMaxima(@PathVariable Double distanciaMaxima) {
        log.info("Obteniendo rutas con distancia máxima: {} km", distanciaMaxima);
        List<Ruta> rutas = rutaService.findByDistanciaMaxima(distanciaMaxima);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/tiempo-maximo/{tiempoMaximo}")
    public ResponseEntity<List<Ruta>> findByTiempoMaximo(@PathVariable Integer tiempoMaximo) {
        log.info("Obteniendo rutas con tiempo máximo: {} minutos", tiempoMaximo);
        List<Ruta> rutas = rutaService.findByTiempoMaximo(tiempoMaximo);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/velocidad-minima/{velocidadMinima}")
    public ResponseEntity<List<Ruta>> findByVelocidadPromedioMinima(@PathVariable Double velocidadMinima) {
        log.info("Obteniendo rutas con velocidad promedio mínima: {} km/h", velocidadMinima);
        List<Ruta> rutas = rutaService.findByVelocidadPromedioMinima(velocidadMinima);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/prioridad-minima/{prioridadMinima}")
    public ResponseEntity<List<Ruta>> findByPrioridadMinima(@PathVariable Integer prioridadMinima) {
        log.info("Obteniendo rutas con prioridad mínima: {}", prioridadMinima);
        List<Ruta> rutas = rutaService.findByPrioridadMinima(prioridadMinima);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/tarifa-maxima/{tarifaMaxima}")
    public ResponseEntity<List<Ruta>> findByTarifaMaxima(@PathVariable Double tarifaMaxima) {
        log.info("Obteniendo rutas con tarifa máxima: {}", tarifaMaxima);
        List<Ruta> rutas = rutaService.findByTarifaMaxima(tarifaMaxima);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<Ruta>> findByZona(@PathVariable String zona) {
        log.info("Obteniendo rutas en zona: {}", zona);
        List<Ruta> rutas = rutaService.findByZona(zona);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/lunes")
    public ResponseEntity<List<Ruta>> findRutasLunes() {
        log.info("Obteniendo rutas que operan los lunes");
        List<Ruta> rutas = rutaService.findRutasLunes();
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/domingo")
    public ResponseEntity<List<Ruta>> findRutasDomingo() {
        log.info("Obteniendo rutas que operan los domingos");
        List<Ruta> rutas = rutaService.findRutasDomingo();
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/servicios-dia-minimo/{serviciosMinimo}")
    public ResponseEntity<List<Ruta>> findByServiciosDiaMinimo(@PathVariable Integer serviciosMinimo) {
        log.info("Obteniendo rutas con al menos {} servicios por día", serviciosMinimo);
        List<Ruta> rutas = rutaService.findByServiciosDiaMinimo(serviciosMinimo);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/numero-paradas/{numeroParadas}")
    public ResponseEntity<List<Ruta>> findByNumeroParadas(@PathVariable Integer numeroParadas) {
        log.info("Obteniendo rutas con {} paradas", numeroParadas);
        List<Ruta> rutas = rutaService.findByNumeroParadas(numeroParadas);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/restriccion/{tipoRestriccion}")
    public ResponseEntity<List<Ruta>> findByRestriccionActiva(@PathVariable Ruta.TipoRestriccion tipoRestriccion) {
        log.info("Obteniendo rutas con restricción activa: {}", tipoRestriccion);
        List<Ruta> rutas = rutaService.findByRestriccionActiva(tipoRestriccion);
        return ResponseEntity.ok(rutas);
    }
    
    @GetMapping("/alternativas")
    public ResponseEntity<List<Ruta>> buscarRutasAlternativas(
            @RequestParam String estacionOrigenId,
            @RequestParam String estacionDestinoId,
            @RequestParam Ruta.TipoRuta tipoRuta) {
        log.info("Buscando rutas alternativas entre {} y {} de tipo {}", estacionOrigenId, estacionDestinoId, tipoRuta);
        List<Ruta> rutas = rutaService.buscarRutasAlternativas(estacionOrigenId, estacionDestinoId, tipoRuta);
        return ResponseEntity.ok(rutas);
    }
    
    @PostMapping
    public ResponseEntity<Ruta> create(@Valid @RequestBody Ruta ruta) {
        log.info("Creando nueva ruta: {}", ruta.getCodigoRuta());
        
        if (rutaService.existsByCodigoRuta(ruta.getCodigoRuta())) {
            return ResponseEntity.badRequest().build();
        }
        
        Ruta nuevaRuta = rutaService.save(ruta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRuta);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Ruta> update(@PathVariable String id, @Valid @RequestBody Ruta ruta) {
        log.info("Actualizando ruta con ID: {}", id);
        
        if (!rutaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Ruta rutaActualizada = rutaService.update(id, ruta);
        return ResponseEntity.ok(rutaActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando ruta con ID: {}", id);
        
        if (!rutaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        rutaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Ruta> cambiarEstado(@PathVariable String id, @RequestParam Ruta.EstadoRuta nuevoEstado) {
        log.info("Cambiando estado de ruta {} a: {}", id, nuevoEstado);
        
        return rutaService.findById(id)
            .map(ruta -> {
                Ruta rutaActualizada = rutaService.cambiarEstado(id, nuevoEstado);
                return ResponseEntity.ok(rutaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/tarifa")
    public ResponseEntity<Ruta> actualizarTarifa(@PathVariable String id, @RequestParam Double nuevaTarifa) {
        log.info("Actualizando tarifa de ruta {} a: {}", id, nuevaTarifa);
        
        return rutaService.findById(id)
            .map(ruta -> {
                Ruta rutaActualizada = rutaService.actualizarTarifa(id, nuevaTarifa);
                return ResponseEntity.ok(rutaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/prioridad")
    public ResponseEntity<Ruta> actualizarPrioridad(@PathVariable String id, @RequestParam Integer nuevaPrioridad) {
        log.info("Actualizando prioridad de ruta {} a: {}", id, nuevaPrioridad);
        
        return rutaService.findById(id)
            .map(ruta -> {
                Ruta rutaActualizada = rutaService.actualizarPrioridad(id, nuevaPrioridad);
                return ResponseEntity.ok(rutaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/restriccion")
    public ResponseEntity<Ruta> agregarRestriccion(@PathVariable String id, @RequestBody Ruta.RestriccionRuta restriccion) {
        log.info("Agregando restricción a ruta: {}", id);
        
        return rutaService.findById(id)
            .map(ruta -> {
                Ruta rutaActualizada = rutaService.agregarRestriccion(id, restriccion);
                return ResponseEntity.ok(rutaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}/restriccion/{tipoRestriccion}")
    public ResponseEntity<Ruta> removerRestriccion(@PathVariable String id, @PathVariable String tipoRestriccion) {
        log.info("Removiendo restricción {} de ruta: {}", tipoRestriccion, id);
        
        return rutaService.findById(id)
            .map(ruta -> {
                Ruta rutaActualizada = rutaService.removerRestriccion(id, tipoRestriccion);
                return ResponseEntity.ok(rutaActualizada);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoRuta}")
    public ResponseEntity<Long> getCountByTipoRuta(@PathVariable Ruta.TipoRuta tipoRuta) {
        log.info("Obteniendo contador de rutas por tipo: {}", tipoRuta);
        Long count = rutaService.countByTipoRuta(tipoRuta);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<Long> getCountByEstado(@PathVariable Ruta.EstadoRuta estado) {
        log.info("Obteniendo contador de rutas por estado: {}", estado);
        Long count = rutaService.countByEstado(estado);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-activas")
    public ResponseEntity<Long> getCountActivas() {
        log.info("Obteniendo contador de rutas activas");
        Long count = rutaService.countActivas();
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/distancia-total")
    public ResponseEntity<Double> getDistanciaTotal() {
        log.info("Obteniendo distancia total de rutas");
        Double total = rutaService.sumDistanciaTotal();
        return ResponseEntity.ok(total != null ? total : 0.0);
    }
    
    @GetMapping("/estadisticas/distancia-por-estado/{estado}")
    public ResponseEntity<Double> getDistanciaPorEstado(@PathVariable Ruta.EstadoRuta estado) {
        log.info("Obteniendo distancia total de rutas por estado: {}", estado);
        Double distancia = rutaService.sumDistanciaPorEstado(estado);
        return ResponseEntity.ok(distancia != null ? distancia : 0.0);
    }
    
    @GetMapping("/estadisticas/tiempo-promedio")
    public ResponseEntity<Double> getTiempoPromedio() {
        log.info("Obteniendo tiempo promedio de rutas");
        Double promedio = rutaService.avgTiempoEstimado();
        return ResponseEntity.ok(promedio != null ? promedio : 0.0);
    }
    
    @GetMapping("/exists/codigo/{codigoRuta}")
    public ResponseEntity<Boolean> existsByCodigoRuta(@PathVariable String codigoRuta) {
        log.info("Verificando si existe ruta con código: {}", codigoRuta);
        boolean exists = rutaService.existsByCodigoRuta(codigoRuta);
        return ResponseEntity.ok(exists);
    }
}
