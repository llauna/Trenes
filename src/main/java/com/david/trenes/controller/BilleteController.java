package com.david.trenes.controller;

import com.david.trenes.dto.BilleteResumenResponse;
import com.david.trenes.dto.CompraBilleteRequest;
import com.david.trenes.dto.CompraBilleteResponse;
import com.david.trenes.dto.CompraBilletesResponse;
import com.david.trenes.dto.DisponibilidadBilleteResponse;
import com.david.trenes.model.Billete;
import com.david.trenes.service.BilleteService;
import com.david.trenes.util.ValidationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/billetes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class BilleteController {

    private final BilleteService billeteService;

    @PostMapping("/compra")
    public ResponseEntity<CompraBilletesResponse> comprar(@Valid @RequestBody CompraBilleteRequest request) {
        log.info("Compra billetes: horarioId={}, pasajeros={}, origen={}, destino={}",
                request.getHorarioId(), request.getPasajeroIds().size(), request.getEstacionOrigenId(), request.getEstacionDestinoId());

        if (request.getHorarioId() == null || request.getHorarioId().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "horarioId es obligatorio"
            );
        }
        if (request.getEstacionOrigenId() == null || request.getEstacionOrigenId().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "estacionOrigenId es obligatorio"
            );
        }
        if (request.getEstacionDestinoId() == null || request.getEstacionDestinoId().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "estacionDestinoId es obligatorio"
            );
        }
        if (request.getPasajeroIds() == null || request.getPasajeroIds().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "pasajeroIds debe contener al menos un pasajero"
            );
        }
        if (request.getPasajeroIds().stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "pasajeroIds no puede contener valores nulos o vacíos"
            );
        }

        List<Billete> billetes = billeteService.comprarVarios(
                request.getHorarioId(),
                request.getPasajeroIds(),
                request.getEstacionOrigenId(),
                request.getEstacionDestinoId(),
                request.getClase()
        );

        List<CompraBilleteResponse> items = billetes.stream()
                .map(b -> CompraBilleteResponse.builder()
                        .billeteId(b.getId())
                        .codigoBillete(b.getCodigoBillete())
                        .estado(b.getEstado().name())
                        .build())
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CompraBilletesResponse.builder()
                        .totalBilletes(items.size())
                        .billetes(items)
                        .build()
        );
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
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pasajero/{pasajeroId}")
    public ResponseEntity<List<BilleteResumenResponse>> billetesDePasajero(@PathVariable String pasajeroId) {
        ValidationUtil.validarMongoId(pasajeroId, "pasajeroId");
        List<Billete> billetes = billeteService.findBilletesDePasajeroDelUsuarioActual(pasajeroId);

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
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{billeteId}/cancelar")
    public ResponseEntity<CompraBilleteResponse> cancelar(@PathVariable String billeteId) {
        ValidationUtil.validarMongoId(billeteId, "billeteId");
        log.info("Cancelación billete: {}", billeteId);

        Billete billete = billeteService.cancelarDelUsuarioActual(billeteId);

        return ResponseEntity.ok(
                CompraBilleteResponse.builder()
                        .billeteId(billete.getId())
                        .codigoBillete(billete.getCodigoBillete())
                        .estado(billete.getEstado().name())
                        .build()
        );
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<DisponibilidadBilleteResponse> disponibilidad(@RequestParam String horarioId) {
        ValidationUtil.validarMongoId(horarioId, "horarioId");
        log.info("Disponibilidad billetes: horarioId={}", horarioId);
        return ResponseEntity.ok(billeteService.getDisponibilidad(horarioId));
    }
}