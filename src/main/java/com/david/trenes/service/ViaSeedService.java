package com.david.trenes.service;

import com.david.trenes.model.Via;
import com.david.trenes.repository.ViaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ViaSeedService {

    private final ViaRepository viaRepository;

    /**
     * Genera vías de estación automáticamente
     */
    public List<Via> generarViasEstacion(int cantidad) {
        List<Via> nuevasVias = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (int i = 1; i <= cantidad; i++) {
            // Generar diferentes tipos de vías de estación
            Via.TipoVia[] tiposEstacion = {
                Via.TipoVia.ANDEN,
                Via.TipoVia.VIA_MUERTA,
                Via.TipoVia.DESVIO
            };
            
            Via.TipoVia tipo = tiposEstacion[(i - 1) % tiposEstacion.length];
            
            Via via = Via.builder()
                    .id(null)
                    .codigoVia(generarCodigoVia(tipo, i))
                    .nombre("Vía " + tipo.toString() + " " + i)
                    .descripcion("Vía de " + tipo.toString().toLowerCase() + " generada automáticamente")
                    .longitudKm(0.5 + (Math.random() * 2.0)) // Entre 0.5 y 2.5 km
                    .tipoVia(tipo)
                    .estado(Via.EstadoVia.OPERATIVA)
                    .coordenadaInicio(generarCoordenada())
                    .coordenadaFin(generarCoordenada())
                    .estacionOrigenId("EST-001") // Estación principal por defecto
                    .estacionDestinoId(null) // Vías de estación no tienen destino
                    .velocidadMaxima(30) // Velocidad reducida en estación
                    .anchoVia(1.435) // Ancho estándar europeo
                    .electrificada(true)
                    .voltaje(3000) // Voltaje común en España
                    .fechaConstruccion(ahora.minusYears((int)(Math.random() * 30) + 5))
                    .fechaUltimaMantenimiento(ahora.minusMonths((int)(Math.random() * 6) + 1))
                    .proximaMantenimiento(ahora.plusMonths((int)(Math.random() * 6) + 1))
                    .activo(true)
                    .fechaCreacion(ahora)
                    .fechaActualizacion(ahora)
                    .build();

            nuevasVias.add(via);
        }

        List<Via> guardadas = viaRepository.saveAll(nuevasVias);
        log.info("Se generaron {} vías de estación exitosamente", guardadas.size());
        
        return guardadas;
    }

    /**
     * Genera código de vía único
     */
    private String generarCodigoVia(Via.TipoVia tipo, int numero) {
        String prefijo = switch (tipo) {
            case ANDEN -> "AND";
            case VIA_MUERTA -> "VM";
            case DESVIO -> "DSV";
            default -> "VIA";
        };
        
        return String.format("%s-%03d", prefijo, numero);
    }

    /**
     * Genera coordenadas aleatorias alrededor de una ubicación base
     */
    private Via.Coordenada generarCoordenada() {
        // Coordenadas base alrededor de Madrid (puedes ajustar)
        double baseLat = 40.4168;
        double baseLon = -3.7038;
        
        // Variación pequeña para mantenerlas cerca
        double latVariation = (Math.random() - 0.5) * 0.01; // ±0.005 grados
        double lonVariation = (Math.random() - 0.5) * 0.01; // ±0.005 grados
        
        return Via.Coordenada.builder()
                .latitud(baseLat + latVariation)
                .longitud(baseLon + lonVariation)
                .altitud(600.0 + (Math.random() * 200)) // Entre 600-800m
                .build();
    }

    /**
     * Genera vías específicas para una estación
     */
    public List<Via> generarViasParaEstacion(String estacionId, int andenes, int viasMuertas, int desvios) {
        List<Via> vias = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        int contador = 1;

        // Generar andenes
        for (int i = 1; i <= andenes; i++) {
            Via via = crearViaEstacion(estacionId, Via.TipoVia.ANDEN, contador++);
            vias.add(via);
        }

        // Generar vías muertas
        for (int i = 1; i <= viasMuertas; i++) {
            Via via = crearViaEstacion(estacionId, Via.TipoVia.VIA_MUERTA, contador++);
            vias.add(via);
        }

        // Generar desvíos
        for (int i = 1; i <= desvios; i++) {
            Via via = crearViaEstacion(estacionId, Via.TipoVia.DESVIO, contador++);
            vias.add(via);
        }

        List<Via> guardadas = viaRepository.saveAll(vias);
        log.info("Se generaron {} vías para la estación {}: {} andenes, {} vías muertas, {} desvíos", 
                guardadas.size(), estacionId, andenes, viasMuertas, desvios);
        
        return guardadas;
    }

    /**
     * Crea una vía de estación específica
     */
    private Via crearViaEstacion(String estacionId, Via.TipoVia tipo, int numero) {
        LocalDateTime ahora = LocalDateTime.now();
        
        return Via.builder()
                .id(null)
                .codigoVia(generarCodigoVia(tipo, numero))
                .nombre("Vía " + tipo.toString() + " " + numero + " - Estación " + estacionId)
                .descripcion("Vía de " + tipo.toString().toLowerCase() + " para estación " + estacionId)
                .longitudKm(0.3 + (Math.random() * 1.5))
                .tipoVia(tipo)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(generarCoordenada())
                .coordenadaFin(generarCoordenada())
                .estacionOrigenId(estacionId)
                .estacionDestinoId(null)
                .velocidadMaxima(20 + (int)(Math.random() * 20)) // Entre 20-40 km/h
                .anchoVia(1.435)
                .electrificada(true)
                .voltaje(3000)
                .fechaConstruccion(ahora.minusYears((int)(Math.random() * 20) + 5))
                .fechaUltimaMantenimiento(ahora.minusMonths((int)(Math.random() * 3) + 1))
                .proximaMantenimiento(ahora.plusMonths((int)(Math.random() * 3) + 1))
                .activo(true)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build();
    }
}
