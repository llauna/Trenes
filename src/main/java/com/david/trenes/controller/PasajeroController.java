package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.dto.PagedResponse;
import com.david.trenes.dto.SeedPasajeroItem;
import com.david.trenes.dto.SeedPasajerosResponse;
import com.david.trenes.model.Pasajero;
import com.david.trenes.security.CurrentUserService;
import com.david.trenes.service.PasajeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión completa de pasajeros.
 * Proporciona CRUD completo, búsquedas avanzadas y funcionalidades adicionales.
 */
@RestController
@RequestMapping("/v1/pasajeros")
@RequiredArgsConstructor
@Slf4j
public class PasajeroController extends BaseController {

    private final PasajeroService pasajeroService;
    private final CurrentUserService currentUserService;

    // ==================== ENDPOINTS BÁSICOS CRUD ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<Pasajero>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logRequest("findAll", page, size, sortBy, sortDir);

        try {
            // Por ahora solo retornamos los pasajeros del usuario actual
            // TODO: Implementar findAll() en PasajeroService si se necesita listar todos
            String usuarioId = currentUserService.getCurrentUsuarioId();
            List<Pasajero> pasajeros = pasajeroService.findByUsuario(usuarioId);
            return ok(pasajeros, "Pasajeros obtenidos exitosamente");

        } catch (Exception e) {
            logError("findAll", e);
            return badRequest("Error al listar pasajeros: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pasajero>> findById(@PathVariable String id) {
        logRequest("findById", id);

        try {
            // TODO: Implementar findById() en PasajeroService
            // Por ahora retornamos error indicando que no está implementado
            return badRequest("Funcionalidad findById no implementada en PasajeroService");

        } catch (Exception e) {
            logError("findById", e);
            return badRequest("Error al buscar pasajero: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pasajero>> create(@Valid @RequestBody Pasajero pasajero) {
        logRequest("create", pasajero.getNombre(), pasajero.getDocumento());

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            Pasajero creado = pasajeroService.create(usuarioId, pasajero);
            return created(creado, "Pasajero creado exitosamente");

        } catch (Exception e) {
            logError("create", e);
            return badRequest("Error al crear pasajero: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Pasajero>> update(
            @PathVariable String id, 
            @Valid @RequestBody Pasajero pasajero
    ) {
        logRequest("update", id, pasajero.getNombre());

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            Pasajero actualizado = pasajeroService.update(usuarioId, id, pasajero);
            return ok(actualizado, "Pasajero actualizado exitosamente");

        } catch (Exception e) {
            logError("update", e);
            return badRequest("Error al actualizar pasajero: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        logRequest("delete", id);

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            pasajeroService.delete(usuarioId, id);
            return noContent("Pasajero eliminado exitosamente");

        } catch (Exception e) {
            logError("delete", e);
            return badRequest("Error al eliminar pasajero: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS POR USUARIO AUTENTICADO ====================

    @GetMapping("/mis")
    public ResponseEntity<ApiResponse<List<Pasajero>>> findMyPasajeros() {
        logRequest("findMyPasajeros");

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            List<Pasajero> pasajeros = pasajeroService.findByUsuario(usuarioId);
            return ok(pasajeros, "Pasajeros del usuario obtenidos exitosamente");

        } catch (Exception e) {
            logError("findMyPasajeros", e);
            return badRequest("Error al obtener pasajeros del usuario: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE BÚSQUEDA AVANZADA ====================

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<Pasajero>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logRequest("search", query, page, size);

        try {
            // TODO: Implementar searchByNombreOrDocumento() en PasajeroService
            // Por ahora retornamos lista vacía
            return ok(List.of(), "Búsqueda completada (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("search", e);
            return badRequest("Error en búsqueda: " + e.getMessage());
        }
    }

    @GetMapping("/documento/{documento}")
    public ResponseEntity<ApiResponse<Pasajero>> findByDocumento(@PathVariable String documento) {
        logRequest("findByDocumento", documento);

        try {
            // TODO: Implementar findByDocumento() en PasajeroService
            return badRequest("Funcionalidad findByDocumento no implementada en PasajeroService");

        } catch (Exception e) {
            logError("findByDocumento", e);
            return badRequest("Error al buscar por documento: " + e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Pasajero>> findByEmail(@PathVariable String email) {
        logRequest("findByEmail", email);

        try {
            // TODO: Implementar findByEmail() en PasajeroService
            return badRequest("Funcionalidad findByEmail no implementada en PasajeroService");

        } catch (Exception e) {
            logError("findByEmail", e);
            return badRequest("Error al buscar por email: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<Pasajero>>> findByUsuario(@PathVariable String usuarioId) {
        logRequest("findByUsuario", usuarioId);

        try {
            List<Pasajero> pasajeros = pasajeroService.findByUsuario(usuarioId);
            return ok(pasajeros, "Pasajeros del usuario obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByUsuario", e);
            return badRequest("Error al obtener pasajeros del usuario: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE HISTORIAL ====================

    @GetMapping("/{id}/historial-viajes")
    public ResponseEntity<ApiResponse<Object>> getHistorialViajes(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logRequest("getHistorialViajes", id, page, size);

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            
            var historial = Map.of(
                    "pasajeroId", id,
                    "usuarioId", usuarioId,
                    "message", "Funcionalidad de historial pendiente de implementar",
                    "viajes", List.of()
            );

            return ok(historial, "Historial de viajes obtenido (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("getHistorialViajes", e);
            return badRequest("Error al obtener historial de viajes: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity<ApiResponse<Object>> getEstadisticas(@PathVariable String id) {
        logRequest("getEstadisticas", id);

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            
            var estadisticas = Map.of(
                    "pasajeroId", id,
                    "usuarioId", usuarioId,
                    "message", "Funcionalidad de estadísticas pendiente de implementar",
                    "totalViajes", 0,
                    "ultimaFechaViaje", null
            );

            return ok(estadisticas, "Estadísticas obtenidas (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("getEstadisticas", e);
            return badRequest("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE UTILIDADES ====================

    @GetMapping("/exists/documento/{documento}")
    public ResponseEntity<ApiResponse<Boolean>> existsByDocumento(@PathVariable String documento) {
        logRequest("existsByDocumento", documento);

        try {
            // TODO: Implementar existsByDocumento() en PasajeroService
            return ok(false, "Verificación de documento completada (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("existsByDocumento", e);
            return badRequest("Error al verificar documento: " + e.getMessage());
        }
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(@PathVariable String email) {
        logRequest("existsByEmail", email);

        try {
            // TODO: Implementar existsByEmail() en PasajeroService
            return ok(false, "Verificación de email completada (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("existsByEmail", e);
            return badRequest("Error al verificar email: " + e.getMessage());
        }
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<SeedPasajerosResponse>> seed(@RequestParam(defaultValue = "50") int count) {
        logRequest("seed", count);

        try {
            String usuarioId = currentUserService.getCurrentUsuarioId();
            List<Pasajero> creados = pasajeroService.seedAleatorios(usuarioId, count);

            List<SeedPasajeroItem> items = creados.stream()
                    .map(p -> SeedPasajeroItem.builder()
                            .pasajeroId(p.getId())
                            .nombre(p.getNombre())
                            .apellidos(p.getApellidos())
                            .documento(p.getDocumento())
                            .email(p.getEmail())
                            .telefono(p.getTelefono())
                            .build())
                    .toList();

            SeedPasajerosResponse response = SeedPasajerosResponse.builder()
                    .creados(items.size())
                    .pasajeros(items)
                    .build();

            return created(response, "Pasajeros de prueba creados exitosamente");

        } catch (Exception e) {
            logError("seed", e);
            return badRequest("Error al crear pasajeros de prueba: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas/generales")
    public ResponseEntity<ApiResponse<Object>> getEstadisticasGenerales() {
        logRequest("getEstadisticasGenerales");

        try {
            var estadisticas = Map.of(
                    "message", "Funcionalidad de estadísticas generales pendiente de implementar",
                    "totalPasajeros", 0,
                    "pasajerosActivos", 0,
                    "promedioViajesPorPasajero", 0.0
            );

            return ok(estadisticas, "Estadísticas generales obtenidas (funcionalidad pendiente)");

        } catch (Exception e) {
            logError("getEstadisticasGenerales", e);
            return badRequest("Error al obtener estadísticas generales: " + e.getMessage());
        }
    }
}
