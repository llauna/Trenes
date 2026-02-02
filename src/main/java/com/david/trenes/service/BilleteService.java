package com.david.trenes.service;

import com.david.trenes.dto.DisponibilidadBilleteResponse;
import com.david.trenes.model.Billete;
import com.david.trenes.model.Horario;
import com.david.trenes.model.Tren;
import com.david.trenes.repository.BilleteRepository;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.PasajeroRepository;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Billete> comprarVarios(String horarioId, List<String> pasajeroIds,
                                       String origenId, String destinoId, String clase)  {

        String usuarioId = currentUserService.getCurrentUsuarioId();

        if (pasajeroIds == null || pasajeroIds.isEmpty()) {
            throw new RuntimeException("Debes indicar al menos un pasajero");
        }

        for (String pasajeroId : pasajeroIds) {
            if (!pasajeroRepository.existsByIdAndUsuarioId(pasajeroId, usuarioId)) {
                throw new RuntimeException("No puedes comprar billetes para un pasajero que no es tuyo: " + pasajeroId);
            }
        }

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + horarioId));

        validarParadas(horario, origenId, destinoId);

        Tren tren = trenRepository.findById(horario.getTrenId())
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + horario.getTrenId()));

        int capEfectiva = calcularCapacidadEfectiva(horario, tren);

        long vendidos = billeteRepository.countByHorarioIdAndEstado(horarioId, Billete.EstadoBillete.COMPRADO);
        int q = pasajeroIds.size();

        if (vendidos + q > capEfectiva) {
            throw new RuntimeException("No hay plazas disponibles. Capacidad=" + capEfectiva + ", vendidos=" + vendidos);
        }

        double tarifaBase = (horario.getTarifa() == null) ? 0.0 : horario.getTarifa();
        double precioUnitario = tarifaBase;

        List<Billete> billetes = pasajeroIds.stream().map(pasajeroId ->
                Billete.builder()
                        .codigoBillete("BIL-" + UUID.randomUUID())
                        .horarioId(horarioId)
                        .pasajeroId(pasajeroId)
                        .estacionOrigenId(origenId)
                        .estacionDestinoId(destinoId)
                        .clase(clase)
                        .cantidad(1)
                        .precioTotal(precioUnitario)
                        .estado(Billete.EstadoBillete.COMPRADO)
                        .fechaCompra(LocalDateTime.now())
                        .fechaActualizacion(LocalDateTime.now())
                        .build()
        ).toList();

        List<Billete> guardados = billeteRepository.saveAll(billetes);

        recalcularOcupacion(horarioId, capEfectiva);
        return guardados;
    }

    @Transactional(readOnly = true)
    public List<Billete> findBilletesDelUsuarioActual() {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        List<String> pasajeroIds = pasajeroRepository.findByUsuarioId(usuarioId).stream()
                .map(p -> p.getId())
                .toList();

        if (pasajeroIds.isEmpty()) return List.of();
        return billeteRepository.findByPasajeroIdIn(pasajeroIds);
    }

    @Transactional(readOnly = true)
    public List<Billete> findBilletesDePasajeroDelUsuarioActual(String pasajeroId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        if (!pasajeroRepository.existsByIdAndUsuarioId(pasajeroId, usuarioId)) {
            throw new RuntimeException("No tienes permisos para ver billetes de este pasajero");
        }
        return billeteRepository.findByPasajeroId(pasajeroId);
    }

    @Transactional(readOnly = true)
    public DisponibilidadBilleteResponse getDisponibilidad(String horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + horarioId));

        Tren tren = trenRepository.findById(horario.getTrenId())
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + horario.getTrenId()));

        int capEfectiva = calcularCapacidadEfectiva(horario, tren);
        long vendidos = billeteRepository.countByHorarioIdAndEstado(horarioId, Billete.EstadoBillete.COMPRADO);
        int disponibles = Math.max(0, capEfectiva - (int) vendidos);

        double ocupacion = (capEfectiva > 0) ? ((double) vendidos / (double) capEfectiva) * 100.0 : 0.0;

        return DisponibilidadBilleteResponse.builder()
                .horarioId(horarioId)
                .trenId(horario.getTrenId())
                .capacidadEfectiva(capEfectiva)
                .vendidos((int) vendidos)
                .disponibles(disponibles)
                .ocupacionPorcentaje(ocupacion)
                .build();
    }

    public Billete cancelarDelUsuarioActual(String billeteId) {
        String usuarioId = currentUserService.getCurrentUsuarioId();

        Billete billete = billeteRepository.findById(billeteId)
                .orElseThrow(() -> new RuntimeException("Billete no encontrado con ID: " + billeteId));

        // Verificación de propiedad: el billete debe pertenecer a un pasajero del usuario autenticado
        if (!pasajeroRepository.existsByIdAndUsuarioId(billete.getPasajeroId(), usuarioId)) {
            throw new RuntimeException("No tienes permisos para cancelar este billete");
        }

        billete.setEstado(Billete.EstadoBillete.CANCELADO);
        billete.setFechaActualizacion(LocalDateTime.now());
        Billete actualizado = billeteRepository.save(billete);

        // Recalcular ocupación del horario (capacidad efectiva tren+horario)
        Horario horario = horarioRepository.findById(billete.getHorarioId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + billete.getHorarioId()));

        Tren tren = trenRepository.findById(horario.getTrenId())
                .orElseThrow(() -> new RuntimeException("Tren no encontrado con ID: " + horario.getTrenId()));

        recalcularOcupacion(horario.getId(), calcularCapacidadEfectiva(horario, tren));

        return actualizado;
    }

    private void recalcularOcupacion(String horarioId, int capacidadEfectiva) {
        long vendidos = billeteRepository.countByHorarioIdAndEstado(horarioId, Billete.EstadoBillete.COMPRADO);

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + horarioId));

        horario.setPasajerosActuales((int) vendidos);
        if (capacidadEfectiva > 0) {
            horario.setOcupacionPorcentaje(((double) vendidos / (double) capacidadEfectiva) * 100.0);
        } else {
            horario.setOcupacionPorcentaje(0.0);
        }
        horario.setFechaActualizacion(LocalDateTime.now());
        horarioRepository.save(horario);
    }

    private int calcularCapacidadEfectiva(Horario horario, Tren tren) {
        Integer capTren = tren.getCapacidadPasajeros();
        Integer capHorario = horario.getCapacidadPasajeros();

        if (capTren == null && capHorario == null) return 0;
        if (capTren == null) return Math.max(0, capHorario);
        if (capHorario == null) return Math.max(0, capTren);
        return Math.max(0, Math.min(capTren, capHorario));
    }

    private void validarParadas(Horario horario, String origenId, String destinoId) {
        if (horario.getParadas() == null || horario.getParadas().isEmpty()) {
            throw new RuntimeException("No se puede vender billete: el horario no tiene paradas definidas");
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
            throw new RuntimeException("No se puede vender billete: el tren no para en origen o destino para este horario");
        }
        if (ordenOrigen >= ordenDestino) {
            throw new RuntimeException("No se puede vender billete: el destino no está después del origen en las paradas del horario");
        }
    }

}
