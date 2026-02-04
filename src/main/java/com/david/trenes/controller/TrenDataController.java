package com.david.trenes.controller;

import com.david.trenes.service.TrenDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trenes-data")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class TrenDataController {

    private final TrenDataService trenDataService;

    @PostMapping("/crear-trenes-ejemplo")
    public ResponseEntity<String> crearTrenesDeEjemplo() {
        log.info("Creando trenes de ejemplo para enriquecer el sistema");
        
        try {
            trenDataService.crearTrenesDeEjemplo();
            return ResponseEntity.ok("Trenes de ejemplo creados exitosamente");
        } catch (Exception e) {
            log.error("Error al crear trenes de ejemplo", e);
            return ResponseEntity.internalServerError()
                    .body("Error al crear trenes de ejemplo: " + e.getMessage());
        }
    }
}
