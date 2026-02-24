package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.dto.PagedResponse;
import com.david.trenes.model.Estacion;
import com.david.trenes.service.EstacionService;
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
 * Controller para gestión completa de estaciones.
 * Proporciona CRUD, búsquedas avanzadas, operaciones de gestión y estadísticas.
 */
@RestController
@RequestMapping("/v1/estaciones")
@RequiredArgsConstructor
@Slf4j
public class EstacionController extends BaseController {

    private final EstacionService estacionService;

    // ==================== ENDPOINTS BÁSICOS CRUD ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<Estacion>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "codigoEstacion") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logRequest("findAll", page, size, sortBy, sortDir);

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Por ahora usamos findAll sin paginación del service
            List<Estacion> estaciones = estacionService.findAll();
            return ok(estaciones, "Estaciones obtenidas exitosamente");

        } catch (Exception e) {
            logError("findAll", e);
            return badRequest("Error al listar estaciones: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Estacion>> findById(@PathVariable String id) {
        logRequest("findById", id);

        try {
            return handleOptional(estacionService.findById(id), "Estación no encontrada con ID: " + id);

        } catch (Exception e) {
            logError("findById", e);
            return badRequest("Error al buscar estación: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Estacion>> create(@Valid @RequestBody Estacion estacion) {
        logRequest("create", estacion.getCodigoEstacion());

        try {
            // Validaciones de negocio
            if (estacionService.existsByCodigoEstacion(estacion.getCodigoEstacion())) {
                return badRequest("Ya existe una estación con el código: " + estacion.getCodigoEstacion());
            }

            Estacion nuevaEstacion = estacionService.save(estacion);
            return created(nuevaEstacion, "Estación creada exitosamente");

        } catch (Exception e) {
            logError("create", e);
            return badRequest("Error al crear estación: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Estacion>> update(@PathVariable String id, @Valid @RequestBody Estacion estacion) {
        logRequest("update", id, estacion.getCodigoEstacion());

        try {
            if (!estacionService.findById(id).isPresent()) {
                return notFound("Estación no encontrada con ID: " + id);
            }

            Estacion estacionActualizada = estacionService.update(id, estacion);
            return ok(estacionActualizada, "Estación actualizada exitosamente");

        } catch (Exception e) {
            logError("update", e);
            return badRequest("Error al actualizar estación: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable String id) {
        logRequest("deleteById", id);

        try {
            if (!estacionService.findById(id).isPresent()) {
                return notFound("Estación no encontrada con ID: " + id);
            }

            estacionService.deleteById(id);
            return noContent("Estación eliminada exitosamente");

        } catch (Exception e) {
            logError("deleteById", e);
            return badRequest("Error al eliminar estación: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR IDENTIFICADORES ====================

    @GetMapping("/codigo/{codigoEstacion}")
    public ResponseEntity<ApiResponse<Estacion>> findByCodigoEstacion(@PathVariable String codigoEstacion) {
        logRequest("findByCodigoEstacion", codigoEstacion);

        try {
            return handleOptional(estacionService.findByCodigoEstacion(codigoEstacion),
                    "Estación no encontrada con código: " + codigoEstacion);

        } catch (Exception e) {
            logError("findByCodigoEstacion", e);
            return badRequest("Error al buscar por código: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR UBICACIÓN ====================

    @GetMapping("/nombre")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByNombreContaining(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logRequest("findByNombreContaining", nombre, page, size);

        try {
            List<Estacion> estaciones = estacionService.findByNombreContaining(nombre);
            return ok(estaciones, "Estaciones por nombre obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByNombreContaining", e);
            return badRequest("Error al buscar por nombre: " + e.getMessage());
        }
    }

    @GetMapping("/ciudad")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByCiudad(@RequestParam String ciudad) {
        logRequest("findByCiudad", ciudad);

        try {
            List<Estacion> estaciones = estacionService.findByCiudad(ciudad);
            return ok(estaciones, "Estaciones por ciudad obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByCiudad", e);
            return badRequest("Error al buscar por ciudad: " + e.getMessage());
        }
    }

    @GetMapping("/provincia")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByProvincia(@RequestParam String provincia) {
        logRequest("findByProvincia", provincia);

        try {
            List<Estacion> estaciones = estacionService.findByProvincia(provincia);
            return ok(estaciones, "Estaciones por provincia obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByProvincia", e);
            return badRequest("Error al buscar por provincia: " + e.getMessage());
        }
    }

    @GetMapping("/coordenadas")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByCoordenadasRango(
            @RequestParam Double latMin,
            @RequestParam Double latMax,
            @RequestParam Double lonMin,
            @RequestParam Double lonMax
    ) {
        logRequest("findByCoordenadasRango", latMin, latMax, lonMin, lonMax);

        try {
            List<Estacion> estaciones = estacionService.findByCoordenadasRango(latMin, latMax, lonMin, lonMax);
            return ok(estaciones, "Estaciones por coordenadas obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByCoordenadasRango", e);
            return badRequest("Error al buscar por coordenadas: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR CARACTERÍSTICAS ====================

    @GetMapping("/tipo/{tipoEstacion}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByTipoEstacion(@PathVariable Estacion.TipoEstacion tipoEstacion) {
        logRequest("findByTipoEstacion", tipoEstacion);

        try {
            List<Estacion> estaciones = estacionService.findByTipoEstacion(tipoEstacion);
            return ok(estaciones, "Estaciones por tipo obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByTipoEstacion", e);
            return badRequest("Error al buscar por tipo: " + e.getMessage());
        }
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByCategoria(@PathVariable Estacion.CategoriaEstacion categoria) {
        logRequest("findByCategoria", categoria);

        try {
            List<Estacion> estaciones = estacionService.findByCategoria(categoria);
            return ok(estaciones, "Estaciones por categoría obtenidas exitosamente");

        } catch (Exception e) {
            logError("findByCategoria", e);
            return badRequest("Error al buscar por categoría: " + e.getMessage());
        }
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<Estacion>>> findEstacionesActivas() {
        logRequest("findEstacionesActivas");

        try {
            List<Estacion> estaciones = estacionService.findEstacionesActivas();
            return ok(estaciones, "Estaciones activas obtenidas exitosamente");

        } catch (Exception e) {
            logError("findEstacionesActivas", e);
            return badRequest("Error al obtener estaciones activas: " + e.getMessage());
        }
    }

    @GetMapping("/accesibles")
    public ResponseEntity<ApiResponse<List<Estacion>>> findEstacionesAccesibles() {
        logRequest("findEstacionesAccesibles");

        try {
            List<Estacion> estaciones = estacionService.findEstacionesAccesibles();
            return ok(estaciones, "Estaciones accesibles obtenidas exitosamente");

        } catch (Exception e) {
            logError("findEstacionesAccesibles", e);
            return badRequest("Error al obtener estaciones accesibles: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA POR CAPACIDADES ====================

    @GetMapping("/capacidad-estacionamiento-minima/{capacidad}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByCapacidadEstacionamientoMinima(@PathVariable Integer capacidad) {
        logRequest("findByCapacidadEstacionamientoMinima", capacidad);
        try {
            List<Estacion> estaciones = estacionService.findByCapacidadEstacionamientoMinima(capacidad);
            return ok(estaciones, "Estaciones por capacidad obtenidas");
        } catch (Exception e) {
            logError("findByCapacidadEstacionamientoMinima", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/numero-andenes/{numeroAndenes}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByNumeroAndenes(@PathVariable Integer numeroAndenes) {
        logRequest("findByNumeroAndenes", numeroAndenes);
        try {
            List<Estacion> estaciones = estacionService.findByNumeroAndenes(numeroAndenes);
            return ok(estaciones, "Estaciones por andenes obtenidas");
        } catch (Exception e) {
            logError("findByNumeroAndenes", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/servicio/{tipoServicio}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByServicioDisponible(@PathVariable Estacion.TipoServicio tipoServicio) {
        logRequest("findByServicioDisponible", tipoServicio);
        try {
            List<Estacion> estaciones = estacionService.findByServicioDisponible(tipoServicio);
            return ok(estaciones, "Estaciones por servicio obtenidas");
        } catch (Exception e) {
            logError("findByServicioDisponible", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/anden/{tipoAnden}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findByAndenTipoDisponible(@PathVariable Estacion.TipoAnden tipoAnden) {
        logRequest("findByAndenTipoDisponible", tipoAnden);
        try {
            List<Estacion> estaciones = estacionService.findByAndenTipoDisponible(tipoAnden);
            return ok(estaciones, "Estaciones por andén obtenidas");
        } catch (Exception e) {
            logError("findByAndenTipoDisponible", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<ApiResponse<List<Estacion>>> findBySupervisorId(@PathVariable String supervisorId) {
        logRequest("findBySupervisorId", supervisorId);
        try {
            List<Estacion> estaciones = estacionService.findBySupervisorId(supervisorId);
            return ok(estaciones, "Estaciones por supervisor obtenidas");
        } catch (Exception e) {
            logError("findBySupervisorId", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE GESTIÓN ====================

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Estacion>> cambiarEstado(@PathVariable String id, @RequestParam boolean activo) {
        logRequest("cambiarEstado", id, activo);
        try {
            Optional<Estacion> opt = estacionService.findById(id);
            if (!opt.isPresent()) return notFound("No encontrada");
            Estacion actualizada = estacionService.cambiarEstado(id, activo);
            return ok(actualizada, "Estado cambiado");
        } catch (Exception e) {
            logError("cambiarEstado", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/personal")
    public ResponseEntity<ApiResponse<Estacion>> actualizarPersonal(@PathVariable String id, @RequestParam Integer personalActivo) {
        logRequest("actualizarPersonal", id, personalActivo);
        try {
            Optional<Estacion> opt = estacionService.findById(id);
            if (!opt.isPresent()) return notFound("No encontrada");
            Estacion actualizada = estacionService.actualizarPersonal(id, personalActivo);
            return ok(actualizada, "Personal actualizado");
        } catch (Exception e) {
            logError("actualizarPersonal", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/supervisor")
    public ResponseEntity<ApiResponse<Estacion>> asignarSupervisor(@PathVariable String id, @RequestParam String supervisorId) {
        logRequest("asignarSupervisor", id, supervisorId);
        try {
            Optional<Estacion> opt = estacionService.findById(id);
            if (!opt.isPresent()) return notFound("No encontrada");
            Estacion actualizada = estacionService.asignarSupervisor(id, supervisorId);
            return ok(actualizada, "Supervisor asignado");
        } catch (Exception e) {
            logError("asignarSupervisor", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE ESTADÍSTICAS ====================

    @GetMapping("/estadisticas/generales")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEstadisticasGenerales() {
        logRequest("getEstadisticasGenerales");
        try {
            Long activas = estacionService.countActivas();
            Long accesibles = estacionService.countAccesibles();
            var stats = Map.<String, Object>of("activas", activas != null ? activas : 0L, "accesibles", accesibles != null ? accesibles : 0L, "timestamp", java.time.LocalDateTime.now());
            return ok(stats, "Estadísticas obtenidas");
        } catch (Exception e) {
            logError("getEstadisticasGenerales", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/contador-por-tipo/{tipoEstacion}")
    public ResponseEntity<ApiResponse<Long>> getCountByTipoEstacion(@PathVariable Estacion.TipoEstacion tipoEstacion) {
        logRequest("getCountByTipoEstacion", tipoEstacion);
        try {
            Long count = estacionService.countByTipoEstacion(tipoEstacion);
            return ok(count != null ? count : 0L, "Contador obtenido");
        } catch (Exception e) {
            logError("getCountByTipoEstacion", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/contador-activas")
    public ResponseEntity<ApiResponse<Long>> getCountActivas() {
        logRequest("getCountActivas");
        try {
            Long count = estacionService.countActivas();
            return ok(count != null ? count : 0L, "Contador obtenido");
        } catch (Exception e) {
            logError("getCountActivas", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/exists/codigo/{codigoEstacion}")
    public ResponseEntity<ApiResponse<Boolean>> existsByCodigoEstacion(@PathVariable String codigoEstacion) {
        logRequest("existsByCodigoEstacion", codigoEstacion);
        try {
            boolean exists = estacionService.existsByCodigoEstacion(codigoEstacion);
            return ok(exists, "Verificación completada");
        } catch (Exception e) {
            logError("existsByCodigoEstacion", e);
            return badRequest("Error: " + e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Estacion>>> buscar(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        logRequest("buscar", query, page, size);
        try {
            return ok(List.of(), "Búsqueda completada (pendiente)");
        } catch (Exception e) {
            logError("buscar", e);
            return badRequest("Error: " + e.getMessage());
        }
    }
}
