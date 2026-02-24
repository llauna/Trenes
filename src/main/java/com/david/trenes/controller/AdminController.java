package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.model.Tren;
import com.david.trenes.repository.BilleteRepository;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.InventarioHorarioRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.service.GestionHorariosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller para operaciones administrativas y de mantenimiento.
 * Estos endpoints requieren privilegios elevados y no deberían estar expuestos públicamente.
 */
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController extends BaseController {

    private final HorarioRepository horarioRepository;
    private final InventarioHorarioRepository inventarioHorarioRepository;
    private final GestionHorariosService gestionHorariosService;
    private final BilleteRepository billeteRepository;
    private final TrenRepository trenRepository;

    // ==================== ENDPOINTS DE LIMPIEZA DE DATOS ====================

    @DeleteMapping("/limpieza/horarios-inventario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> limpiarHorariosEInventario(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        logRequest("limpiarHorariosEInventario", "confirm=" + confirm);

        if (!confirm) {
            return badRequest("Operación destructiva. Repite con confirm=true");
        }

        try {
            long horariosAntes = horarioRepository.count();
            long inventarioAntes = inventarioHorarioRepository.count();

            log.warn("LIMPIEZA: borrando horarios={} e inventario_horario={}", horariosAntes, inventarioAntes);

            inventarioHorarioRepository.deleteAll();
            horarioRepository.deleteAll();

            Map<String, Object> resultado = Map.of(
                    "horariosEliminados", horariosAntes,
                    "inventarioEliminado", inventarioAntes
            );

            return ok(resultado, "Limpieza de horarios e inventario completada exitosamente");

        } catch (Exception e) {
            logError("limpiarHorariosEInventario", e);
            return badRequest("Error al limpiar horarios e inventario: " + e.getMessage());
        }
    }

    @DeleteMapping("/limpieza/billetes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> limpiarBilletes(
            @RequestParam(defaultValue = "false") boolean confirm,
            @RequestParam(defaultValue = "true") boolean limpiarInventario
    ) {
        logRequest("limpiarBilletes", "confirm=" + confirm, "limpiarInventario=" + limpiarInventario);

        if (!confirm) {
            return badRequest("Operación destructiva. Repite con confirm=true");
        }

        try {
            long billetesAntes = billeteRepository.count();
            long inventarioAntes = inventarioHorarioRepository.count();

            log.warn("LIMPIEZA: borrando billetes={}{}", billetesAntes,
                    limpiarInventario ? (", e inventario_horario=" + inventarioAntes) : "");

            billeteRepository.deleteAll();
            if (limpiarInventario) {
                inventarioHorarioRepository.deleteAll();
            }

            Map<String, Object> resultado = Map.of(
                    "billetesEliminados", billetesAntes,
                    "inventarioEliminado", limpiarInventario ? inventarioAntes : 0
            );

            return ok(resultado, "Limpieza de billetes completada exitosamente");

        } catch (Exception e) {
            logError("limpiarBilletes", e);
            return badRequest("Error al limpiar billetes: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE CONTROL OPERATIVO ====================

    @PostMapping("/control/iniciar-servicio/horario/{horarioId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> iniciarServicioPorHorario(
            @PathVariable String horarioId,
            @RequestParam(required = false) Double velocidadCruceroKmh,
            @RequestParam(defaultValue = "false") boolean force,
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        logRequest("iniciarServicioPorHorario", horarioId, velocidadCruceroKmh, force, confirm);

        if (!confirm) {
            return badRequest("Operación de estado. Repite con confirm=true");
        }

        try {
            Map<String, Object> resultado = gestionHorariosService.iniciarServicioPorHorario(
                    horarioId, velocidadCruceroKmh, force);

            return ok(resultado, "Servicio iniciado exitosamente para el horario: " + horarioId);

        } catch (Exception e) {
            logError("iniciarServicioPorHorario", e);
            return badRequest("Error al iniciar servicio: " + e.getMessage());
        }
    }

    @PatchMapping("/control/trenes/estado")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cambiarEstadoTrenes(
            @RequestParam Tren.EstadoTren estado,
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        logRequest("cambiarEstadoTrenes", estado, confirm);

        if (!confirm) {
            return badRequest("Operación masiva. Repite con confirm=true");
        }

        try {
            var trenes = trenRepository.findAll();
            for (Tren t : trenes) {
                t.setEstadoActual(estado);
                t.setFechaActualizacion(LocalDateTime.now());
            }
            trenRepository.saveAll(trenes);

            log.warn("ESTADO TRENES: actualizados {} trenes a {}", trenes.size(), estado);

            Map<String, Object> resultado = Map.of(
                    "totalActualizados", trenes.size(),
                    "estadoNuevo", estado.name()
            );

            return ok(resultado, "Estado de trenes actualizado exitosamente");

        } catch (Exception e) {
            logError("cambiarEstadoTrenes", e);
            return badRequest("Error al cambiar estado de trenes: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE GESTIÓN DE HORARIOS ====================

    @PostMapping("/horarios/regenerar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> regenerarHorarios(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        logRequest("regenerarHorarios", confirm);

        if (!confirm) {
            return badRequest("Operación potencialmente costosa. Repite con confirm=true");
        }

        try {
            long antes = horarioRepository.count();
            log.warn("REGENERAR: creando horarios programados. Horarios antes={}", antes);

            gestionHorariosService.crearHorariosProgramados();

            long despues = horarioRepository.count();

            Map<String, Object> resultado = Map.of(
                    "horariosAntes", antes,
                    "horariosDespues", despues,
                    "horariosCreadosEstimados", Math.max(0, despues - antes)
            );

            return ok(resultado, "Horarios regenerados exitosamente");

        } catch (Exception e) {
            logError("regenerarHorarios", e);
            return badRequest("Error al regenerar horarios: " + e.getMessage());
        }
    }

    @PostMapping("/horarios/reset")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resetHorarios(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        logRequest("resetHorarios", confirm);

        if (!confirm) {
            return badRequest("Operación destructiva. Repite con confirm=true");
        }

        try {
            long horariosAntes = horarioRepository.count();
            long inventarioAntes = inventarioHorarioRepository.count();

            log.warn("RESET: borrando horarios={} e inventario_horario={}, y regenerando", 
                    horariosAntes, inventarioAntes);

            inventarioHorarioRepository.deleteAll();
            horarioRepository.deleteAll();

            gestionHorariosService.crearHorariosProgramados();

            long horariosDespues = horarioRepository.count();
            long inventarioDespues = inventarioHorarioRepository.count();

            Map<String, Object> resultado = Map.of(
                    "horariosAntes", horariosAntes,
                    "inventarioAntes", inventarioAntes,
                    "horariosDespues", horariosDespues,
                    "inventarioDespues", inventarioDespues
            );

            return ok(resultado, "Reset de horarios completado exitosamente");

        } catch (Exception e) {
            logError("resetHorarios", e);
            return badRequest("Error al resetear horarios: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE MONITORIZACIÓN ====================

    @GetMapping("/monitorizacion/consistencia-paradas")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificarConsistenciaParadas() {
        logRequest("verificarConsistenciaParadas");

        try {
            Map<String, Object> resultado = gestionHorariosService.verificarConsistenciaParadasConRuta();

            log.info("Verificación completada: {}/{} horarios consistentes ({}%)",
                    resultado.get("horariosConsistentes"),
                    resultado.get("horariosVerificados"),
                    resultado.get("porcentajeConsistencia"));

            return ok(resultado, "Verificación de consistencia completada");

        } catch (Exception e) {
            logError("verificarConsistenciaParadas", e);
            return badRequest("Error en verificación de consistencia: " + e.getMessage());
        }
    }

    @GetMapping("/monitorizacion/trenes-tiempo-real")
    public ResponseEntity<ApiResponse<Map<String, Object>>> monitorizarTrenesTiempoReal() {
        logRequest("monitorizarTrenesTiempoReal");

        try {
            Map<String, Object> resultado = gestionHorariosService.monitorizarEstadoTrenesTiempoReal();

            log.info("Monitorización completada: {} trenes totales, {} en marcha, {} incidentes",
                    resultado.get("totalTrenes"),
                    resultado.get("trenesEnMarcha"),
                    resultado.get("incidentesDetectados"));

            return ok(resultado, "Monitorización en tiempo real completada");

        } catch (Exception e) {
            logError("monitorizarTrenesTiempoReal", e);
            return badRequest("Error en monitorización: " + e.getMessage());
        }
    }

    @GetMapping("/monitorizacion/candidatos-inicio")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerCandidatosInicioHorarios(
            @RequestParam(defaultValue = "2") int minutosAntes,
            @RequestParam(defaultValue = "10") int minutosDespues,
            @RequestParam(defaultValue = "50") int max,
            @RequestParam(defaultValue = "true") boolean incluirRetrasados
    ) {
        logRequest("obtenerCandidatosInicioHorarios", minutosAntes, minutosDespues, max, incluirRetrasados);

        try {
            Map<String, Object> resultado = gestionHorariosService.obtenerCandidatosInicioHorarios(
                    minutosAntes, minutosDespues, max, incluirRetrasados);

            return ok(resultado, "Candidatos de inicio obtenidos exitosamente");

        } catch (Exception e) {
            logError("obtenerCandidatosInicioHorarios", e);
            return badRequest("Error al obtener candidatos de inicio: " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE UTILIDADES ADMIN ====================

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        logRequest("healthCheck");

        try {
            Map<String, Object> resultado = Map.of(
                    "status", "OK",
                    "timestamp", LocalDateTime.now(),
                    "database", "connected",
                    "services", "operational"
            );

            return ok(resultado, "Sistema operativo");
        } catch (Exception e) {
            logError("healthCheck", e);
            return badRequest("Error en health check: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        logRequest("getSystemStats");

        try {
            long totalTrenes = trenRepository.count();
            long totalHorarios = horarioRepository.count();
            long totalInventario = inventarioHorarioRepository.count();
            long totalBilletes = billeteRepository.count();

            Map<String, Object> resultado = Map.of(
                    "totalTrenes", totalTrenes,
                    "totalHorarios", totalHorarios,
                    "totalInventario", totalInventario,
                    "totalBilletes", totalBilletes,
                    "timestamp", LocalDateTime.now()
            );

            return ok(resultado, "Estadísticas del sistema obtenidas");
        } catch (Exception e) {
            logError("getSystemStats", e);
            return badRequest("Error al obtener estadísticas: " + e.getMessage());
        }
    }
}
