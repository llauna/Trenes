package com.david.trenes.controller;

import com.david.trenes.model.Tren;
import com.david.trenes.service.UbicacionTrenesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/ubicacion-trenes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class UbicacionTrenesController {

    private final UbicacionTrenesService ubicacionTrenesService;

    @PostMapping("/ubicar-detenidos")
    public ResponseEntity<String> ubicarTrenesDetenidosEnEstaciones() {
        log.info("Iniciando ubicación de trenes detenidos en estaciones");
        try {
            ubicacionTrenesService.ubicarTrenesDetenidosEnEstaciones();
            return ResponseEntity.ok("Trenes detenidos ubicados exitosamente en estaciones");
        } catch (Exception e) {
            log.error("Error al ubicar trenes detenidos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @PostMapping("/ubicar-tren/{trenId}")
    public ResponseEntity<Tren> ubicarTrenEnEstacion(@PathVariable String trenId) {
        log.info("Ubicando tren {} en estación", trenId);
        try {
            Tren trenActualizado = ubicacionTrenesService.ubicarTrenEnEstacion(trenId);
            return ResponseEntity.ok(trenActualizado);
        } catch (RuntimeException e) {
            log.error("Error al ubicar tren {} en estación", trenId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error interno al ubicar tren {} en estación", trenId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/liberar-vias")
    public ResponseEntity<String> liberarViasDeEstacion() {
        log.info("Liberando vías de estación (devolviendo trenes a estado detenido)");
        try {
            ubicacionTrenesService.liberarViasDeEstacion();
            return ResponseEntity.ok("Vías de estación liberadas exitosamente");
        } catch (Exception e) {
            log.error("Error al liberar vías de estación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/reporte")
    public ResponseEntity<UbicacionTrenesService.UbicacionReporte> obtenerReporteUbicacion() {
        log.info("Generando reporte de ubicación de trenes");
        try {
            UbicacionTrenesService.UbicacionReporte reporte = ubicacionTrenesService.obtenerReporteUbicacion();
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            log.error("Error al generar reporte de ubicación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
