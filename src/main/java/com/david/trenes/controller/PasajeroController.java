package com.david.trenes.controller;

import com.david.trenes.model.Pasajero;
import com.david.trenes.security.CurrentUserService;
import com.david.trenes.service.PasajeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pasajeros")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class PasajeroController {

    private final PasajeroService pasajeroService;
    private final CurrentUserService currentUserService;

    @GetMapping("/mis")
    public ResponseEntity<List<Pasajero>> misPasajeros() {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Listando pasajeros del usuario autenticado: usuarioId={}", usuarioId);
        return ResponseEntity.ok(pasajeroService.findByUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Pasajero> crear(@Valid @RequestBody Pasajero pasajero) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Creando pasajero para usuario autenticado: usuarioId={}", usuarioId);
        Pasajero creado = pasajeroService.create(usuarioId, pasajero);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{pasajeroId}")
    public ResponseEntity<Pasajero> actualizar(@PathVariable String pasajeroId, @Valid @RequestBody Pasajero pasajero) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Actualizando pasajeroId={} para usuario autenticado: usuarioId={}", pasajeroId, usuarioId);
        return ResponseEntity.ok(pasajeroService.update(usuarioId, pasajeroId, pasajero));
    }

    @DeleteMapping("/{pasajeroId}")
    public ResponseEntity<Void> borrar(@PathVariable String pasajeroId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();
        log.info("Borrando pasajeroId={} para usuario autenticado: usuarioId={}", pasajeroId, usuarioId);
        pasajeroService.delete(usuarioId, pasajeroId);
        return ResponseEntity.noContent().build();
    }
}
