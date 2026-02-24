package com.david.trenes.controller;

import com.david.trenes.dto.BilleteResumenResponse;
import com.david.trenes.model.Billete;
import com.david.trenes.service.BilleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/billetes-count")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class BilleteCountController {

    private final BilleteService billeteService;

    @GetMapping("/mis-count")
    public ResponseEntity<Map<String, Object>> contarMisBilletes() {
        try {
            log.info("Contando billetes del usuario actual");
            
            java.util.List<Billete> billetes = billeteService.findBilletesDelUsuarioActual();
            int total = billetes.size();
            
            return ResponseEntity.ok(Map.of(
                    "total", total,
                    "mensaje", "Total de billetes: " + total
            ));
            
        } catch (Exception e) {
            log.error("Error contando billetes", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/mis")
    public ResponseEntity<List<BilleteResumenResponse>> misBilletes() {
        List<Billete> billetes = billeteService.findBilletesDelUsuarioActual();

        List<BilleteResumenResponse> response = billetes.stream()
                .map(b -> BilleteResumenResponse.builder()
                        .billeteId(b.getId())
                        .codigoBillete(b.getCodigoBillete())
                        .horarioId(b.getHorarioId())
                        .pasajeroId(b.getPasajeroId())
                        .estacionOrigenId(b.getEstacionOrigenId())
                        .estacionDestinoId(b.getEstacionDestinoId())
                        .clase(b.getClase())
                        .estado(b.getEstado() != null ? b.getEstado().name() : null)
                        .precioTotal(b.getPrecioTotal())
                        .fechaCompra(b.getFechaCompra())
                        .vagonNumero(b.getVagonNumero())
                        .asientoNumero(b.getAsientoNumero())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis/detalle")
    public ResponseEntity<List<Billete>> misBilletesDetalle() {

        return ResponseEntity.ok(billeteService.findBilletesDelUsuarioActual());
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("""
                Endpoint para contar billetes:
                
                GET /api/v1/billetes-count/mis-count
                Retorna el número total de billetes del usuario actual
                """);
    }
}
