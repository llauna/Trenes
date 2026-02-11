package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Ruta;
import com.david.trenes.repository.HorarioRepository;
import com.david.trenes.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutaValidationService {

    private final HorarioRepository horarioRepository;
    private final RutaRepository rutaRepository;

    /**
     * Analiza la consistencia entre horarios y rutas
     */
    public Map<String, Object> analizarConsistencia() {
        log.info("Analizando consistencia entre horarios y rutas");
        
        List<Horario> horarios = horarioRepository.findAll();
        List<Ruta> rutas = rutaRepository.findAll();
        
        Set<String> rutaIdsEnHorarios = horarios.stream()
                .map(Horario::getRutaId)
                .filter(Objects::nonNull)
                .filter(id -> !id.isBlank())
                .collect(Collectors.toSet());
        
        Set<String> rutaIdsExistentes = rutas.stream()
                .map(Ruta::getId)
                .collect(Collectors.toSet());
        
        Set<String> rutaIdsFaltantes = new HashSet<>(rutaIdsEnHorarios);
        rutaIdsFaltantes.removeAll(rutaIdsExistentes);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalHorarios", horarios.size());
        resultado.put("totalRutas", rutas.size());
        resultado.put("rutaIdsEnHorarios", rutaIdsEnHorarios.size());
        resultado.put("rutaIdsExistentes", rutaIdsExistentes.size());
        resultado.put("rutaIdsFaltantes", rutaIdsFaltantes);
        resultado.put("horariosConRutasFaltantes", 
                horarios.stream()
                        .filter(h -> h.getRutaId() != null && 
                                   !h.getRutaId().isBlank() && 
                                   rutaIdsFaltantes.contains(h.getRutaId()))
                        .map(h -> Map.of(
                                "horarioId", h.getId(),
                                "rutaId", h.getRutaId(),
                                "codigoServicio", h.getCodigoServicio()
                        ))
                        .collect(Collectors.toList()));
        
        log.info("Análisis completado. Rutas faltantes: {}", rutaIdsFaltantes.size());
        
        return resultado;
    }

    /**
     * Asigna rutas existentes a horarios que tienen rutas inexistentes
     */
    public Map<String, Object> asignarRutasAleatorias() {
        log.info("Asignando rutas aleatorias a horarios con rutas inexistentes");
        
        List<Ruta> rutasExistentes = rutaRepository.findAll();
        List<Horario> horarios = horarioRepository.findAll();
        
        Set<String> rutaIdsExistentes = rutasExistentes.stream()
                .map(Ruta::getId)
                .collect(Collectors.toSet());
        
        List<Horario> horariosParaActualizar = horarios.stream()
                .filter(h -> h.getRutaId() == null || 
                           h.getRutaId().isBlank() || 
                           !rutaIdsExistentes.contains(h.getRutaId()))
                .collect(Collectors.toList());
        
        if (rutasExistentes.isEmpty()) {
            log.warn("No hay rutas existentes para asignar");
            return Map.of(
                    "actualizados", 0,
                    "mensaje", "No hay rutas existentes para asignar"
            );
        }
        
        Random random = new Random();
        int actualizados = 0;
        
        for (Horario horario : horariosParaActualizar) {
            Ruta rutaAleatoria = rutasExistentes.get(random.nextInt(rutasExistentes.size()));
            String rutaAnterior = horario.getRutaId();
            horario.setRutaId(rutaAleatoria.getId());
            horarioRepository.save(horario);
            
            log.info("Horario {} actualizado: ruta {} -> {}", 
                    horario.getId(), rutaAnterior, rutaAleatoria.getId());
            actualizados++;
        }
        
        return Map.of(
                "actualizados", actualizados,
                "totalHorarios", horarios.size(),
                "rutasExistentes", rutasExistentes.size(),
                "mensaje", "Asignación completada correctamente"
        );
    }
}
