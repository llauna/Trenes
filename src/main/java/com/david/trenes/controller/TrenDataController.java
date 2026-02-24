package com.david.trenes.controller;

import com.david.trenes.model.Tren;
import com.david.trenes.service.TrenDataService;
import com.david.trenes.service.TrenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/trenes-data")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class TrenDataController {

    private final TrenDataService trenDataService;
    private final TrenService trenService;

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

    @PostMapping("/limpiar-trenes")
    public ResponseEntity<String> limpiarTrenes() {
        log.info("Limpiando todos los trenes existentes");

        try {
            List<Tren> trenes = trenService.findAll();
            for (Tren tren : trenes) {
                trenService.deleteById(tren.getId());
            }
            return ResponseEntity.ok("Trenes limpiados exitosamente. Se eliminaron " + trenes.size() + " trenes.");
        } catch (Exception e) {
            log.error("Error al limpiar trenes", e);
            return ResponseEntity.internalServerError()
                    .body("Error al limpiar trenes: " + e.getMessage());
        }
    }
}