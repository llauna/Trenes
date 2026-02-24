package com.david.trenes.controller;

import com.david.trenes.model.Via;
import com.david.trenes.service.InfraestructuraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/infraestructura")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class InfraestructuraController {

    private final InfraestructuraService infraestructuraService;

    @PostMapping("/crear-infraestructura-ejemplo")
    public ResponseEntity<String> crearInfraestructuraDeEjemplo() {
        log.info("Creando infraestructura de ejemplo (desvíos y vías muertas)");
        
        try {
            infraestructuraService.crearInfraestructuraDeEjemplo();
            return ResponseEntity.ok("Infraestructura de ejemplo creada exitosamente");
        } catch (Exception e) {
            log.error("Error al crear infraestructura de ejemplo", e);
            return ResponseEntity.internalServerError()
                    .body("Error al crear infraestructura de ejemplo: " + e.getMessage());
        }
    }

    @GetMapping("/desvios")
    public ResponseEntity<List<Via>> obtenerDesvios() {
        log.info("Obteniendo lista de desvíos");
        List<Via> desvios = infraestructuraService.obtenerDesvios();
        return ResponseEntity.ok(desvios);
    }

    @GetMapping("/vias-muertas")
    public ResponseEntity<List<Via>> obtenerViasMuertas() {
        log.info("Obteniendo lista de vías muertas");
        List<Via> viasMuertas = infraestructuraService.obtenerViasMuertas();
        return ResponseEntity.ok(viasMuertas);
    }

    @PatchMapping("/desvios/{viaId}/estado")
    public ResponseEntity<String> cambiarEstadoDesvio(
            @PathVariable String viaId,
            @RequestParam Via.EstadoVia nuevoEstado) {
        log.info("Cambiando estado del desvío {} a: {}", viaId, nuevoEstado);
        
        boolean resultado = infraestructuraService.cambiarEstadoDesvio(viaId, nuevoEstado);
        
        if (resultado) {
            return ResponseEntity.ok("Estado del desvío actualizado exitosamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo actualizar el estado del desvío. Verifique el ID.");
        }
    }

    @PostMapping("/vias-muertas/{viaMuertaId}/asignar-tren")
    public ResponseEntity<String> asignarTrenAViaMuerta(
            @PathVariable String viaMuertaId,
            @RequestParam String trenId) {
        log.info("Asignando tren {} a vía muerta: {}", trenId, viaMuertaId);
        
        boolean resultado = infraestructuraService.asignarTrenAViaMuerta(trenId, viaMuertaId);
        
        if (resultado) {
            return ResponseEntity.ok("Tren asignado a vía muerta exitosamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo asignar el tren a la vía muerta. Verifique los IDs.");
        }
    }
}
