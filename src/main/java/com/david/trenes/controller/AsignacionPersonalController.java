package com.david.trenes.controller;

import com.david.trenes.model.Tren;
import com.david.trenes.service.AsignacionPersonalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/asignacion-personal")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class AsignacionPersonalController {

    private final AsignacionPersonalService asignacionPersonalService;

    @PostMapping("/asignar-todos")
    public ResponseEntity<String> asignarPersonalATodosLosTrenes() {
        log.info("Iniciando asignación automática de personal a todos los trenes");
        try {
            asignacionPersonalService.asignarPersonalATodosLosTrenes();
            return ResponseEntity.ok("Personal asignado exitosamente a todos los trenes activos");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error al asignar personal a todos los trenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @PostMapping("/asignar-tren/{trenId}")
    public ResponseEntity<Tren> asignarPersonalATren(@PathVariable String trenId) {
        log.info("Asignando personal al tren: {}", trenId);
        try {
            Tren trenActualizado = asignacionPersonalService.asignarPersonalATren(trenId);
            return ResponseEntity.ok(trenActualizado);
        } catch (RuntimeException e) {
            log.error("Error al asignar personal al tren {}", trenId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno al asignar personal al tren {}", trenId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reasignar")
    public ResponseEntity<String> reasignarPersonal() {
        log.info("Iniciando reasignación de personal para rotación de turnos");
        try {
            asignacionPersonalService.reasignarPersonal();
            return ResponseEntity.ok("Personal reasignado exitosamente para rotación de turnos");
        } catch (Exception e) {
            log.error("Error al reasignar personal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @GetMapping("/tren/{trenId}")
    public ResponseEntity<AsignacionPersonalService.PersonalAsignadoResponse> 
            obtenerPersonalAsignado(@PathVariable String trenId) {
        log.info("Consultando personal asignado al tren: {}", trenId);
        try {
            AsignacionPersonalService.PersonalAsignadoResponse response = 
                    asignacionPersonalService.obtenerPersonalAsignado(trenId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error al obtener personal asignado al tren {}", trenId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno al obtener personal asignado al tren {}", trenId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
