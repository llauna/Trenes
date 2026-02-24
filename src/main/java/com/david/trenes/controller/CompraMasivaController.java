package com.david.trenes.controller;

import com.david.trenes.service.CompraMasivaBilletesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/compra-masiva")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class CompraMasivaController {

    private final CompraMasivaBilletesService compraMasivaBilletesService;

    @PostMapping("/comprar")
    public ResponseEntity<CompraMasivaBilletesService.CompraMasivaResponse> comprarBilletesMasivos(
            @RequestParam(defaultValue = "50") int cantidadPasajeros) {
        log.info("Iniciando compra masiva de billetes para {} pasajeros", cantidadPasajeros);
        
        try {
            CompraMasivaBilletesService.CompraMasivaResponse response = 
                    compraMasivaBilletesService.comprarBilletesMasivos(cantidadPasajeros);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalStateException e) {
            log.warn("Error en compra masiva: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CompraMasivaBilletesService.CompraMasivaResponse.builder()
                            .totalPasajeros(0)
                            .comprasExitosas(0)
                            .comprasFallidas(0)
                            .compras(List.of())
                            .build());
        } catch (Exception e) {
            log.error("Error interno en compra masiva", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompraMasivaBilletesService.CompraMasivaResponse.builder()
                            .totalPasajeros(0)
                            .comprasExitosas(0)
                            .comprasFallidas(0)
                            .compras(List.of())
                            .build());
        }
    }

    @PostMapping("/comprar-todos")
    public ResponseEntity<CompraMasivaBilletesService.CompraMasivaResponse> comprarBilletesParaTodosLosPasajeros() {
        log.info("Iniciando compra masiva de billetes para todos los pasajeros disponibles");
        
        try {
            // Comprar para 300 pasajeros como solicitaste
            CompraMasivaBilletesService.CompraMasivaResponse response = 
                    compraMasivaBilletesService.comprarBilletesMasivos(300);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalStateException e) {
            log.warn("Error en compra masiva para todos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(CompraMasivaBilletesService.CompraMasivaResponse.builder()
                            .totalPasajeros(0)
                            .comprasExitosas(0)
                            .comprasFallidas(0)
                            .compras(List.of())
                            .build());
        } catch (Exception e) {
            log.error("Error interno en compra masiva para todos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CompraMasivaBilletesService.CompraMasivaResponse.builder()
                            .totalPasajeros(0)
                            .comprasExitosas(0)
                            .comprasFallidas(0)
                            .compras(List.of())
                            .build());
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<String> obtenerEstadisticas() {
        log.info("Generando estadísticas de compras masivas");
        
        try {
            // Esta es una implementación básica, puedes expandirla según necesites
            String estadisticas = """
                    Estadísticas de Compras Masivas:
                    - Endpoint disponible: /api/v1/compra-masiva/comprar?cantidadPasajeros=X
                    - Endpoint para todos: /api/v1/compra-masiva/comprar-todos
                    - Clases disponibles: TURISTA, PRIMERA, BUSINESS
                    - Distribución automática en horarios y rutas disponibles
                    """;
            
            return ResponseEntity.ok(estadisticas);
            
        } catch (Exception e) {
            log.error("Error al generar estadísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar estadísticas");
        }
    }
}
