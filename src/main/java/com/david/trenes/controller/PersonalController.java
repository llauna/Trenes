package com.david.trenes.controller;

import com.david.trenes.model.Personal;
import com.david.trenes.security.CurrentUserService;
import com.david.trenes.service.PersonalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personal")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class PersonalController {

    private final PersonalService personalService;
    private final CurrentUserService currentUserService;

    @GetMapping("/mis")
    public ResponseEntity<List<Personal>> miPersonal() {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando personal del usuario autenticado: usuarioId={}", usuarioId);
        return ResponseEntity.ok(personalService.findByUsuario(usuarioId));
    }

    @GetMapping("/tipo/{tipoPersonal}")
    public ResponseEntity<List<Personal>> personalPorTipo(@PathVariable Personal.TipoPersonal tipoPersonal) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando personal por tipo {} para usuario: usuarioId={}", tipoPersonal, usuarioId);
        return ResponseEntity.ok(personalService.findByUsuarioAndTipoPersonal(usuarioId, tipoPersonal));
    }

    @GetMapping("/activos/{activo}")
    public ResponseEntity<List<Personal>> personalPorEstado(@PathVariable Boolean activo) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando personal por estado {} para usuario: usuarioId={}", activo, usuarioId);
        return ResponseEntity.ok(personalService.findByUsuarioAndTipoPersonal(usuarioId, null)
                .stream()
                .filter(p -> activo.equals(p.getActivo()))
                .toList());
    }

    @PostMapping
    public ResponseEntity<Personal> crear(@Valid @RequestBody Personal personal) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Creando personal para usuario autenticado: usuarioId={}, tipo={}", 
                usuarioId, personal.getTipoPersonal());
        Personal creado = personalService.create(usuarioId, personal);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{personalId}")
    public ResponseEntity<Personal> actualizar(@PathVariable String personalId, 
                                             @Valid @RequestBody Personal personal) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Actualizando personalId={} para usuario autenticado: usuarioId={}", 
                personalId, usuarioId);
        return ResponseEntity.ok(personalService.update(usuarioId, personalId, personal));
    }

    @DeleteMapping("/{personalId}")
    public ResponseEntity<Void> borrar(@PathVariable String personalId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Borrando personalId={} para usuario autenticado: usuarioId={}", 
                personalId, usuarioId);
        personalService.delete(usuarioId, personalId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/seed")
    public ResponseEntity<List<Personal>> seed(@RequestParam(defaultValue = "20") int count) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Seed personal aleatorio: usuarioId={}, count={}", usuarioId, count);

        List<Personal> creados = personalService.seedAleatorios(usuarioId, count);
        return ResponseEntity.status(HttpStatus.CREATED).body(creados);
    }

    @GetMapping("/conductores")
    public ResponseEntity<List<Personal>> conductores() {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando conductores para usuario: usuarioId={}", usuarioId);
        return ResponseEntity.ok(personalService.findByUsuarioAndTipoPersonal(usuarioId, Personal.TipoPersonal.CONDUCTOR));
    }

    @GetMapping("/cabina")
    public ResponseEntity<List<Personal>> personalCabina() {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando personal de cabina para usuario: usuarioId={}", usuarioId);
        return ResponseEntity.ok(personalService.findByUsuarioAndTipoPersonal(usuarioId, Personal.TipoPersonal.CABINA));
    }

    @GetMapping("/restaurante")
    public ResponseEntity<List<Personal>> personalRestaurante() {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando personal de restaurante para usuario: usuarioId={}", usuarioId);
        return ResponseEntity.ok(personalService.findByUsuarioAndTipoPersonal(usuarioId, Personal.TipoPersonal.RESTAURANTE));
    }
}
