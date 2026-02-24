package com.david.trenes.service;

import com.david.trenes.dto.DisponibilidadBilleteResponse;
import com.david.trenes.dto.RecalcularAsientosResponse;
import com.david.trenes.exception.NoHayPlazasException;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BilleteService {

    private static final int CAP_VAGON_TURISTA = 60;
    private static final int CAP_VAGON_PRIMERA = 40;

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
        return comprarVariosInternal(horarioId, pasajeroIds, origenId, destinoId, clase, false);
    }

    public List<Billete> comprarVariosMasivo(String horarioId,
                                             List<String> pasajeroIds,
                                             String origenId,
                                             String destinoId,
                                             String clase) {
        return comprarVariosInternal(horarioId, pasajeroIds, origenId, destinoId, clase, true);
    }

    private List<Billete> comprarVariosInternal(String horarioId,
                                                List<String> pasajeroIds,
                                                String origenId,
                                                String destinoId,
                                                String clase,
                                                boolean permitirAmpliacionMasiva) {

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

        inventarioHorarioService.ensureInventario(horario, tren);

        int q = pasajeroIdsNorm.size();

        boolean ok = inventarioHorarioService.intentarVender(horarioId, claseEnum, q);

        // Solo en MASIVA: si no hay plazas, se permite añadir como máximo 1 vagón y reintentar.
        if (!ok && permitirAmpliacionMasiva) {
            boolean ampliado = intentarAmpliarCapacidadConMaximoUnVagon(horario, tren, claseEnum);
            if (ampliado) {
                ok = inventarioHorarioService.intentarVender(horarioId, claseEnum, q);
            }
        }

        if (!ok) {
            String sugerencia = sugerirProximoServicioMismoOrigenDestino(horario, origenId, destinoId);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No hay plazas disponibles en clase " + claseNorm + " para este horario."
                            + (sugerencia.isBlank() ? "" : " " + sugerencia)
            );
        }

        double tarifaBase = (horario.getTarifa() == null) ? 0.0 : horario.getTarifa();
        double precioUnitario = tarifaBase;

        int capPorVagon = (claseEnum == Clase.PRIMERA) ? CAP_VAGON_PRIMERA : CAP_VAGON_TURISTA;

        InventarioHorario invDespuesDeVender = inventarioHorarioService.getInventarioOrThrow(horarioId);
        int vendidosClaseDespues = (claseEnum == Clase.PRIMERA)
                ? nvl(invDespuesDeVender.getVendidosPrimera())
                : nvl(invDespuesDeVender.getVendidosTurista());

        int startSeatIndex = Math.max(1, vendidosClaseDespues - q + 1);

        List<Billete> billetes = new ArrayList<>(pasajeroIdsNorm.size());
        for (int i = 0; i < pasajeroIdsNorm.size(); i++) {
            String pasajeroId = pasajeroIdsNorm.get(i);

            int seatIndex = startSeatIndex + i; // 1..N
            int vagonNumero = ((seatIndex - 1) / capPorVagon) + 1;
            int asientoNumero = ((seatIndex - 1) % capPorVagon) + 1;

            billetes.add(
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
                            .vagonNumero(vagonNumero)
                            .asientoNumero(asientoNumero)
                            .build()
            );
        }

        try {
            List<Billete> guardados = billeteRepository.saveAll(billetes);
            recalcularOcupacionDesdeInventario(horarioId);
            return guardados;
        } catch (RuntimeException e) {
            inventarioHorarioService.compensarVenta(horarioId, claseEnum, q);
            throw e;
        }
    }

    // ==========================================================
    // TEMPORAL: recalcular/asignar asientos a billetes antiguos
    // ==========================================================
    @Transactional
    public RecalcularAsientosResponse recalcularAsientosAntiguos(String horarioId, boolean overwrite, boolean dryRun) {

        List<Billete> candidatos = (horarioId != null && !horarioId.isBlank())
                ? billeteRepository.findConUbicacionFaltanteByHorarioId(horarioId.trim())
                : billeteRepository.findConUbicacionFaltante();

        int totalEncontrados = candidatos.size();
        if (totalEncontrados == 0) {
            return RecalcularAsientosResponse.builder()
                    .dryRun(dryRun)
                    .overwrite(overwrite)
                    .totalEncontrados(0)
                    .totalProcesados(0)
                    .totalActualizados(0)
                    .actualizadosPorHorario(Map.of())
                    .build();
        }

        Map<String, List<Billete>> grupos = new HashMap<>();
        for (Billete b : candidatos) {
            String hId = (b.getHorarioId() == null) ? "" : b.getHorarioId().trim();
            String claseN = safeNormalizarClaseParaRecalculo(b.getClase());
            String key = hId + "||" + claseN;
            grupos.computeIfAbsent(key, k -> new ArrayList<>()).add(b);
        }

        Map<String, Integer> actualizadosPorHorario = new HashMap<>();
        List<Billete> paraGuardar = new ArrayList<>();

        for (List<Billete> lista : grupos.values()) {
            lista.sort(Comparator
                    .comparing((Billete b) -> b.getFechaCompra() == null ? LocalDateTime.MIN : b.getFechaCompra())
                    .thenComparing(b -> b.getId() == null ? "" : b.getId())
            );

            String claseN = safeNormalizarClaseParaRecalculo(lista.get(0).getClase());
            int capPorVagon = "PRIMERA".equals(claseN) ? CAP_VAGON_PRIMERA : CAP_VAGON_TURISTA;

            int seatIndex = 0;
            for (Billete b : lista) {
                boolean tieneUbicacion = b.getVagonNumero() != null && b.getAsientoNumero() != null;
                if (tieneUbicacion && !overwrite) continue;

                seatIndex++;
                int vagonNumero = ((seatIndex - 1) / capPorVagon) + 1;
                int asientoNumero = ((seatIndex - 1) % capPorVagon) + 1;

                b.setVagonNumero(vagonNumero);
                b.setAsientoNumero(asientoNumero);
                b.setFechaActualizacion(LocalDateTime.now());

                paraGuardar.add(b);
                actualizadosPorHorario.merge(b.getHorarioId(), 1, Integer::sum);
            }
        }

        if (!dryRun && !paraGuardar.isEmpty()) {
            billeteRepository.saveAll(paraGuardar);
        }

        return RecalcularAsientosResponse.builder()
                .dryRun(dryRun)
                .overwrite(overwrite)
                .totalEncontrados(totalEncontrados)
                .totalProcesados(totalEncontrados)
                .totalActualizados(paraGuardar.size())
                .actualizadosPorHorario(actualizadosPorHorario)
                .build();
    }

    private String safeNormalizarClaseParaRecalculo(String clase) {
        if (clase == null) return "TURISTA";
        String c = clase.trim().toUpperCase();
        if (c.equals("PRIMERA") || c.equals("PRIMERA CLASE") || c.equals("1A") || c.equals("1")) return "PRIMERA";
        return "TURISTA";
    }

    private String sugerirProximoServicioMismoOrigenDestino(Horario horarioActual, String origenId, String destinoId) {
        if (origenId == null || origenId.isBlank() || destinoId == null || destinoId.isBlank()) return "";
        if (horarioActual == null || horarioActual.getFechaSalida() == null) return "";

        Optional<Horario> proximo = horarioRepository
                .findFirstByEstacionOrigenIdAndEstacionDestinoIdAndActivoTrueAndFechaSalidaAfterOrderByFechaSalidaAsc(
                        origenId,
                        destinoId,
                        horarioActual.getFechaSalida()
                );

        return proximo
                .map(h -> "Siguiente servicio (mismo origen/destino): " + h.getCodigoServicio()
                        + " (sale " + h.getFechaSalida() + ").")
                .orElse("");
    }

    private boolean intentarAmpliarCapacidadConMaximoUnVagon(Horario horario, Tren tren, Clase clase) {

        int capPorVagon = (clase == Clase.PRIMERA) ? CAP_VAGON_PRIMERA : CAP_VAGON_TURISTA;

        if (tren.getVagones() == null) {
            tren.setVagones(new ArrayList<>());
        }

        Tren.Vagon nuevo = Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("AUTO-" + (tren.getVagones().size() + 1))
                .tipo(clase == Clase.PRIMERA ? Tren.TipoVagon.PASAJEROS_PRIMERA : Tren.TipoVagon.PASAJEROS_TURISTA)
                .capacidadPasajeros(capPorVagon)
                .activo(true)
                .observaciones("Vagón añadido automáticamente por falta de plazas (solo compra masiva, máx 1 vagón)")
                .build();

        tren.getVagones().add(nuevo);
        tren.setCapacidadPasajeros(nvl(tren.getCapacidadPasajeros()) + capPorVagon);
        tren.setFechaActualizacion(LocalDateTime.now());
        trenRepository.save(tren);

        inventarioHorarioService.incrementarCapacidad(horario.getId(), clase, capPorVagon);

        InventarioHorario inv2 = inventarioHorarioService.getInventarioOrThrow(horario.getId());
        int capacidadTotal = Math.max(0, nvl(inv2.getCapacidadTurista()) + nvl(inv2.getCapacidadPrimera()));
        horario.setCapacidadPasajeros(capacidadTotal);
        horario.setFechaActualizacion(LocalDateTime.now());
        horarioRepository.save(horario);

        return true;
    }

    private String sugerirProximoServicio(Horario horarioActual) {
        if (horarioActual.getRutaId() == null || horarioActual.getRutaId().isBlank()) return "";

        LocalDateTime salida = horarioActual.getFechaSalida();
        if (salida == null) return "";

        Optional<Horario> proximo = horarioRepository
                .findFirstByRutaIdAndActivoTrueAndFechaSalidaAfterOrderByFechaSalidaAsc(
                        horarioActual.getRutaId(),
                        salida
                );

        return proximo
                .map(h -> "Siguiente servicio disponible: " + h.getCodigoServicio()
                        + " (sale " + h.getFechaSalida() + ").")
                .orElse("");
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