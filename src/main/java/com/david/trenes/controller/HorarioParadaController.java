package com.david.trenes.controller;

import com.david.trenes.service.HorarioParadaService;
import com.david.trenes.service.RutaValidationService;
import com.david.trenes.service.EstacionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/horario-paradas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class HorarioParadaController {

    private final HorarioParadaService horarioParadaService;
    private final RutaValidationService rutaValidationService;
    private final EstacionValidationService estacionValidationService;

    @PostMapping("/actualizar-todos")
    public ResponseEntity<String> actualizarParadasEnTodosLosHorarios() {
        log.info("Solicitada actualización de paradas en todos los horarios");
        
        try {
            horarioParadaService.actualizarParadasEnTodosLosHorarios();
            
            return ResponseEntity.ok("Paradas actualizadas correctamente en todos los horarios");
            
        } catch (Exception e) {
            log.error("Error actualizando paradas en horarios", e);
            return ResponseEntity.internalServerError()
                    .body("Error actualizando paradas: " + e.getMessage());
        }
    }

    @GetMapping("/analizar")
    public ResponseEntity<Map<String, Object>> analizarConsistencia() {
        log.info("Analizando consistencia entre horarios y rutas");
        
        try {
            Map<String, Object> analisis = rutaValidationService.analizarConsistencia();
            return ResponseEntity.ok(analisis);
            
        } catch (Exception e) {
            log.error("Error analizando consistencia", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error analizando consistencia: " + e.getMessage()));
        }
    }

    @GetMapping("/analizar-estaciones")
    public ResponseEntity<Map<String, Object>> analizarEstacionesFaltantes() {
        log.info("Analizando estaciones faltantes");
        
        try {
            Map<String, Object> analisis = estacionValidationService.analizarEstacionesFaltantes();
            return ResponseEntity.ok(analisis);
            
        } catch (Exception e) {
            log.error("Error analizando estaciones faltantes", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error analizando estaciones: " + e.getMessage()));
        }
    }

    @PostMapping("/asignar-rutas")
    public ResponseEntity<Map<String, Object>> asignarRutasAleatorias() {
        log.info("Asignando rutas aleatorias a horarios");
        
        try {
            Map<String, Object> resultado = rutaValidationService.asignarRutasAleatorias();
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            log.error("Error asignando rutas", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error asignando rutas: " + e.getMessage()));
        }
    }

    @PostMapping("/crear-estaciones")
    public ResponseEntity<Map<String, Object>> crearEstacionesFaltantes() {
        log.info("Creando estaciones faltantes");
        
        try {
            Map<String, Object> resultado = estacionValidationService.crearEstacionesFaltantes();
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            log.error("Error creando estaciones", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error creando estaciones: " + e.getMessage()));
        }
    }

    @PostMapping("/reparar-completo")
    public ResponseEntity<Map<String, Object>> repararCompleto() {
        log.info("Iniciando reparación completa del sistema");
        
        try {
            // Paso 1: Asignar rutas válidas
            Map<String, Object> asignacion = rutaValidationService.asignarRutasAleatorias();
            
            // Paso 2: Actualizar paradas
            horarioParadaService.actualizarParadasEnTodosLosHorarios();
            
            // Paso 3: Crear estaciones faltantes
            Map<String, Object> estacionesCreadas = estacionValidationService.crearEstacionesFaltantes();
            
            Map<String, Object> resultado = new java.util.HashMap<>();
            resultado.put("asignacionRutas", asignacion);
            resultado.put("actualizacionParadas", "Completado");
            resultado.put("estacionesCreadas", estacionesCreadas);
            resultado.put("mensaje", "Reparación completada exitosamente");
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            log.error("Error en reparación completa", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error en reparación: " + e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo() {
        String info = """
                Servicio de Paradas de Horarios:
                
                Endpoints disponibles:
                - GET /api/v1/horario-paradas/analizar
                  Analiza la consistencia entre horarios y rutas
                
                - GET /api/v1/horario-paradas/analizar-estaciones
                  Analiza qué estaciones faltan en las paradas de horarios
                
                - POST /api/v1/horario-paradas/asignar-rutas
                  Asigna rutas existentes a horarios con rutas inválidas
                
                - POST /api/v1/horario-paradas/crear-estaciones
                  Crea las estaciones que faltan según las paradas de horarios
                
                - POST /api/v1/horario-paradas/actualizar-todos
                  Actualiza todos los horarios con las paradas de sus rutas
                
                - POST /api/v1/horario-paradas/reparar-completo
                  Ejecuta la reparación completa (rutas + paradas + estaciones)
                
                Flujo recomendado:
                1. GET /analizar-estaciones - Verificar qué estaciones faltan
                2. POST /reparar-completo - Solucionar todo
                3. Probar compra masiva
                """;
        
        return ResponseEntity.ok(info);
    }
}
