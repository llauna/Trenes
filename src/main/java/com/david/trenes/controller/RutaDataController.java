package com.david.trenes.controller;

import com.david.trenes.model.Ruta;
import com.david.trenes.service.RutaDataService;
import com.david.trenes.service.RutaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rutas-data")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8082", "http://127.0.0.1:8082"})
public class RutaDataController {

    private final RutaDataService rutaDataService;
    private final RutaService rutaService;

    @PostMapping("/crear-rutas-ejemplo")
    public ResponseEntity<String> crearRutasDeEjemplo() {
        log.info("Creando rutas de ejemplo para enriquecer el sistema");
        
        try {
            rutaDataService.crearRutasDeEjemplo();
            return ResponseEntity.ok("Rutas de ejemplo creadas exitosamente");
        } catch (Exception e) {
            log.error("Error al crear rutas de ejemplo", e);
            return ResponseEntity.internalServerError()
                    .body("Error al crear rutas de ejemplo: " + e.getMessage());
        }
    }

    @PostMapping("/limpiar-rutas")
    public ResponseEntity<String> limpiarRutas() {
        log.info("Limpiando todas las rutas existentes");
        
        try {
            List<Ruta> rutas = rutaService.findAll();
            for (Ruta ruta : rutas) {
                rutaService.deleteById(ruta.getId());
            }
            return ResponseEntity.ok("Rutas limpiadas exitosamente. Se eliminaron " + rutas.size() + " rutas.");
        } catch (Exception e) {
            log.error("Error al limpiar rutas", e);
            return ResponseEntity.internalServerError()
                    .body("Error al limpiar rutas: " + e.getMessage());
        }
    }

    @GetMapping("/tipo/{tipoRuta}")
    public ResponseEntity<List<Ruta>> obtenerRutasPorTipo(@PathVariable Ruta.TipoRuta tipoRuta) {
        log.info("Obteniendo rutas por tipo: {}", tipoRuta);
        // Aquí se implementaría la lógica para obtener rutas por tipo
        // Por ahora retornamos una lista vacía
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<Ruta>> obtenerRutasPorEstacion(@PathVariable String estacionId) {
        log.info("Obteniendo rutas que pasan por la estación: {}", estacionId);
        // Aquí se implementaría la lógica para obtener rutas por estación
        // Por ahora retornamos una lista vacía
        return ResponseEntity.ok(List.of());
    }
}
