package com.david.trenes.service;

import com.david.trenes.model.Tren;
import com.david.trenes.model.Via;
import com.david.trenes.repository.TrenRepository;
import com.david.trenes.repository.ViaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UbicacionTrenesService {

    private final TrenRepository trenRepository;
    private final ViaRepository viaRepository;
    private final Random random = new Random();

    /**
     * Ubica trenes detenidos (no en mantenimiento) en vías de estación disponibles
     */
    public void ubicarTrenesDetenidosEnEstaciones() {
        // 1. Obtener trenes detenidos que no están en mantenimiento
        List<Tren> trenesDetenidos = obtenerTrenesDetenidosParaUbicar();
        
        if (trenesDetenidos.isEmpty()) {
            log.info("No hay trenes detenidos para ubicar");
            return;
        }

        // 2. Obtener vías de estación disponibles
        List<Via> viasDisponibles = obtenerViasEstacionDisponibles();
        
        if (viasDisponibles.isEmpty()) {
            log.warn("No hay vías de estación disponibles para ubicar trenes");
            return;
        }

        log.info("Ubicando {} trenes detenidos en {} vías de estación disponibles", 
                trenesDetenidos.size(), viasDisponibles.size());

        // 3. Mezclar listas para distribución aleatoria
        List<Tren> trenesDetenidosMutables = new ArrayList<>(trenesDetenidos);
        List<Via> viasDisponiblesMutables = new ArrayList<>(viasDisponibles);
        
        Collections.shuffle(trenesDetenidosMutables);
        Collections.shuffle(viasDisponiblesMutables);

        // 4. Asignar trenes a vías
        int trenesUbicados = 0;
        List<Tren> trenesActualizados = new ArrayList<>();
        
        for (int i = 0; i < Math.min(trenesDetenidosMutables.size(), viasDisponiblesMutables.size()); i++) {
            Tren tren = trenesDetenidosMutables.get(i);
            Via via = viasDisponiblesMutables.get(i);

            // Ubicar tren en la vía
            ubicarTrenEnVia(tren, via);
            trenesActualizados.add(tren);
            trenesUbicados++;
        }

        // Guardar todos los trenes actualizados
        if (!trenesActualizados.isEmpty()) {
            trenRepository.saveAll(trenesActualizados);
        }

        log.info("Se ubicaron {} trenes en estaciones", trenesUbicados);
    }

    /**
     * Obtiene trenes detenidos que no están en mantenimiento y no tienen ubicación
     */
    private List<Tren> obtenerTrenesDetenidosParaUbicar() {
        List<Tren> trenesDetenidos = trenRepository.findByEstadoActual(Tren.EstadoTren.DETENIDO);
        
        return trenesDetenidos.stream()
                .filter(tren -> tren.getActivo() != null && tren.getActivo())
                .filter(tren -> tren.getEstadoActual() != Tren.EstadoTren.MANTENIMIENTO)
                .filter(tren -> tren.getViaActualId() == null || tren.getViaActualId().isEmpty())
                .filter(tren -> tren.getEstacionActualId() == null || tren.getEstacionActualId().isEmpty())
                .toList();
    }

    /**
     * Obtiene vías de estación disponibles para recibir trenes
     */
    private List<Via> obtenerViasEstacionDisponibles() {
        List<Via> viasOperativas = viaRepository.findByActivo(true);
        
        return viasOperativas.stream()
                .filter(via -> via.getEstado() == Via.EstadoVia.OPERATIVA)
                .filter(via -> via.getTipoVia() == Via.TipoVia.ANDEN || 
                              via.getTipoVia() == Via.TipoVia.VIA_MUERTA ||
                              via.getTipoVia() == Via.TipoVia.DESVIO)
                .filter(via -> via.getEstacionOrigenId() != null && !via.getEstacionOrigenId().isEmpty())
                .toList();
    }

    /**
     * Ubica un tren específico en una vía
     */
    private void ubicarTrenEnVia(Tren tren, Via via) {
        // Establecer vía actual
        tren.setViaActualId(via.getId());
        
        // Establecer estación actual (usando la estación de origen de la vía)
        tren.setEstacionActualId(via.getEstacionOrigenId());
        
        // Establecer ubicación coordinates (coordenadas de inicio de la vía)
        if (via.getCoordenadaInicio() != null) {
            tren.setUbicacionActual(via.getCoordenadaInicio());
        }
        
        // Establecer kilómetro actual (inicio de la vía)
        tren.setKilometroActual(0.0);
        
        // Cambiar estado a EN_ESTACION
        tren.setEstadoActual(Tren.EstadoTren.EN_ESTACION);
        
        // Actualizar timestamp
        tren.setFechaActualizacion(LocalDateTime.now());
        
        log.info("Tren {} ({}) ubicado en vía {} de estación {}", 
                tren.getId(), tren.getNumeroTren(), via.getCodigoVia(), via.getEstacionOrigenId());
    }

    /**
     * Ubica un tren específico en una estación (endpoint individual)
     */
    public Tren ubicarTrenEnEstacion(String trenId) {
        Tren tren = trenRepository.findById(trenId)
                .orElseThrow(() -> new RuntimeException("Tren no encontrado: " + trenId));

        if (tren.getEstadoActual() != Tren.EstadoTren.DETENIDO) {
            throw new IllegalStateException("El tren no está detenido. Estado actual: " + tren.getEstadoActual());
        }

        List<Via> viasDisponibles = obtenerViasEstacionDisponibles();
        
        if (viasDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay vías de estación disponibles");
        }

        // Elegir vía aleatoria
        Via viaSeleccionada = viasDisponibles.get(random.nextInt(viasDisponibles.size()));
        
        ubicarTrenEnVia(tren, viaSeleccionada);
        
        return trenRepository.save(tren);
    }

    /**
     * Devuelve trenes de estación a estado detenido (libera vías)
     */
    public void liberarViasDeEstacion() {
        List<Tren> trenesEnEstacion = trenRepository.findByEstadoActual(Tren.EstadoTren.EN_ESTACION);
        
        for (Tren tren : trenesEnEstacion) {
            // Liberar vía y estación
            tren.setViaActualId(null);
            tren.setEstacionActualId(null);
            tren.setUbicacionActual(null);
            tren.setKilometroActual(null);
            tren.setEstadoActual(Tren.EstadoTren.DETENIDO);
            tren.setFechaActualizacion(LocalDateTime.now());
        }
        
        if (!trenesEnEstacion.isEmpty()) {
            trenRepository.saveAll(trenesEnEstacion);
            log.info("Se liberaron {} trenes de las vías de estación", trenesEnEstacion.size());
        }
    }

    /**
     * Obtiene reporte de ubicación actual de trenes
     */
    public UbicacionReporte obtenerReporteUbicacion() {
        List<Tren> todosLosTrenes = trenRepository.findByActivo(true);
        
        long detenidos = todosLosTrenes.stream()
                .filter(t -> t.getEstadoActual() == Tren.EstadoTren.DETENIDO)
                .count();
        
        long enEstacion = todosLosTrenes.stream()
                .filter(t -> t.getEstadoActual() == Tren.EstadoTren.EN_ESTACION)
                .count();
        
        long enMantenimiento = todosLosTrenes.stream()
                .filter(t -> t.getEstadoActual() == Tren.EstadoTren.MANTENIMIENTO)
                .count();
        
        long sinUbicar = todosLosTrenes.stream()
                .filter(t -> (t.getViaActualId() == null || t.getViaActualId().isEmpty()) &&
                            (t.getEstacionActualId() == null || t.getEstacionActualId().isEmpty()))
                .count();

        return UbicacionReporte.builder()
                .totalTrenes(todosLosTrenes.size())
                .detenidos(detenidos)
                .enEstacion(enEstacion)
                .enMantenimiento(enMantenimiento)
                .sinUbicar(sinUbicar)
                .build();
    }

    /**
     * DTO para reporte de ubicación
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class UbicacionReporte {
        private Integer totalTrenes;
        private Long detenidos;
        private Long enEstacion;
        private Long enMantenimiento;
        private Long sinUbicar;
    }
}
