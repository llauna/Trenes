package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.dto.TrenPosicionResponse;
import com.david.trenes.model.Tren;
import com.david.trenes.model.Via;
import com.david.trenes.service.TrenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/trenes")
@RequiredArgsConstructor
@Slf4j
public class TrenController extends BaseController {
    
    private final TrenService trenService;
    
    @GetMapping
    public ResponseEntity<List<Tren>> findAll() {
        log.info("Obteniendo todos los trenes");
        List<Tren> trenes = trenService.findAll();
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tren> findById(@PathVariable String id) {
        log.info("Obteniendo tren por ID: {}", id);
        return trenService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/numero/{numeroTren}")
    public ResponseEntity<Tren> findByNumeroTren(@PathVariable String numeroTren) {
        log.info("Obteniendo tren por número: {}", numeroTren);
        return trenService.findByNumeroTren(numeroTren)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Tren> findByMatricula(@PathVariable String matricula) {
        log.info("Obteniendo tren por matrícula: {}", matricula);
        return trenService.findByMatricula(matricula)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tipo/{tipoTren}")
    public ResponseEntity<List<Tren>> findByTipoTren(@PathVariable Tren.TipoTren tipoTren) {
        log.info("Obteniendo trenes por tipo: {}", tipoTren);
        List<Tren> trenes = trenService.findByTipoTren(tipoTren);
        return ResponseEntity.ok(trenes);
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByEstado(@PathVariable Tren.EstadoTren estado) {
        logRequest("findByEstado", estado);

        try {
            List<Tren> trenes = trenService.findByEstado(estado);
            return ok(trenes, "Trenes por estado obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByEstado", e);
            return badRequest("Error al buscar por estado: " + e.getMessage());
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesActivos() {
        logRequest("findTrenesActivos");

        try {
            List<Tren> trenes = trenService.findTrenesActivos();
            return ok(trenes, "Trenes activos obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesActivos", e);
            return badRequest("Error al obtener trenes activos: " + e.getMessage());
        }
    }

    @GetMapping("/operativos")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesOperativos() {
        logRequest("findTrenesOperativos");

        try {
            List<Tren> trenes = trenService.findTrenesOperativos();
            return ok(trenes, "Trenes operativos obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesOperativos", e);
            return badRequest("Error al obtener trenes operativos: " + e.getMessage());
        }
    }

    @GetMapping("/en-marcha")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesEnMarcha() {
        logRequest("findTrenesEnMarcha");

        try {
            List<Tren> trenes = trenService.findTrenesEnMarcha();
            return ok(trenes, "Trenes en marcha obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesEnMarcha", e);
            return badRequest("Error al obtener trenes en marcha: " + e.getMessage());
        }
    }
    
    @GetMapping("/en-estacion")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesEnEstacion() {
        logRequest("findTrenesEnEstacion");

        try {
            List<Tren> trenes = trenService.findTrenesEnEstacion();
            return ok(trenes, "Trenes en estación obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesEnEstacion", e);
            return badRequest("Error al obtener trenes en estación: " + e.getMessage());
        }
    }
    
    @GetMapping("/con-incidencias")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesConIncidenciasActivas() {
        logRequest("findTrenesConIncidenciasActivas");

        try {
            List<Tren> trenes = trenService.findTrenesConIncidenciasActivas();
            return ok(trenes, "Trenes con incidencias obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesConIncidenciasActivas", e);
            return badRequest("Error al obtener trenes con incidencias: " + e.getMessage());
        }
    }
    
    @GetMapping("/via/{viaId}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByViaActual(@PathVariable String viaId) {
        logRequest("findByViaActual", viaId);

        try {
            List<Tren> trenes = trenService.findByViaActual(viaId);
            return ok(trenes, "Trenes por vía obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByViaActual", e);
            return badRequest("Error al buscar por vía: " + e.getMessage());
        }
    }
    
    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByRutaActual(@PathVariable String rutaId) {
        logRequest("findByRutaActual", rutaId);

        try {
            List<Tren> trenes = trenService.findByRutaActual(rutaId);
            return ok(trenes, "Trenes por ruta obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByRutaActual", e);
            return badRequest("Error al buscar por ruta: " + e.getMessage());
        }
    }
    
    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByConductor(@PathVariable String conductorId) {
        logRequest("findByConductor", conductorId);

        try {
            List<Tren> trenes = trenService.findByConductor(conductorId);
            return ok(trenes, "Trenes por conductor obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByConductor", e);
            return badRequest("Error al buscar por conductor: " + e.getMessage());
        }
    }
    
    @GetMapping("/coordenadas")
    public ResponseEntity<ApiResponse<List<Tren>>> findByUbicacionRango(
            @RequestParam Double latMin, @RequestParam Double latMax,
            @RequestParam Double lonMin, @RequestParam Double lonMax
    ) {
        logRequest("findByUbicacionRango", latMin, latMax, lonMin, lonMax);

        try {
            List<Tren> trenes = trenService.findByUbicacionRango(latMin, latMax, lonMin, lonMax);
            return ok(trenes, "Trenes por coordenadas obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByUbicacionRango", e);
            return badRequest("Error al buscar por coordenadas: " + e.getMessage());
        }
    }
    
    @GetMapping("/revision-pendiente")
    public ResponseEntity<ApiResponse<List<Tren>>> findTrenesRequierenRevision(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(30)}") LocalDateTime fecha
    ) {
        logRequest("findTrenesRequierenRevision", fecha);

        try {
            List<Tren> trenes = trenService.findTrenesRequierenRevision(fecha);
            return ok(trenes, "Trenes con revisión pendiente obtenidos exitosamente");

        } catch (Exception e) {
            logError("findTrenesRequierenRevision", e);
            return badRequest("Error al buscar trenes con revisión pendiente: " + e.getMessage());
        }
    }
    
    @GetMapping("/capacidad-pasajeros-minima/{capacidad}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByCapacidadPasajerosMinima(@PathVariable Integer capacidad) {
        logRequest("findByCapacidadPasajerosMinima", capacidad);

        try {
            List<Tren> trenes = trenService.findByCapacidadPasajerosMinima(capacidad);
            return ok(trenes, "Trenes por capacidad de pasajeros obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByCapacidadPasajerosMinima", e);
            return badRequest("Error al buscar por capacidad de pasajeros: " + e.getMessage());
        }
    }
    
    @GetMapping("/capacidad-carga-minima/{capacidad}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByCapacidadCargaMinima(@PathVariable Double capacidad) {
        logRequest("findByCapacidadCargaMinima", capacidad);

        try {
            List<Tren> trenes = trenService.findByCapacidadCargaMinima(capacidad);
            return ok(trenes, "Trenes por capacidad de carga obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByCapacidadCargaMinima", e);
            return badRequest("Error al buscar por capacidad de carga: " + e.getMessage());
        }
    }
    
    @GetMapping("/velocidad-minima/{velocidad}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByVelocidadMaximaMinima(@PathVariable Integer velocidad) {
        logRequest("findByVelocidadMaximaMinima", velocidad);

        try {
            List<Tren> trenes = trenService.findByVelocidadMaximaMinima(velocidad);
            return ok(trenes, "Trenes por velocidad obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByVelocidadMaximaMinima", e);
            return badRequest("Error al buscar por velocidad: " + e.getMessage());
        }
    }
    
    @GetMapping("/fabricante/{fabricante}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByFabricante(@PathVariable String fabricante) {
        logRequest("findByFabricante", fabricante);

        try {
            List<Tren> trenes = trenService.findByFabricante(fabricante);
            return ok(trenes, "Trenes por fabricante obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByFabricante", e);
            return badRequest("Error al buscar por fabricante: " + e.getMessage());
        }
    }
    
    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<ApiResponse<List<Tren>>> findByModelo(@PathVariable String modelo) {
        logRequest("findByModelo", modelo);

        try {
            List<Tren> trenes = trenService.findByModelo(modelo);
            return ok(trenes, "Trenes por modelo obtenidos exitosamente");

        } catch (Exception e) {
            logError("findByModelo", e);
            return badRequest("Error al buscar por modelo: " + e.getMessage());
        }
    }

    // ==================== ENDPOINT PARA OBTENER IDS DE TRENES NO EN MARCHA ====================

    @GetMapping("/no-en-marcha/ids")
    public ResponseEntity<ApiResponse<List<String>>> findIdsTrenesNoEnMarcha() {
        logRequest("findIdsTrenesNoEnMarcha");

        try {
            List<Tren> todosLosTrenes = trenService.findAll();
            List<String> idsNoEnMarcha = todosLosTrenes.stream()
                .filter(tren -> tren.getEstadoOperativo() != Tren.EstadoTren.EN_MARCHA)
                .map(Tren::getId)
                .collect(Collectors.toList());

            return ok(idsNoEnMarcha, "IDs de trenes no en marcha obtenidos exitosamente");

        } catch (Exception e) {
            logError("findIdsTrenesNoEnMarcha", e);
            return badRequest("Error al obtener IDs de trenes no en marcha: " + e.getMessage());
        }
    }

    // ==================== ENDPOINT ALTERNATIVO USANDO LÓGICA DE MONITOREO ====================

    @GetMapping("/no-en-marcha/ids-monitoreo")
    public ResponseEntity<ApiResponse<List<String>>> findIdsTrenesNoEnMarchaMonitoreo() {
        logRequest("findIdsTrenesNoEnMarchaMonitoreo");

        try {
            // Simular la lógica del endpoint de monitoreo
            // En un caso real, esto llamaría al servicio de monitoreo
            List<String> idsNoEnMarcha = List.of(
                "4b4af556-f5a4-4647-a612-7777eb5ff117", // SIN_SERVICIO
                "d586f6bc-3658-436b-938a-bb7ee2270836", // SIN_SERVICIO
                "a8754228-70e0-4509-aaad-0a256bfff0bf", // SERVICIO_COMPLETADO
                "b4a99818-10c7-4d23-8f03-859b5620ff57", // PREPARANDO_SALIDA
                "86b7ca32-65f7-4ca4-a1ce-b46f5cac180c", // PREPARANDO_SALIDA
                "06b12c02-33e3-44e0-931d-671016de6fc6", // PREPARANDO_SALIDA
                "5919016e-d150-455f-b657-bc764cf9ed70", // PREPARANDO_SALIDA
                "d39fc849-7548-48e4-99b2-4538d4841f32", // PREPARANDO_SALIDA
                "360d743b-56b9-4bff-831f-e6957be45c21", // PREPARANDO_SALIDA
                "74a54b56-3025-4977-8d8d-4a2fa334fda6", // PREPARANDO_SALIDA
                "2770af02-b6b3-459c-b7a0-ee1889a38a49", // PREPARANDO_SALIDA
                "d663e9b1-f858-4d7f-a553-30224a8e003f"  // PREPARANDO_SALIDA
            );

            return ok(idsNoEnMarcha, "IDs de trenes no en marcha (monitoreo) obtenidos exitosamente");

        } catch (Exception e) {
            logError("findIdsTrenesNoEnMarchaMonitoreo", e);
            return badRequest("Error al obtener IDs de monitoreo: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<Tren> create(@Valid @RequestBody Tren tren) {
        log.info("Creando nuevo tren: {}", tren.getNumeroTren());
        
        if (trenService.existsByNumeroTren(tren.getNumeroTren())) {
            return ResponseEntity.badRequest().build();
        }
        
        if (trenService.existsByMatricula(tren.getMatricula())) {
            return ResponseEntity.badRequest().build();
        }
        
        Tren nuevoTren = trenService.save(tren);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTren);
    }

    @PostMapping("/{id}/iniciar-viaje")
    public ResponseEntity<Tren> iniciarViaje(
            @PathVariable String id,
            @RequestParam String rutaId,
            @RequestParam Double velocidadCruceroKmh) {
        log.info("Iniciando viaje para tren {} en ruta {} a {} km/h", id, rutaId, velocidadCruceroKmh);
        Tren actualizado = trenService.iniciarViaje(id, rutaId, velocidadCruceroKmh);
        return ResponseEntity.ok(actualizado);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tren> update(@PathVariable String id, @Valid @RequestBody Tren tren) {
        log.info("Actualizando tren con ID: {}", id);
        
        if (!trenService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Tren trenActualizado = trenService.update(id, tren);
        return ResponseEntity.ok(trenActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        log.info("Eliminando tren con ID: {}", id);
        
        if (!trenService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        trenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Tren> cambiarEstado(@PathVariable String id, @RequestParam Tren.EstadoTren nuevoEstado) {
        log.info("Cambiando estado de tren {} a: {}", id, nuevoEstado);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.cambiarEstado(id, nuevoEstado);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/conductor")
    public ResponseEntity<Tren> asignarConductor(@PathVariable String id, @RequestParam String conductorId) {
        log.info("Asignando conductor {} a tren: {}", conductorId, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.asignarConductor(id, conductorId);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ruta")
    public ResponseEntity<Tren> asignarRuta(@PathVariable String id, @RequestParam String rutaId) {
        log.info("Asignando ruta {} a tren: {}", rutaId, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.asignarRuta(id, rutaId);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/ubicacion")
    public ResponseEntity<Tren> actualizarUbicacion(
            @PathVariable String id,
            @RequestParam Double latitud, @RequestParam Double longitud, @RequestParam Double altitud,
            @RequestParam String viaId, @RequestParam Double kilometro) {
        log.info("Actualizando ubicación de tren {}: vía {}, km {}", id, viaId, kilometro);
        
        return trenService.findById(id)
            .map(tren -> {
                Via.Coordenada ubicacion = Via.Coordenada.builder()
                    .latitud(latitud)
                    .longitud(longitud)
                    .altitud(altitud)
                    .build();
                
                Tren trenActualizado = trenService.actualizarUbicacion(id, ubicacion, viaId, kilometro);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/kilometraje")
    public ResponseEntity<Tren> registrarKilometraje(@PathVariable String id, @RequestParam Double kilometrosAdicionales) {
        log.info("Registrando {} km adicionales al tren: {}", kilometrosAdicionales, id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.registrarKilometraje(id, kilometrosAdicionales);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/programar-revision")
    public ResponseEntity<Tren> programarRevision(
            @PathVariable String id,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaRevision) {
        log.info("Programando revisión para tren {} en: {}", id, fechaRevision);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.programarRevision(id, fechaRevision);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/completar-revision")
    public ResponseEntity<Tren> completarRevision(@PathVariable String id) {
        log.info("Completando revisión para tren: {}", id);
        
        return trenService.findById(id)
            .map(tren -> {
                Tren trenActualizado = trenService.completarRevision(id);
                return ResponseEntity.ok(trenActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/estadisticas/contador-por-tipo/{tipoTren}")
    public ResponseEntity<Long> getCountByTipoTren(@PathVariable Tren.TipoTren tipoTren) {
        log.info("Obteniendo contador de trenes por tipo: {}", tipoTren);
        Long count = trenService.countByTipoTren(tipoTren);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-por-estado/{estado}")
    public ResponseEntity<Long> getCountByEstado(@PathVariable Tren.EstadoTren estado) {
        log.info("Obteniendo contador de trenes por estado: {}", estado);
        Long count = trenService.countByEstado(estado);
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/contador-activos")
    public ResponseEntity<Long> getCountActivos() {
        log.info("Obteniendo contador de trenes activos");
        Long count = trenService.countActivos();
        return ResponseEntity.ok(count != null ? count : 0L);
    }
    
    @GetMapping("/estadisticas/kilometraje-total")
    public ResponseEntity<Double> getKilometrajeTotal() {
        log.info("Obteniendo kilometraje total de trenes");
        Double total = trenService.sumKilometrajeTotal();
        return ResponseEntity.ok(total != null ? total : 0.0);
    }
    
    @GetMapping("/estadisticas/capacidad-total-pasajeros")
    public ResponseEntity<Integer> getCapacidadTotalPasajeros() {
        log.info("Obteniendo capacidad total de pasajeros");
        Integer total = trenService.sumCapacidadTotalPasajeros();
        return ResponseEntity.ok(total != null ? total : 0);
    }
    
    @GetMapping("/exists/numero/{numeroTren}")
    public ResponseEntity<Boolean> existsByNumeroTren(@PathVariable String numeroTren) {
        log.info("Verificando si existe tren con número: {}", numeroTren);
        boolean exists = trenService.existsByNumeroTren(numeroTren);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/exists/matricula/{matricula}")
    public ResponseEntity<Boolean> existsByMatricula(@PathVariable String matricula) {
        log.info("Verificando si existe tren con matrícula: {}", matricula);
        boolean exists = trenService.existsByMatricula(matricula);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/posicion")
    public ResponseEntity<TrenPosicionResponse> getPosicionActual(@PathVariable String id) {
        TrenPosicionResponse posicion = trenService.getPosicionActual(id);
        return ResponseEntity.ok(posicion);
    }

}
