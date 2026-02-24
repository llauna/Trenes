package com.david.trenes.controller;

import com.david.trenes.model.Horario;
import com.david.trenes.model.Ruta;
import com.david.trenes.service.GestionHorariosService;
import com.david.trenes.service.RutaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/paradas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class ParadaController {

    private final RutaService rutaService;
    private final GestionHorariosService gestionHorariosService;

    @PostMapping("/ruta/{rutaId}/agregar-parada")
    public ResponseEntity<String> agregarParadaARuta(
            @PathVariable String rutaId,
            @RequestParam String estacionId,
            @RequestParam String nombreEstacion,
            @RequestParam Integer orden,
            @RequestParam Double kilometro,
            @RequestParam Integer tiempoParadaMinutos,
            @RequestParam Boolean obligatoria) {
        
        log.info("Agregando parada {} a la ruta {}", nombreEstacion, rutaId);
        
        try {
            Ruta ruta = rutaService.findById(rutaId).orElse(null);
            if (ruta == null) {
                return ResponseEntity.notFound().build();
            }

            Ruta.ParadaRuta nuevaParada = Ruta.ParadaRuta.builder()
                    .estacionId(estacionId)
                    .nombreEstacion(nombreEstacion)
                    .orden(orden)
                    .kilometro(kilometro)
                    .tiempoParadaMinutos(tiempoParadaMinutos)
                    .obligatoria(obligatoria)
                    .build();

            ruta.getEstacionesIntermedias().add(nuevaParada);
            rutaService.save(ruta);

            return ResponseEntity.ok("Parada agregada exitosamente a la ruta");
        } catch (Exception e) {
            log.error("Error al agregar parada a la ruta", e);
            return ResponseEntity.internalServerError()
                    .body("Error al agregar parada: " + e.getMessage());
        }
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Ruta.ParadaRuta>> obtenerParadasDeRuta(@PathVariable String rutaId) {
        log.info("Obteniendo paradas de la ruta: {}", rutaId);
        
        Ruta ruta = rutaService.findById(rutaId).orElse(null);
        if (ruta == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(ruta.getEstacionesIntermedias());
    }

    @GetMapping("/horario/{horarioId}")
    public ResponseEntity<List<Horario.ParadaHorario>> obtenerParadasDeHorario(@PathVariable String horarioId) {
        log.info("Obteniendo paradas del horario: {}", horarioId);
        
        List<Horario> horarios = gestionHorariosService.obtenerHorariosPorRuta(horarioId);
        if (horarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(horarios.get(0).getParadas());
    }

    @PatchMapping("/horario/{horarioId}/parada/{orden}/estado")
    public ResponseEntity<String> actualizarEstadoParada(
            @PathVariable String horarioId,
            @PathVariable Integer orden,
            @RequestParam Horario.EstadoParada nuevoEstado) {
        
        log.info("Actualizando estado de parada {} del horario {} a: {}", orden, horarioId, nuevoEstado);
        
        try {
            boolean resultado = gestionHorariosService.actualizarEstadoParada(horarioId, orden, nuevoEstado);
            
            if (resultado) {
                return ResponseEntity.ok("Estado de parada actualizado exitosamente");
            } else {
                return ResponseEntity.badRequest()
                        .body("No se pudo actualizar el estado de la parada");
            }
        } catch (Exception e) {
            log.error("Error al actualizar estado de parada", e);
            return ResponseEntity.internalServerError()
                    .body("Error al actualizar estado de parada: " + e.getMessage());
        }
    }
}
