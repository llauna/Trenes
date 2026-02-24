package com.david.trenes.controller;

import com.david.trenes.model.Horario;
import com.david.trenes.service.HorarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/horarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class HorarioController {
    
    private final HorarioService horarioService;
    
    @GetMapping
    public ResponseEntity<List<Horario>> findAll() {
        log.info("Obteniendo todos los horarios");
        List<Horario> horarios = horarioService.findAll();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Horario> findById(@PathVariable String id) {
        log.info("Obteniendo horario por ID: {}", id);
        return horarioService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/codigo/{codigoServicio}")
    public ResponseEntity<Horario> findByCodigoServicio(@PathVariable String codigoServicio) {
        log.info("Obteniendo horario por código de servicio: {}", codigoServicio);
        return horarioService.findByCodigoServicio(codigoServicio)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero/{numeroServicio}")
    public ResponseEntity<List<Horario>> findByNumeroServicio(@PathVariable String numeroServicio) {
        log.info("Obteniendo horarios por número de servicio: {}", numeroServicio);
        List<Horario> horarios = horarioService.findByNumeroServicio(numeroServicio);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/tren/{trenId}")
    public ResponseEntity<List<Horario>> findByTrenId(@PathVariable String trenId) {
        log.info("Obteniendo horarios del tren: {}", trenId);
        List<Horario> horarios = horarioService.findByTrenId(trenId);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Horario>> findByRutaId(@PathVariable String rutaId) {
        log.info("Obteniendo horarios de la ruta: {}", rutaId);
        List<Horario> horarios = horarioService.findByRutaId(rutaId);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<List<Horario>> findByConductorId(@PathVariable String conductorId) {
        log.info("Obteniendo horarios del conductor: {}", conductorId);
        List<Horario> horarios = horarioService.findByConductorId(conductorId);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/tipo/{tipoServicio}")
    public ResponseEntity<List<Horario>> findByTipoServicio(@PathVariable Horario.TipoServicio tipoServicio) {
        log.info("Obteniendo horarios por tipo de servicio: {}", tipoServicio);
        List<Horario> horarios = horarioService.findByTipoServicio(tipoServicio);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Horario>> findByEstado(@PathVariable Horario.EstadoHorario estado) {
        log.info("Obteniendo horarios por estado: {}", estado);
        List<Horario> horarios = horarioService.findByEstado(estado);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Horario>> findHorariosActivos() {
        log.info("Obteniendo horarios activos");
        List<Horario> horarios = horarioService.findHorariosActivos();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/entre-estaciones")
    public ResponseEntity<List<Horario>> findByEstaciones(
            @RequestParam String estacionOrigenId,
            @RequestParam String estacionDestinoId) {
        log.info("Obteniendo horarios entre estaciones {} y {}", estacionOrigenId, estacionDestinoId);
        List<Horario> horarios = horarioService.findByEstaciones(estacionOrigenId, estacionDestinoId);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/salida-entre")
    public ResponseEntity<List<Horario>> findByFechaSalidaBetween(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        log.info("Obteniendo horarios con salida entre {} y {}", fechaInicio, fechaFin);
        List<Horario> horarios = horarioService.findByFechaSalidaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/llegada-entre")
    public ResponseEntity<List<Horario>> findByFechaLlegadaBetween(
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        log.info("Obteniendo horarios con llegada entre {} y {}", fechaInicio, fechaFin);
        List<Horario> horarios = horarioService.findByFechaLlegadaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/salida-despues")
    public ResponseEntity<List<Horario>> findByFechaSalidaAfter(
            @RequestParam LocalDateTime fecha) {
        log.info("Obteniendo horarios con salida después de: {}", fecha);
        List<Horario> horarios = horarioService.findByFechaSalidaAfter(fecha);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/salida-antes")
    public ResponseEntity<List<Horario>> findByFechaSalidaBefore(
            @RequestParam LocalDateTime fecha) {
        log.info("Obteniendo horarios con salida antes de: {}", fecha);
        List<Horario> horarios = horarioService.findByFechaSalidaBefore(fecha);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/parada-estacion/{estacionId}")
    public ResponseEntity<List<Horario>> findByEstacionParada(@PathVariable String estacionId) {
        log.info("Obteniendo horarios que paran en estación: {}", estacionId);
        List<Horario> horarios = horarioService.findByEstacionParada(estacionId);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/llegada-estacion-entre")
    public ResponseEntity<List<Horario>> findByLlegadaEstacionEntreFechas(
            @PathVariable String estacionId,
            @RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        log.info("Obteniendo horarios con llegada a estación {} entre {} y {}", estacionId, fechaInicio, fechaFin);
        List<Horario> horarios = horarioService.findByLlegadaEstacionEntreFechas(estacionId, fechaInicio, fechaFin);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/capacidad-minima/{capacidad}")
    public ResponseEntity<List<Horario>> findByCapacidadMinima(@PathVariable Integer capacidad) {
        log.info("Obteniendo horarios con capacidad mínima de {} pasajeros", capacidad);
        List<Horario> horarios = horarioService.findByCapacidadMinima(capacidad);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/ocupacion-minima/{ocupacion}")
    public ResponseEntity<List<Horario>> findByOcupacionMinima(@PathVariable Double ocupacion) {
        log.info("Obteniendo horarios con ocupación mínima del {}%", ocupacion);
        List<Horario> horarios = horarioService.findByOcupacionMinima(ocupacion);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/casi-llenos")
    public ResponseEntity<List<Horario>> findHorariosCasiLlenos() {
        log.info("Obteniendo horarios casi llenos (>90% ocupación)");
        List<Horario> horarios = horarioService.findHorariosCasiLlenos();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/poca-ocupacion")
    public ResponseEntity<List<Horario>> findHorariosConPocaOcupacion() {
        log.info("Obteniendo horarios con poca ocupación (<30%)");
        List<Horario> horarios = horarioService.findHorariosConPocaOcupacion();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/tarifa-maxima/{tarifa}")
    public ResponseEntity<List<Horario>> findByTarifaMaxima(@PathVariable Double tarifa) {
        log.info("Obteniendo horarios con tarifa máxima de {}", tarifa);
        List<Horario> horarios = horarioService.findByTarifaMaxima(tarifa);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/clase/{nombreClase}")
    public ResponseEntity<List<Horario>> findByClaseDisponible(@PathVariable String nombreClase) {
        log.info("Obteniendo horarios con clase disponible: {}", nombreClase);
        List<Horario> horarios = horarioService.findByClaseDisponible(nombreClase);
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/lunes")
    public ResponseEntity<List<Horario>> findHorariosLunes() {
        log.info("Obteniendo horarios que operan los lunes");
        List<Horario> horarios = horarioService.findHorariosLunes();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/domingo")
    public ResponseEntity<List<Horario>> findHorariosDomingo() {
        log.info("Obteniendo horarios que operan los domingos");
        List<Horario> horarios = horarioService.findHorariosDomingo();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/con-incidencias")
    public ResponseEntity<List<Horario>> findHorariosConIncidenciasActivas() {
        log.info("Obteniendo horarios con incidencias activas");
        List<Horario> horarios = horarioService.findHorariosConIncidenciasActivas();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/retrasados")
    public ResponseEntity<List<Horario>> findHorariosRetrasados() {
        log.info("Obteniendo horarios retrasados");
        List<Horario> horarios = horarioService.findHorariosRetrasados();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/cancelados")
    public ResponseEntity<List<Horario>> findHorariosCancelados() {
        log.info("Obteniendo horarios cancelados");
        List<Horario> horarios = horarioService.findHorariosCancelados();
        return ResponseEntity.ok(horarios);
    }
    
    @GetMapping("/en-marcha")
    public ResponseEntity<List<Horario>> findHorariosEnMarcha() {
        log.info("Obteniendo horarios en marcha");
        List<Horario> horarios = horarioService.findHorariosEnMarcha();
        return ResponseEntity.ok(horarios);
    }
    
    @PostMapping
    public ResponseEntity<Horario> create(@Valid @RequestBody Horario horario) {
        log.info("Creando nuevo horario: {}", horario.getCodigoServicio());
        
        if (horarioService.existsByCodigoServicio(horario.getCodigoServicio())) {
            return ResponseEntity.badRequest().build();
        }
        
        Horario nuevoHorario = horarioService.save(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Horario> update(@PathVariable String id, @Valid @RequestBody Horario horario) {
        log.info("Actualizando horario con ID: {}", id);
        
        if (!horarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Horario horarioActualizado = horarioService.update(id, horario);
        return ResponseEntity.ok(horarioActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando horario con ID: {}", id);
        
        if (!horarioService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        horarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Horario> cambiarEstado(@PathVariable String id, @RequestParam Horario.EstadoHorario nuevoEstado) {
        log.info("Cambiando estado de horario {} a: {}", id, nuevoEstado);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.cambiarEstado(id, nuevoEstado);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/conductor")
    public ResponseEntity<Horario> asignarConductor(@PathVariable String id, @RequestParam String conductorId) {
        log.info("Asignando conductor {} a horario: {}", conductorId, id);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.asignarConductor(id, conductorId);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/conductor-suplente")
    public ResponseEntity<Horario> asignarConductorSuplente(@PathVariable String id, @RequestParam String conductorSuplenteId) {
        log.info("Asignando conductor suplente {} a horario: {}", conductorSuplenteId, id);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.asignarConductorSuplente(id, conductorSuplenteId);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ocupacion")
    public ResponseEntity<Horario> actualizarOcupacion(@PathVariable String id, @RequestParam Integer pasajerosActuales) {
        log.info("Actualizando ocupación de horario {}: {} pasajeros", id, pasajerosActuales);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.actualizarOcupacion(id, pasajerosActuales);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/tarifa")
    public ResponseEntity<Horario> actualizarTarifa(@PathVariable String id, @RequestParam Double nuevaTarifa) {
        log.info("Actualizando tarifa de horario {} a: {}", id, nuevaTarifa);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.actualizarTarifa(id, nuevaTarifa);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/incidencia")
    public ResponseEntity<Horario> registrarIncidencia(@PathVariable String id, @RequestBody Horario.IncidenciaHorario incidencia) {
        log.info("Registrando incidencia en horario: {}", id);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.registrarIncidencia(id, incidencia);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/retraso")
    public ResponseEntity<Horario> registrarRetraso(
            @PathVariable String id,
            @RequestParam Integer minutosRetraso,
            @RequestParam String motivo) {
        log.info("Registrando retraso de {} minutos en horario: {}", minutosRetraso, id);
        
        return horarioService.findById(id)
            .map(horario -> {
                Horario horarioActualizado = horarioService.registrarRetraso(id, minutosRetraso, motivo);
                return ResponseEntity.ok(horarioActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoServicio}")
    public ResponseEntity<Long> getCountByTipoServicio(@PathVariable Horario.TipoServicio tipoServicio) {
        log.info("Obteniendo contador de horarios por tipo de servicio: {}", tipoServicio);
        Long count = horarioService.countByTipoServicio(tipoServicio);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<Long> getCountByEstado(@PathVariable Horario.EstadoHorario estado) {
        log.info("Obteniendo contador de horarios por estado: {}", estado);
        Long count = horarioService.countByEstado(estado);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-tren/{trenId}")
    public ResponseEntity<Long> getCountByTrenId(@PathVariable String trenId) {
        log.info("Obteniendo contador de horarios por tren: {}", trenId);
        Long count = horarioService.countByTrenId(trenId);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-ruta/{rutaId}")
    public ResponseEntity<Long> getCountByRutaId(@PathVariable String rutaId) {
        log.info("Obteniendo contador de horarios por ruta: {}", rutaId);
        Long count = horarioService.countByRutaId(rutaId);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/pasajeros-actuales")
    public ResponseEntity<Integer> getPasajerosActuales() {
        log.info("Obteniendo total de pasajeros actuales");
        Integer total = horarioService.sumPasajerosActuales();
        return ResponseEntity.ok(total != null ? total : 0);
    }
    
    @GetMapping("/estadisticas/capacidad-total")
    public ResponseEntity<Integer> getCapacidadTotal() {
        log.info("Obteniendo capacidad total de pasajeros");
        Integer total = horarioService.sumCapacidadTotal();
        return ResponseEntity.ok(total != null ? total : 0);
    }
    
    @GetMapping("/estadisticas/tarifa-promedio")
    public ResponseEntity<Double> getTarifaPromedio() {
        log.info("Obteniendo tarifa promedio");
        Double promedio = horarioService.avgTarifa();
        return ResponseEntity.ok(promedio != null ? promedio : 0.0);
    }
    
    @GetMapping("/exists/codigo/{codigoServicio}")
    public ResponseEntity<Boolean> existsByCodigoServicio(@PathVariable String codigoServicio) {
        log.info("Verificando si existe horario con código: {}", codigoServicio);
        boolean exists = horarioService.existsByCodigoServicio(codigoServicio);
        return ResponseEntity.ok(exists);
    }
}
