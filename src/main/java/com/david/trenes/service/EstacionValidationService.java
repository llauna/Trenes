package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Estacion;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.EstacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstacionValidationService {

    private final HorarioRepository horarioRepository;
    private final EstacionRepository estacionRepository;

    /**
     * Analiza qué estaciones faltan comparando paradas de horarios con estaciones existentes
     */
    public Map<String, Object> analizarEstacionesFaltantes() {
        log.info("Analizando estaciones faltantes en paradas de horarios");
        
        List<Horario> horarios = horarioRepository.findAll();
        List<Estacion> estaciones = estacionRepository.findAll();
        
        // Obtener todos los estacionId de las paradas de horarios
        Set<String> estacionIdsEnParadas = horarios.stream()
                .filter(h -> h.getParadas() != null && !h.getParadas().isEmpty())
                .flatMap(h -> h.getParadas().stream())
                .map(Horario.ParadaHorario::getEstacionId)
                .filter(Objects::nonNull)
                .filter(id -> !id.isBlank())
                .collect(Collectors.toSet());
        
        // Obtener todos los estacionId existentes
        Set<String> estacionIdsExistentes = estaciones.stream()
                .map(Estacion::getId)
                .collect(Collectors.toSet());
        
        // Encontrar las que faltan
        Set<String> estacionIdsFaltantes = new HashSet<>(estacionIdsEnParadas);
        estacionIdsFaltantes.removeAll(estacionIdsExistentes);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalHorarios", horarios.size());
        resultado.put("totalEstaciones", estaciones.size());
        resultado.put("estacionIdsEnParadas", estacionIdsEnParadas.size());
        resultado.put("estacionIdsExistentes", estacionIdsExistentes.size());
        resultado.put("estacionIdsFaltantes", estacionIdsFaltantes);
        resultado.put("porcentajeFaltante", estacionIdsEnParadas.isEmpty() ? 0 : 
                (double) estacionIdsFaltantes.size() / estacionIdsEnParadas.size() * 100);
        
        // Detalles de las estaciones faltantes
        List<Map<String, Object>> detallesFaltantes = estacionIdsFaltantes.stream()
                .map(estacionId -> {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("estacionId", estacionId);
                    detalle.put("horariosAfectados", 
                            horarios.stream()
                                    .filter(h -> h.getParadas() != null && 
                                               h.getParadas().stream()
                                                       .anyMatch(p -> estacionId.equals(p.getEstacionId())))
                                    .map(h -> Map.of(
                                            "horarioId", h.getId(),
                                            "codigoServicio", h.getCodigoServicio()
                                    ))
                                    .limit(5) // Limitar para no sobrecargar
                                    .collect(Collectors.toList()));
                    return detalle;
                })
                .collect(Collectors.toList());
        
        resultado.put("detallesFaltantes", detallesFaltantes);
        
        log.info("Análisis completado. Estaciones faltantes: {} de {}", 
                estacionIdsFaltantes.size(), estacionIdsEnParadas.size());
        
        return resultado;
    }

    /**
     * Crea estaciones faltantes basadas en las paradas de horarios
     */
    public Map<String, Object> crearEstacionesFaltantes() {
        log.info("Creando estaciones faltantes");
        
        Map<String, Object> analisis = analizarEstacionesFaltantes();
        @SuppressWarnings("unchecked")
        Set<String> estacionIdsFaltantes = (Set<String>) analisis.get("estacionIdsFaltantes");
        
        if (estacionIdsFaltantes.isEmpty()) {
            return Map.of(
                    "creadas", 0,
                    "mensaje", "No faltan estaciones por crear"
            );
        }
        
        List<Estacion> estacionesNuevas = new ArrayList<>();
        int creadas = 0;
        
        for (String estacionId : estacionIdsFaltantes) {
            Estacion nuevaEstacion = Estacion.builder()
                    .id(estacionId)
                    .nombre("Estacion_" + estacionId.substring(0, Math.min(8, estacionId.length())))
                    .codigoEstacion("EST_" + estacionId.substring(0, Math.min(6, estacionId.length())).toUpperCase())
                    .ciudad("Ciudad_" + estacionId.substring(0, Math.min(4, estacionId.length())).toUpperCase())
                    .ubicacion(com.david.trenes.model.Via.Coordenada.builder()
                            .latitud(40.0 + Math.random() * 10) // Coordenadas aleatorias en España
                            .longitud(-3.0 + Math.random() * 5)
                            .build())
                    .activo(true)
                    .fechaCreacion(java.time.LocalDateTime.now())
                    .fechaActualizacion(java.time.LocalDateTime.now())
                    .build();
            
            estacionesNuevas.add(nuevaEstacion);
            creadas++;
        }
        
        // Guardar todas las estaciones nuevas
        estacionRepository.saveAll(estacionesNuevas);
        
        log.info("Creadas {} estaciones nuevas", creadas);
        
        return Map.of(
                "creadas", creadas,
                "totalAnterior", analisis.get("totalEstaciones"),
                "totalNuevo", (Integer) analisis.get("totalEstaciones") + creadas,
                "mensaje", "Estaciones creadas exitosamente"
        );
    }
}
