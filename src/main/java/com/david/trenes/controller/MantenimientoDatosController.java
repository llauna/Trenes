package com.david.trenes.controller;


import com.david.trenes.repository.BilleteRepository;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.InventarioHorarioRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.service.GestionHorariosService;

import com.david.trenes.model.Tren;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/mantenimiento-datos")
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
        
        log.info("Verificación completada: {}/{} horarios consistentes ({:.2f}%)", 
            resultado.get("horariosConsistentes"), 
            resultado.get("horariosVerificados"),
            (Double) resultado.get("porcentajeConsistencia"));
        
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "resultado", resultado
        ));
    }
}
