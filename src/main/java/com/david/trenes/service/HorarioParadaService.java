package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Ruta;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HorarioParadaService {

    private final HorarioRepository horarioRepository;
    private final RutaRepository rutaRepository;

    /**
     * Actualiza todos los horarios con las paradas de sus rutas
     */
    public void actualizarParadasEnTodosLosHorarios() {
        log.info("Iniciando actualización de paradas en todos los horarios");
        
        List<Horario> horarios = horarioRepository.findAll();
        int actualizados = 0;
        int errores = 0;
        
        for (Horario horario : horarios) {
            try {
                if (actualizarParadasDeHorario(horario)) {
                    actualizados++;
                }
            } catch (Exception e) {
                log.error("Error actualizando paradas del horario {}: {}", 
                        horario.getId(), e.getMessage(), e);
                errores++;
            }
        }
        
        log.info("Actualización completada. Horarios actualizados: {}, Errores: {}", 
                actualizados, errores);
    }

    /**
     * Actualiza las paradas de un horario específico usando su ruta
     */
    public boolean actualizarParadasDeHorario(Horario horario) {
        if (horario.getRutaId() == null || horario.getRutaId().isBlank()) {
            log.warn("Horario {} no tiene rutaId asignado", horario.getId());
            return false;
        }

        Ruta ruta = rutaRepository.findById(horario.getRutaId()).orElse(null);
        if (ruta == null) {
            log.warn("No se encontró la ruta {} para el horario {}", 
                    horario.getRutaId(), horario.getId());
            return false;
        }

        // Convertir ParadaRuta a ParadaHorario
        List<Horario.ParadaHorario> paradasHorario = new ArrayList<>();
        
        // Agregar estación de origen
        if (ruta.getEstacionOrigenId() != null && !ruta.getEstacionOrigenId().isBlank()) {
            paradasHorario.add(Horario.ParadaHorario.builder()
                    .estacionId(ruta.getEstacionOrigenId())
                    .nombreEstacion(obtenerNombreEstacion(ruta.getEstacionOrigenId()))
                    .orden(0)
                    .build());
        }

        // Agregar estaciones intermedias
        if (ruta.getEstacionesIntermedias() != null) {
            for (Ruta.ParadaRuta paradaRuta : ruta.getEstacionesIntermedias()) {
                if (paradaRuta.getEstacionId() != null && !paradaRuta.getEstacionId().isBlank()) {
                    paradasHorario.add(Horario.ParadaHorario.builder()
                            .estacionId(paradaRuta.getEstacionId())
                            .nombreEstacion(paradaRuta.getNombreEstacion())
                            .orden(paradaRuta.getOrden() != null ? paradaRuta.getOrden() : 1)
                            .build());
                }
            }
        }

        // Agregar estación de destino
        if (ruta.getEstacionDestinoId() != null && !ruta.getEstacionDestinoId().isBlank()) {
            int ultimoOrden = paradasHorario.size();
            paradasHorario.add(Horario.ParadaHorario.builder()
                    .estacionId(ruta.getEstacionDestinoId())
                    .nombreEstacion(obtenerNombreEstacion(ruta.getEstacionDestinoId()))
                    .orden(ultimoOrden)
                    .build());
        }

        // Actualizar el horario
        horario.setParadas(paradasHorario);
        horario.setFechaActualizacion(java.time.LocalDateTime.now());
        
        Horario guardado = horarioRepository.save(horario);
        log.info("Horario {} actualizado con {} paradas", guardado.getId(), paradasHorario.size());
        
        return true;
    }

    /**
     * Obtiene el nombre de una estación (simulado - en un caso real buscaría en la BD)
     */
    private String obtenerNombreEstacion(String estacionId) {
        // Por ahora retornamos el ID como nombre
        // En una implementación completa, buscaríamos en la base de datos de estaciones
        return "Estacion_" + estacionId.substring(0, Math.min(8, estacionId.length()));
    }
}
