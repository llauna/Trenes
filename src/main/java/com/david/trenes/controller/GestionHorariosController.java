package com.david.trenes.controller;

import com.david.trenes.model.Horario;
import com.david.trenes.service.GestionHorariosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/gestion-horarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class GestionHorariosController {

    private final GestionHorariosService gestionHorariosService;

    @PostMapping("/crear-horarios-programados")
    public ResponseEntity<String> crearHorariosProgramados() {
        log.info("Creando horarios programados para el sistema");
        
        try {
            gestionHorariosService.crearHorariosProgramados();
            return ResponseEntity.ok("Horarios programados creados exitosamente");
        } catch (Exception e) {
            log.error("Error al crear horarios programados", e);
            return ResponseEntity.internalServerError()
                    .body("Error al crear horarios programados: " + e.getMessage());
        }
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Horario>> obtenerHorariosPorRuta(@PathVariable String rutaId) {
        log.info("Obteniendo horarios para la ruta: {}", rutaId);
        List<Horario> horarios = gestionHorariosService.obtenerHorariosPorRuta(rutaId);
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/tren/{trenId}")
    public ResponseEntity<List<Horario>> obtenerHorariosPorTren(@PathVariable String trenId) {
        log.info("Obteniendo horarios para el tren: {}", trenId);
        List<Horario> horarios = gestionHorariosService.obtenerHorariosPorTren(trenId);
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Horario>> obtenerHorariosPorEstado(@PathVariable Horario.EstadoHorario estado) {
        log.info("Obteniendo horarios con estado: {}", estado);
        List<Horario> horarios = gestionHorariosService.obtenerHorariosPorEstado(estado);
        return ResponseEntity.ok(horarios);
    }

    @PatchMapping("/{horarioId}/estado")
    public ResponseEntity<String> actualizarEstadoHorario(
            @PathVariable String horarioId,
            @RequestParam Horario.EstadoHorario nuevoEstado) {
        log.info("Actualizando estado del horario {} a: {}", horarioId, nuevoEstado);
        
        boolean resultado = gestionHorariosService.actualizarEstadoHorario(horarioId, nuevoEstado);
        
        if (resultado) {
            return ResponseEntity.ok("Estado del horario actualizado exitosamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo actualizar el estado del horario. Verifique el ID.");
        }
    }
}
