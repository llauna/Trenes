package com.david.trenes.controller;


import com.david.trenes.repository.BilleteRepository;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.InventarioHorarioRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.service.GestionHorariosService;
import com.david.trenes.util.DateUtils;

import com.david.trenes.model.Tren;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/mantenimiento-datos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class MantenimientoDatosController {

    private final HorarioRepository horarioRepository;
    private final InventarioHorarioRepository inventarioHorarioRepository;
    private final GestionHorariosService gestionHorariosService;

    private final BilleteRepository billeteRepository;
    private final TrenRepository trenRepository;

    @DeleteMapping("/limpiar-horarios-inventario")
    public ResponseEntity<Map<String, Object>> limpiarHorariosEInventario(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación destructiva. Repite con confirm=true"
            ));
        }

        long horariosAntes = horarioRepository.count();
        long inventarioAntes = inventarioHorarioRepository.count();

        log.warn("LIMPIEZA: borrando horarios={} e inventario_horario={}", horariosAntes, inventarioAntes);

        inventarioHorarioRepository.deleteAll();
        horarioRepository.deleteAll();

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "horariosEliminados", horariosAntes,
                "inventarioEliminado", inventarioAntes
        ));
    }

    @DeleteMapping("/limpiar-billetes")
    public ResponseEntity<Map<String, Object>> limpiarBilletes(
            @RequestParam(defaultValue = "false") boolean confirm,
            @RequestParam(defaultValue = "true") boolean limpiarInventario
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación destructiva. Repite con confirm=true"
            ));
        }

        long billetesAntes = billeteRepository.count();
        long inventarioAntes = inventarioHorarioRepository.count();

        log.warn("LIMPIEZA: borrando billetes={}{}", billetesAntes,
                limpiarInventario ? (", e inventario_horario=" + inventarioAntes) : "");

        billeteRepository.deleteAll();
        if (limpiarInventario) {
            inventarioHorarioRepository.deleteAll();
        }

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "billetesEliminados", billetesAntes,
                "inventarioEliminado", limpiarInventario ? inventarioAntes : 0
        ));
    }

    @PostMapping("/horarios/{horarioId}/iniciar-servicio")
    public ResponseEntity<Map<String, Object>> iniciarServicioPorHorario(
            @PathVariable String horarioId,
            @RequestParam(required = false) Double velocidadCruceroKmh,
            @RequestParam(defaultValue = "false") boolean force,
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación de estado. Repite con confirm=true"
            ));
        }

        Map<String, Object> resultado = gestionHorariosService.iniciarServicioPorHorario(horarioId, velocidadCruceroKmh, force);

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "resultado", resultado
        ));
    }


    @PatchMapping("/trenes/estado")
    public ResponseEntity<Map<String, Object>> cambiarEstadoTrenes(
            @RequestParam Tren.EstadoTren estado,
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación masiva. Repite con confirm=true"
            ));
        }

        var trenes = trenRepository.findAll();
        for (Tren t : trenes) {
            t.setEstadoActual(estado);
            t.setFechaActualizacion(LocalDateTime.now());
        }
        trenRepository.saveAll(trenes);

        log.warn("ESTADO TRENES: actualizados {} trenes a {}", trenes.size(), estado);

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "totalActualizados", trenes.size(),
                "estadoNuevo", estado.name()
        ));
    }

    @PostMapping("/regenerar-horarios")
    public ResponseEntity<Map<String, Object>> regenerarHorarios(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación potencialmente costosa. Repite con confirm=true"
            ));
        }

        long antes = horarioRepository.count();
        log.warn("REGENERAR: creando horarios programados. Horarios antes={}", antes);

        gestionHorariosService.crearHorariosProgramados();

        long despues = horarioRepository.count();

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "horariosAntes", antes,
                "horariosDespues", despues,
                "horariosCreadosEstimados", Math.max(0, despues - antes)
        ));
    }

    @PostMapping("/reset-horarios")
    public ResponseEntity<Map<String, Object>> resetHorarios(
            @RequestParam(defaultValue = "false") boolean confirm
    ) {
        if (!confirm) {
            return ResponseEntity.badRequest().body(Map.of(
                    "ok", false,
                    "message", "Operación destructiva. Repite con confirm=true"
            ));
        }

        long horariosAntes = horarioRepository.count();
        long inventarioAntes = inventarioHorarioRepository.count();

        log.warn("RESET: borrando horarios={} e inventario_horario={}, y regenerando", horariosAntes, inventarioAntes);

        inventarioHorarioRepository.deleteAll();
        horarioRepository.deleteAll();

        gestionHorariosService.crearHorariosProgramados();

        long horariosDespues = horarioRepository.count();
        long inventarioDespues = inventarioHorarioRepository.count(); // debería ser 0 hasta que se vendan billetes

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "horariosAntes", horariosAntes,
                "inventarioAntes", inventarioAntes,
                "horariosDespues", horariosDespues,
                "inventarioDespues", inventarioDespues
        ));
    }
    @GetMapping("/verificar-consistencia-paradas")
    public ResponseEntity<Map<String, Object>> verificarConsistenciaParadas() {
        log.info("Iniciando verificación de consistencia de paradas con rutas");

        Map<String, Object> resultado = gestionHorariosService.verificarConsistenciaParadasConRuta();

        log.info("Verificación completada: {}/{} horarios consistentes ({}%)",
                resultado.get("horariosConsistentes"),
                resultado.get("horariosVerificados"),
                resultado.get("porcentajeConsistencia"));

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "resultado", resultado
        ));
    }

    @GetMapping("/monitorizar-trenes-tiempo-real")
    @CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"}, methods = {RequestMethod.GET})
    public ResponseEntity<Map<String, Object>> monitorizarTrenesTiempoReal() {
        log.info("Iniciando monitorización en tiempo real de trenes");
        
        Map<String, Object> resultado = gestionHorariosService.monitorizarEstadoTrenesTiempoReal();
        
        log.info("Monitorización completada: {} trenes totales, {} en marcha, {} incidentes", 
            resultado.get("totalTrenes"), 
            resultado.get("trenesEnMarcha"), 
            resultado.get("incidentesDetectados"));
        
        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .body(Map.of(
                "ok", true,
                "resultado", resultado
        ));
    }

    @GetMapping("/horarios/candidatos-inicio")
    public ResponseEntity<Map<String, Object>> obtenerCandidatosInicioHorarios(
            @RequestParam(defaultValue = "2") int minutosAntes,
            @RequestParam(defaultValue = "10") int minutosDespues,
            @RequestParam(defaultValue = "50") int max,
            @RequestParam(defaultValue = "true") boolean incluirRetrasados
    ) {
        Map<String, Object> resultado = gestionHorariosService.obtenerCandidatosInicioHorarios(
                minutosAntes, minutosDespues, max, incluirRetrasados
        );

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "resultado", resultado
        ));
    }

    @RequestMapping(value = "/monitorizar-trenes-tiempo-real", method = RequestMethod.OPTIONS)
    @CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok()
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
            .build();
    }

    @GetMapping("/test-simple")
    public ResponseEntity<Map<String, Object>> testSimple() {
        return ResponseEntity.ok(Map.of(
            "ok", true,
            "message", "Test endpoint working",
            "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/test-formato-fecha")
    public ResponseEntity<Map<String, Object>> testFormatoFecha(
            @RequestParam(required = false) LocalDateTime fecha) {
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("ok", true);
        response.put("message", "Test de formato de fecha");
        
        if (fecha != null) {
            response.put("fechaRecibida", fecha);
            response.put("fechaFormateadaSpanish", DateUtils.formatSpanishDate(fecha));
            response.put("fechaFormateadaSpanishDateTime", DateUtils.formatSpanishDateTime(fecha));
            response.put("fechaFormateadaISO", DateUtils.formatISODateTime(fecha));
        } else {
            response.put("fechaActual", LocalDateTime.now());
            response.put("fechaActualSpanish", DateUtils.formatSpanishDate(LocalDateTime.now()));
            response.put("ejemploFormato", "Usa: ?fecha=27/02/2026 o ?fecha=27/02/2026 14:30:00 o ?fecha=2026-02-27T14:30:00");
        }
        
        return ResponseEntity.ok(response);
    }


}
