package com.david.trenes.service;

import com.david.trenes.dto.DisponibilidadBilleteResponse;
import com.david.trenes.model.Billete;
import com.david.trenes.model.Horario;
import com.david.trenes.model.InventarioHorario;
import com.david.trenes.model.Tren;
import com.david.trenes.repository.BilleteRepository;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.PasajeroRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.security.CurrentUserService;
import com.david.trenes.service.InventarioHorarioService.Clase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BilleteService {

    private final BilleteRepository billeteRepository;
    private final HorarioRepository horarioRepository;
    private final TrenRepository trenRepository;
    private final PasajeroRepository pasajeroRepository;
    private final CurrentUserService currentUserService;
    private final InventarioHorarioService inventarioHorarioService;

    public List<Billete> comprarVarios(String horarioId,
                                       List<String> pasajeroIds,
                                       String origenId,
                                       String destinoId,
                                       String clase) {

        String usuarioId = currentUserService.getCurrentUsuarioId();

        if (horarioId == null || horarioId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "horarioId es obligatorio");
        }
        if (origenId == null || origenId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origenId es obligatorio");
        }
        if (destinoId == null || destinoId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destinoId es obligatorio");
        }
        if (clase == null || clase.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clase es obligatoria");
        }
        if (pasajeroIds == null || pasajeroIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar al menos un pasajero");
        }

        String claseNorm = normalizarClase(clase);
        Clase claseEnum = "PRIMERA".equals(claseNorm) ? Clase.PRIMERA : Clase.TURISTA;

        List<String> pasajeroIdsNorm = pasajeroIds.stream()
                .map(id -> id == null ? null : id.trim())
                .toList();

        if (pasajeroIdsNorm.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pasajeroIds no puede contener valores nulos o vacíos");
        }

        // Permitimos duplicados (un pasajero puede comprar más de un billete para el mismo horario)
        // pero validamos permisos una sola vez por pasajeroId distinto.
        List<String> pasajeroIdsParaPermisos = pasajeroIdsNorm.stream().distinct().toList();

        for (String pasajeroId : pasajeroIdsParaPermisos) {
            if (!pasajeroRepository.existsByIdAndUsuarioId(pasajeroId, usuarioId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "No puedes comprar billetes para un pasajero que no es tuyo"
                );
            }
        }

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));

        if (horario.getTrenId() == null || horario.getTrenId().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Horario inválido: falta trenId (dato inconsistente en el sistema)"
            );
        }

        validarParadas(horario, origenId, destinoId);

        Tren tren = trenRepository.findById(horario.getTrenId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tren no encontrado"));

        // 1) Inicializa inventario (capacidad por clase ligada a vagones)
        inventarioHorarioService.ensureInventario(horario, tren);

        int q = pasajeroIdsNorm.size();

        // 2) Venta atómica: evita sobreventa
        boolean ok = inventarioHorarioService.intentarVender(horarioId, claseEnum, q);
        if (!ok) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No hay plazas disponibles en clase " + claseNorm + " para este horario"
            );
        }

        // 3) Crear billetes (1 billete = 1 asiento)
        double tarifaBase = (horario.getTarifa() == null) ? 0.0 : horario.getTarifa();
        double precioUnitario = tarifaBase;

        List<Billete> billetes = pasajeroIdsNorm.stream().map(pasajeroId ->
                Billete.builder()
                        .codigoBillete("BIL-" + UUID.randomUUID())
                        .horarioId(horarioId)
                        .pasajeroId(pasajeroId)
                        .estacionOrigenId(origenId)
                        .estacionDestinoId(destinoId)
                        .clase(claseNorm)
                        .cantidad(1)
                        .precioTotal(precioUnitario)
                        .estado(Billete.EstadoBillete.COMPRADO)
                        .fechaCompra(LocalDateTime.now())
                        .fechaActualizacion(LocalDateTime.now())
                        .build()
        ).toList();

        try {
            List<Billete> guardados = billeteRepository.saveAll(billetes);
            recalcularOcupacionDesdeInventario(horarioId);
            return guardados;
        } catch (RuntimeException e) {
            // compensación best-effort: devolvemos plazas al inventario si falló guardar billetes
            inventarioHorarioService.compensarVenta(horarioId, claseEnum, q);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Billete> findBilletesDelUsuarioActual() {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        List<String> pasajeroIds = pasajeroRepository.findByUsuarioId(usuarioId).stream()
                .map(p -> p.getId())
                .toList();

        if (pasajeroIds.isEmpty()) {
            return List.of();
        }
        return billeteRepository.findByPasajeroIdIn(pasajeroIds);
    }

    @Transactional(readOnly = true)
    public List<Billete> findBilletesDePasajeroDelUsuarioActual(String pasajeroId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        if (pasajeroId == null || pasajeroId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pasajeroId es obligatorio");
        }

        if (!pasajeroRepository.existsByIdAndUsuarioId(pasajeroId, usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para ver billetes de este pasajero");
        }

        return billeteRepository.findByPasajeroId(pasajeroId);
    }

    @Transactional(readOnly = true)
    public DisponibilidadBilleteResponse getDisponibilidad(String horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));

        if (horario.getTrenId() == null || horario.getTrenId().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Horario inválido: falta trenId (dato inconsistente en el sistema)"
            );
        }

        Tren tren = trenRepository.findById(horario.getTrenId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tren no encontrado"));

        InventarioHorario inv = inventarioHorarioService.ensureInventario(horario, tren);

        int capacidadTotal = Math.max(0, nvl(inv.getCapacidadTurista()) + nvl(inv.getCapacidadPrimera()));
        int vendidosTotal = Math.max(0, nvl(inv.getVendidosTurista()) + nvl(inv.getVendidosPrimera()));
        int disponibles = Math.max(0, capacidadTotal - vendidosTotal);
        double ocupacion = (capacidadTotal > 0) ? ((double) vendidosTotal / (double) capacidadTotal) * 100.0 : 0.0;

        return DisponibilidadBilleteResponse.builder()
                .horarioId(horarioId)
                .trenId(horario.getTrenId())
                .capacidadEfectiva(capacidadTotal)
                .vendidos(vendidosTotal)
                .disponibles(disponibles)
                .ocupacionPorcentaje(ocupacion)
                .build();
    }

    public Billete cancelarDelUsuarioActual(String billeteId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        Billete billete = billeteRepository.findById(billeteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Billete no encontrado"));

        if (!pasajeroRepository.existsByIdAndUsuarioId(billete.getPasajeroId(), usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para cancelar este billete");
        }

        if (billete.getEstado() == Billete.EstadoBillete.CANCELADO) {
            return billete;
        }

        billete.setEstado(Billete.EstadoBillete.CANCELADO);
        billete.setFechaActualizacion(LocalDateTime.now());
        Billete actualizado = billeteRepository.save(billete);

        String claseNorm = normalizarClase(billete.getClase());
        Clase claseEnum = "PRIMERA".equals(claseNorm) ? Clase.PRIMERA : Clase.TURISTA;

        inventarioHorarioService.compensarVenta(billete.getHorarioId(), claseEnum, 1);
        recalcularOcupacionDesdeInventario(billete.getHorarioId());

        return actualizado;
    }

    private void recalcularOcupacionDesdeInventario(String horarioId) {
        InventarioHorario inv = inventarioHorarioService.getInventarioOrThrow(horarioId);

        int capacidadTotal = Math.max(0, nvl(inv.getCapacidadTurista()) + nvl(inv.getCapacidadPrimera()));
        int vendidosTotal = Math.max(0, nvl(inv.getVendidosTurista()) + nvl(inv.getVendidosPrimera()));

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));

        horario.setPasajerosActuales(vendidosTotal);
        horario.setOcupacionPorcentaje(capacidadTotal > 0 ? ((double) vendidosTotal / (double) capacidadTotal) * 100.0 : 0.0);
        horario.setFechaActualizacion(LocalDateTime.now());
        horarioRepository.save(horario);
    }

    private static int nvl(Integer v) {
        return v == null ? 0 : v;
    }

    private String normalizarClase(String clase) {
        if (clase == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clase es obligatoria");
        }

        String c = clase.trim().toUpperCase();

        if (c.equals("PRIMERA") || c.equals("PRIMERA CLASE") || c.equals("1A") || c.equals("1")) return "PRIMERA";
        if (c.equals("TURISTA") || c.equals("2A") || c.equals("2")) return "TURISTA";

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clase inválida. Usa TURISTA o PRIMERA");
    }

    private void validarParadas(Horario horario, String origenId, String destinoId) {
        if (horario.getParadas() == null || horario.getParadas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede vender billete: el horario no tiene paradas definidas");
        }

        var paradasOrdenadas = horario.getParadas().stream()
                .sorted(Comparator.comparing(Horario.ParadaHorario::getOrden))
                .toList();

        Integer ordenOrigen = paradasOrdenadas.stream()
                .filter(p -> origenId.equals(p.getEstacionId()))
                .map(Horario.ParadaHorario::getOrden)
                .findFirst()
                .orElse(null);

        Integer ordenDestino = paradasOrdenadas.stream()
                .filter(p -> destinoId.equals(p.getEstacionId()))
                .map(Horario.ParadaHorario::getOrden)
                .findFirst()
                .orElse(null);

        if (ordenOrigen == null || ordenDestino == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede vender billete: el tren no para en origen o destino para este horario");
        }
        if (ordenOrigen >= ordenDestino) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede vender billete: el destino no está después del origen en las paradas del horario");
        }
    }
}