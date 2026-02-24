package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.dto.PagedResponse;
import com.david.trenes.model.Ruta;
import com.david.trenes.service.RutaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller para gestión completa de rutas.
 * Proporciona CRUD, búsquedas avanzadas, operaciones de gestión y estadísticas.
 */
@RestController
@RequestMapping("/v1/rutas")
@RequiredArgsConstructor
@Slf4j
public class RutaController extends BaseController {

    private final RutaService rutaService;

    // ==================== ENDPOINTS BÁSICOS CRUD ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<Ruta>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "codigoRuta") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logRequest("findAll", page, size, sortBy, sortDir);

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            // Por ahora usamos findAll sin paginación del service
            List<Ruta> rutas = rutaService.findAll();
            return ok(rutas, "Rutas obtenidas exitosamente");

        } catch (Exception e) {
            logError("findAll", e);
            return badRequest("Error al listar rutas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Ruta>> findById(@PathVariable String id) {
        logRequest("findById", id);

        try {
            return handleOptional(rutaService.findById(id), "Ruta no encontrada con ID: " + id);

        } catch (Exception e) {
            logError("findById", e);
            return badRequest("Error al buscar ruta: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Ruta>> create(@Valid @RequestBody Ruta ruta) {
        logRequest("create", ruta.getCodigoRuta());

        try {
            // Validaciones de negocio
            if (rutaService.existsByCodigoRuta(ruta.getCodigoRuta())) {
                return badRequest("Ya existe una ruta con el código: " + ruta.getCodigoRuta());
            }

            Ruta nuevaRuta = rutaService.save(ruta);
            return created(nuevaRuta, "Ruta creada exitosamente");

        } catch (Exception e) {
            logError("create", e);
            return badRequest("Error al crear ruta: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Ruta>> update(@PathVariable String id, @Valid @RequestBody Ruta ruta) {
        logRequest("update", id, ruta.getCodigoRuta());

        try {
            if (!rutaService.findById(id).isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }

            Ruta rutaActualizada = rutaService.update(id, ruta);
            return ok(rutaActualizada, "Ruta actualizada exitosamente");

        } catch (Exception e) {
            logError("update", e);
            return badRequest("Error al actualizar ruta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable String id) {
        logRequest("deleteById", id);

        try {
            if (!rutaService.findById(id).isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }

            rutaService.deleteById(id);
            return noContent("Ruta eliminada exitosamente");

        } catch (Exception e) {
            logError("deleteById", e);
            return badRequest("Error al eliminar ruta: " + e.getMessage());
        }
    }
    
    // ==================== ENDPOINTS DE BÚSQUEDA POR IDENTIFICADORES ====================

    @GetMapping("/codigo/{codigoRuta}")
    public ResponseEntity<ApiResponse<Ruta>> findByCodigoRuta(@PathVariable String codigoRuta) {
        logRequest("findByCodigoRuta", codigoRuta);

        try {
            return handleOptional(rutaService.findByCodigoRuta(codigoRuta), 
                    "Ruta no encontrada con código: " + codigoRuta);

        } catch (Exception e) {
            logError("findByCodigoRuta", e);
            return badRequest("Error al buscar por código: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR CARACTERÍSTICAS ====================

    @GetMapping("/nombre")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByNombreContaining(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logRequest("findByNombreContaining", nombre, page, size);

        try {
            List<Ruta> rutas = rutaService.findByNombreContaining(nombre);
            return ok(rutas, "Rutas por nombre obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByNombreContaining", e);
            return badRequest("Error al buscar por nombre: " + e.getMessage());
        }
    }

    @GetMapping("/tipo/{tipoRuta}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByTipoRuta(@PathVariable Ruta.TipoRuta tipoRuta) {
        logRequest("findByTipoRuta", tipoRuta);

        try {
            List<Ruta> rutas = rutaService.findByTipoRuta(tipoRuta);
            return ok(rutas, "Rutas por tipo obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByTipoRuta", e);
            return badRequest("Error al buscar por tipo: " + e.getMessage());
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByEstado(@PathVariable Ruta.EstadoRuta estado) {
        logRequest("findByEstado", estado);

        try {
            List<Ruta> rutas = rutaService.findByEstado(estado);
            return ok(rutas, "Rutas por estado obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByEstado", e);
            return badRequest("Error al buscar por estado: " + e.getMessage());
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<Ruta>>> findRutasActivas() {
        logRequest("findRutasActivas");

        try {
            List<Ruta> rutas = rutaService.findRutasActivas();
            return ok(rutas, "Rutas activas obtenidas exitosamente");

        } catch (Exception e) {
            logError("findRutasActivas", e);
            return badRequest("Error al obtener rutas activas: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR ESTACIONES ====================

    @GetMapping("/entre-estaciones")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByEstaciones(
            @RequestParam String estacionOrigenId,
            @RequestParam String estacionDestinoId
    ) {
        logRequest("findByEstaciones", estacionOrigenId, estacionDestinoId);

        try {
            List<Ruta> rutas = rutaService.findByEstaciones(estacionOrigenId, estacionDestinoId);
            return ok(rutas, "Rutas entre estaciones obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByEstaciones", e);
            return badRequest("Error al buscar rutas entre estaciones: " + e.getMessage());
        }
    }

    @GetMapping("/origen/{estacionOrigenId}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByEstacionOrigen(@PathVariable String estacionOrigenId) {
        logRequest("findByEstacionOrigen", estacionOrigenId);

        try {
            List<Ruta> rutas = rutaService.findByEstacionOrigen(estacionOrigenId);
            return ok(rutas, "Rutas por origen obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByEstacionOrigen", e);
            return badRequest("Error al buscar por estación de origen: " + e.getMessage());
        }
    }

    @GetMapping("/destino/{estacionDestinoId}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByEstacionDestino(@PathVariable String estacionDestinoId) {
        logRequest("findByEstacionDestino", estacionDestinoId);

        try {
            List<Ruta> rutas = rutaService.findByEstacionDestino(estacionDestinoId);
            return ok(rutas, "Rutas por destino obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByEstacionDestino", e);
            return badRequest("Error al buscar por estación de destino: " + e.getMessage());
        }
    }

    @GetMapping("/intermedia/{estacionId}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByEstacionIntermedia(@PathVariable String estacionId) {
        logRequest("findByEstacionIntermedia", estacionId);

        try {
            List<Ruta> rutas = rutaService.findByEstacionIntermedia(estacionId);
            return ok(rutas, "Rutas por estación intermedia obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByEstacionIntermedia", e);
            return badRequest("Error al buscar por estación intermedia: " + e.getMessage());
        }
    }
    
    // ==================== ENDPOINTS DE BÚSQUEDA POR VÍAS Y MEDIDAS ====================

    @GetMapping("/via/{viaId}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByViaId(@PathVariable String viaId) {
        logRequest("findByViaId", viaId);

        try {
            List<Ruta> rutas = rutaService.findByViaId(viaId);
            return ok(rutas, "Rutas por vía obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByViaId", e);
            return badRequest("Error al buscar por vía: " + e.getMessage());
        }
    }

    @GetMapping("/distancia-minima/{distanciaMinima}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByDistanciaMinima(@PathVariable Double distanciaMinima) {
        logRequest("findByDistanciaMinima", distanciaMinima);

        try {
            List<Ruta> rutas = rutaService.findByDistanciaMinima(distanciaMinima);
            return ok(rutas, "Rutas por distancia mínima obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByDistanciaMinima", e);
            return badRequest("Error al buscar por distancia mínima: " + e.getMessage());
        }
    }

    @GetMapping("/distancia-maxima/{distanciaMaxima}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByDistanciaMaxima(@PathVariable Double distanciaMaxima) {
        logRequest("findByDistanciaMaxima", distanciaMaxima);

        try {
            List<Ruta> rutas = rutaService.findByDistanciaMaxima(distanciaMaxima);
            return ok(rutas, "Rutas por distancia máxima obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByDistanciaMaxima", e);
            return badRequest("Error al buscar por distancia máxima: " + e.getMessage());
        }
    }

    @GetMapping("/tiempo-maximo/{tiempoMaximo}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByTiempoMaximo(@PathVariable Integer tiempoMaximo) {
        logRequest("findByTiempoMaximo", tiempoMaximo);

        try {
            List<Ruta> rutas = rutaService.findByTiempoMaximo(tiempoMaximo);
            return ok(rutas, "Rutas por tiempo máximo obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByTiempoMaximo", e);
            return badRequest("Error al buscar por tiempo máximo: " + e.getMessage());
        }
    }

    @GetMapping("/velocidad-minima/{velocidadMinima}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByVelocidadPromedioMinima(@PathVariable Double velocidadMinima) {
        logRequest("findByVelocidadPromedioMinima", velocidadMinima);

        try {
            List<Ruta> rutas = rutaService.findByVelocidadPromedioMinima(velocidadMinima);
            return ok(rutas, "Rutas por velocidad mínima obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByVelocidadPromedioMinima", e);
            return badRequest("Error al buscar por velocidad mínima: " + e.getMessage());
        }
    }

    @GetMapping("/prioridad-minima/{prioridadMinima}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByPrioridadMinima(@PathVariable Integer prioridadMinima) {
        logRequest("findByPrioridadMinima", prioridadMinima);

        try {
            List<Ruta> rutas = rutaService.findByPrioridadMinima(prioridadMinima);
            return ok(rutas, "Rutas por prioridad mínima obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByPrioridadMinima", e);
            return badRequest("Error al buscar por prioridad mínima: " + e.getMessage());
        }
    }

    @GetMapping("/tarifa-maxima/{tarifaMaxima}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByTarifaMaxima(@PathVariable Double tarifaMaxima) {
        logRequest("findByTarifaMaxima", tarifaMaxima);

        try {
            List<Ruta> rutas = rutaService.findByTarifaMaxima(tarifaMaxima);
            return ok(rutas, "Rutas por tarifa máxima obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByTarifaMaxima", e);
            return badRequest("Error al buscar por tarifa máxima: " + e.getMessage());
        }
    }
    
    // ==================== ENDPOINTS DE BÚSQUEDA POR ZONAS Y HORARIOS ====================

    @GetMapping("/zona/{zona}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByZona(@PathVariable String zona) {
        logRequest("findByZona", zona);

        try {
            List<Ruta> rutas = rutaService.findByZona(zona);
            return ok(rutas, "Rutas por zona obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByZona", e);
            return badRequest("Error al buscar por zona: " + e.getMessage());
        }
    }

    @GetMapping("/lunes")
    public ResponseEntity<ApiResponse<List<Ruta>>> findRutasLunes() {
        logRequest("findRutasLunes");

        try {
            List<Ruta> rutas = rutaService.findRutasLunes();
            return ok(rutas, "Rutas de lunes obtenidas exitosamente");

        } catch (Exception e) {
            logError("findRutasLunes", e);
            return badRequest("Error al obtener rutas de lunes: " + e.getMessage());
        }
    }

    @GetMapping("/domingo")
    public ResponseEntity<ApiResponse<List<Ruta>>> findRutasDomingo() {
        logRequest("findRutasDomingo");

        try {
            List<Ruta> rutas = rutaService.findRutasDomingo();
            return ok(rutas, "Rutas de domingo obtenidas exitosamente");

        } catch (Exception e) {
            logError("findRutasDomingo", e);
            return badRequest("Error al obtener rutas de domingo: " + e.getMessage());
        }
    }

    @GetMapping("/servicios-dia-minimo/{serviciosMinimo}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByServiciosDiaMinimo(@PathVariable Integer serviciosMinimo) {
        logRequest("findByServiciosDiaMinimo", serviciosMinimo);

        try {
            List<Ruta> rutas = rutaService.findByServiciosDiaMinimo(serviciosMinimo);
            return ok(rutas, "Rutas por servicios diarios obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByServiciosDiaMinimo", e);
            return badRequest("Error al buscar por servicios diarios: " + e.getMessage());
        }
    }

    @GetMapping("/numero-paradas/{numeroParadas}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByNumeroParadas(@PathVariable Integer numeroParadas) {
        logRequest("findByNumeroParadas", numeroParadas);

        try {
            List<Ruta> rutas = rutaService.findByNumeroParadas(numeroParadas);
            return ok(rutas, "Rutas por número de paradas obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByNumeroParadas", e);
            return badRequest("Error al buscar por número de paradas: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR RESTRICCIONES ====================

    @GetMapping("/restriccion/{tipoRestriccion}")
    public ResponseEntity<ApiResponse<List<Ruta>>> findByRestriccionActiva(@PathVariable Ruta.TipoRestriccion tipoRestriccion) {
        logRequest("findByRestriccionActiva", tipoRestriccion);

        try {
            List<Ruta> rutas = rutaService.findByRestriccionActiva(tipoRestriccion);
            return ok(rutas, "Rutas por restricción obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByRestriccionActiva", e);
            return badRequest("Error al buscar por restricción: " + e.getMessage());
        }
    }

    @GetMapping("/alternativas")
    public ResponseEntity<ApiResponse<List<Ruta>>> buscarRutasAlternativas(
            @RequestParam String estacionOrigenId,
            @RequestParam String estacionDestinoId,
            @RequestParam Ruta.TipoRuta tipoRuta
    ) {
        logRequest("buscarRutasAlternativas", estacionOrigenId, estacionDestinoId, tipoRuta);

        try {
            List<Ruta> rutas = rutaService.buscarRutasAlternativas(estacionOrigenId, estacionDestinoId, tipoRuta);
            return ok(rutas, "Rutas alternativas obtenidas exitosamente");

        } catch (Exception e) {
            logError("buscarRutasAlternativas", e);
            return badRequest("Error al buscar rutas alternativas: " + e.getMessage());
        }
    }
    
    // ==================== ENDPOINTS DE GESTIÓN OPERATIVA ====================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Ruta>> cambiarEstado(@PathVariable String id, @RequestParam Ruta.EstadoRuta nuevoEstado) {
        logRequest("cambiarEstado", id, nuevoEstado);

        try {
            Optional<Ruta> rutaOpt = rutaService.findById(id);
            if (!rutaOpt.isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }
            
            try {
                Ruta rutaActualizada = rutaService.cambiarEstado(id, nuevoEstado);
                return ok(rutaActualizada, "Estado cambiado exitosamente");
            } catch (Exception e) {
                return badRequest("Error al cambiar estado: " + e.getMessage());
            }

        } catch (Exception e) {
            logError("cambiarEstado", e);
            return badRequest("Error al cambiar estado: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/tarifa")
    public ResponseEntity<ApiResponse<Ruta>> actualizarTarifa(@PathVariable String id, @RequestParam Double nuevaTarifa) {
        logRequest("actualizarTarifa", id, nuevaTarifa);

        try {
            Optional<Ruta> rutaOpt = rutaService.findById(id);
            if (!rutaOpt.isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }
            
            try {
                Ruta rutaActualizada = rutaService.actualizarTarifa(id, nuevaTarifa);
                return ok(rutaActualizada, "Tarifa actualizada exitosamente");
            } catch (Exception e) {
                return badRequest("Error al actualizar tarifa: " + e.getMessage());
            }

        } catch (Exception e) {
            logError("actualizarTarifa", e);
            return badRequest("Error al actualizar tarifa: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/prioridad")
    public ResponseEntity<ApiResponse<Ruta>> actualizarPrioridad(@PathVariable String id, @RequestParam Integer nuevaPrioridad) {
        logRequest("actualizarPrioridad", id, nuevaPrioridad);

        try {
            Optional<Ruta> rutaOpt = rutaService.findById(id);
            if (!rutaOpt.isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }
            
            try {
                Ruta rutaActualizada = rutaService.actualizarPrioridad(id, nuevaPrioridad);
                return ok(rutaActualizada, "Prioridad actualizada exitosamente");
            } catch (Exception e) {
                return badRequest("Error al actualizar prioridad: " + e.getMessage());
            }

        } catch (Exception e) {
            logError("actualizarPrioridad", e);
            return badRequest("Error al actualizar prioridad: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/restriccion")
    public ResponseEntity<ApiResponse<Ruta>> agregarRestriccion(@PathVariable String id, @RequestBody Ruta.RestriccionRuta restriccion) {
        logRequest("agregarRestriccion", id);

        try {
            Optional<Ruta> rutaOpt = rutaService.findById(id);
            if (!rutaOpt.isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }
            
            try {
                Ruta rutaActualizada = rutaService.agregarRestriccion(id, restriccion);
                return ok(rutaActualizada, "Restricción agregada exitosamente");
            } catch (Exception e) {
                return badRequest("Error al agregar restricción: " + e.getMessage());
            }

        } catch (Exception e) {
            logError("agregarRestriccion", e);
            return badRequest("Error al agregar restricción: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/restriccion/{tipoRestriccion}")
    public ResponseEntity<ApiResponse<Ruta>> removerRestriccion(@PathVariable String id, @PathVariable String tipoRestriccion) {
        logRequest("removerRestriccion", id, tipoRestriccion);

        try {
            Optional<Ruta> rutaOpt = rutaService.findById(id);
            if (!rutaOpt.isPresent()) {
                return notFound("Ruta no encontrada con ID: " + id);
            }
            
            try {
                Ruta rutaActualizada = rutaService.removerRestriccion(id, tipoRestriccion);
                return ok(rutaActualizada, "Restricción removida exitosamente");
            } catch (Exception e) {
                return badRequest("Error al remover restricción: " + e.getMessage());
            }

        } catch (Exception e) {
            logError("removerRestriccion", e);
            return badRequest("Error al remover restricción: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE ESTADÍSTICAS ====================

    @GetMapping("/estadisticas/generales")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEstadisticasGenerales() {
        logRequest("getEstadisticasGenerales");

        try {
            Long countActivas = rutaService.countActivas();
            Double distanciaTotal = rutaService.sumDistanciaTotal();
            Double tiempoPromedio = rutaService.avgTiempoEstimado();

            var estadisticas = Map.<String, Object>of(
                    "totalActivas", countActivas != null ? countActivas : 0L,
                    "distanciaTotal", distanciaTotal != null ? distanciaTotal : 0.0,
                    "tiempoPromedio", tiempoPromedio != null ? tiempoPromedio : 0.0,
                    "timestamp", java.time.LocalDateTime.now()
            );

            return ok(estadisticas, "Estadísticas generales obtenidas exitosamente");

        } catch (Exception e) {
            logError("getEstadisticasGenerales", e);
            return badRequest("Error al obtener estadísticas generales: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/contador-por-tipo/{tipoRuta}")
    public ResponseEntity<ApiResponse<Long>> getCountByTipoRuta(@PathVariable Ruta.TipoRuta tipoRuta) {
        logRequest("getCountByTipoRuta", tipoRuta);

        try {
            Long count = rutaService.countByTipoRuta(tipoRuta);
            return ok(count != null ? count : 0L, "Contador por tipo obtenido exitosamente");

        } catch (Exception e) {
            logError("getCountByTipoRuta", e);
            return badRequest("Error al obtener contador por tipo: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<ApiResponse<Long>> getCountByEstado(@PathVariable Ruta.EstadoRuta estado) {
        logRequest("getCountByEstado", estado);

        try {
            Long count = rutaService.countByEstado(estado);
            return ok(count != null ? count : 0L, "Contador por estado obtenido exitosamente");

        } catch (Exception e) {
            logError("getCountByEstado", e);
            return badRequest("Error al obtener contador por estado: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/contador-activas")
    public ResponseEntity<ApiResponse<Long>> getCountActivas() {
        logRequest("getCountActivas");

        try {
            Long count = rutaService.countActivas();
            return ok(count != null ? count : 0L, "Contador de activas obtenido exitosamente");

        } catch (Exception e) {
            logError("getCountActivas", e);
            return badRequest("Error al obtener contador de activas: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/distancia-total")
    public ResponseEntity<ApiResponse<Double>> getDistanciaTotal() {
        logRequest("getDistanciaTotal");

        try {
            Double total = rutaService.sumDistanciaTotal();
            return ok(total != null ? total : 0.0, "Distancia total obtenida exitosamente");

        } catch (Exception e) {
            logError("getDistanciaTotal", e);
            return badRequest("Error al obtener distancia total: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/distancia-por-estado/{estado}")
    public ResponseEntity<ApiResponse<Double>> getDistanciaPorEstado(@PathVariable Ruta.EstadoRuta estado) {
        logRequest("getDistanciaPorEstado", estado);

        try {
            Double distancia = rutaService.sumDistanciaPorEstado(estado);
            return ok(distancia != null ? distancia : 0.0, "Distancia por estado obtenida exitosamente");

        } catch (Exception e) {
            logError("getDistanciaPorEstado", e);
            return badRequest("Error al obtener distancia por estado: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/tiempo-promedio")
    public ResponseEntity<ApiResponse<Double>> getTiempoPromedio() {
        logRequest("getTiempoPromedio");

        try {
            Double promedio = rutaService.avgTiempoEstimado();
            return ok(promedio != null ? promedio : 0.0, "Tiempo promedio obtenido exitosamente");

        } catch (Exception e) {
            logError("getTiempoPromedio", e);
            return badRequest("Error al obtener tiempo promedio: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE VERIFICACIÓN ====================

    @GetMapping("/exists/codigo/{codigoRuta}")
    public ResponseEntity<ApiResponse<Boolean>> existsByCodigoRuta(@PathVariable String codigoRuta) {
        logRequest("existsByCodigoRuta", codigoRuta);

        try {
            boolean exists = rutaService.existsByCodigoRuta(codigoRuta);
            return ok(exists, "Verificación por código completada");

        } catch (Exception e) {
            logError("existsByCodigoRuta", e);
            return badRequest("Error al verificar por código: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA AVANZADA ====================

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Ruta>>> buscar(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logRequest("buscar", query, page, size);

        try {
            // TODO: Implementar búsqueda avanzada en RutaService
            return ok(List.of(), "Búsqueda completada (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("buscar", e);
            return badRequest("Error en búsqueda: " + e.getMessage());
        }
    }
}
