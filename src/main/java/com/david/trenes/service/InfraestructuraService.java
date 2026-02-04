package com.david.trenes.service;

import com.david.trenes.model.Via;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfraestructuraService {

    private final ViaService viaService;

    public void crearInfraestructuraDeEjemplo() {
        log.info("Creando infraestructura de ejemplo (desvíos y vías muertas)");
        
        // Crear desvíos
        crearDesvios();
        
        // Crear vías muertas
        crearViasMuertas();
        
        log.info("Infraestructura de ejemplo creada exitosamente");
    }

    private void crearDesvios() {
        List<Via> desvios = new ArrayList<>();
        
        // Desvío 1: Conexión entre vías principales
        Via desvio1 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("DSV-001")
                .nombre("Desvío Estación Norte - Vía 1 a Vía 3")
                .descripcion("Desvío para cambio de vía en estación Norte")
                .longitudKm(0.1)
                .tipoVia(Via.TipoVia.DESVIO)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4168)
                        .longitud(-3.7038)
                        .altitud(650.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4170)
                        .longitud(-3.7040)
                        .altitud(651.0)
                        .build())
                .estacionOrigenId("EST-NORTE-001")
                .estacionDestinoId("EST-NORTE-001")
                .velocidadMaxima(30)
                .anchoVia(1.668)
                .electrificada(true)
                .voltaje(3000)
                .fechaConstruccion(LocalDateTime.now().minusYears(10))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(2))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(10))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Desvío 2: Entrada a taller de mantenimiento
        Via desvio2 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("DSV-002")
                .nombre("Desvío Acceso Taller Mantenimiento")
                .descripcion("Desvío para acceso a instalaciones de mantenimiento")
                .longitudKm(0.15)
                .tipoVia(Via.TipoVia.DESVIO)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4180)
                        .longitud(-3.7050)
                        .altitud(648.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4185)
                        .longitud(-3.7055)
                        .altitud(649.0)
                        .build())
                .estacionOrigenId("EST-NORTE-001")
                .estacionDestinoId("TALLER-MANT-001")
                .velocidadMaxima(20)
                .anchoVia(1.668)
                .electrificada(false)
                .voltaje(0)
                .fechaConstruccion(LocalDateTime.now().minusYears(8))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(1))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(11))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Desvío 3: Bypass para trenes de mercancías
        Via desvio3 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("DSV-003")
                .nombre("Desvío Bypass Mercancías")
                .descripcion("Desvío para trenes de mercancías evitando estación principal")
                .longitudKm(0.8)
                .tipoVia(Via.TipoVia.DESVIO)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4150)
                        .longitud(-3.7020)
                        .altitud(652.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4160)
                        .longitud(-3.7010)
                        .altitud(653.0)
                        .build())
                .estacionOrigenId("EST-SUR-001")
                .estacionDestinoId("EST-SUR-001")
                .velocidadMaxima(60)
                .anchoVia(1.668)
                .electrificada(true)
                .voltaje(3000)
                .fechaConstruccion(LocalDateTime.now().minusYears(5))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(3))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(9))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        desvios.add(desvio1);
        desvios.add(desvio2);
        desvios.add(desvio3);
        
        desvios.forEach(viaService::save);
        log.info("Desvíos creados: {}", desvios.size());
    }

    private void crearViasMuertas() {
        List<Via> viasMuertas = new ArrayList<>();
        
        // Vía muerta 1: Almacenamiento temporal
        Via viaMuerta1 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("VMT-001")
                .nombre("Vía Muerta Almacenamiento Temporal")
                .descripcion("Vía sin salida para almacenamiento temporal de trenes")
                .longitudKm(0.5)
                .tipoVia(Via.TipoVia.VIA_MUERTA)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4190)
                        .longitud(-3.7060)
                        .altitud(647.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4195)
                        .longitud(-3.7065)
                        .altitud(647.0)
                        .build())
                .estacionOrigenId("EST-NORTE-001")
                .estacionDestinoId(null)
                .velocidadMaxima(15)
                .anchoVia(1.668)
                .electrificada(false)
                .voltaje(0)
                .fechaConstruccion(LocalDateTime.now().minusYears(12))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(4))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(8))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Vía muerta 2: Área de desguace
        Via viaMuerta2 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("VMT-002")
                .nombre("Vía Muerta Área Desguace")
                .descripcion("Vía sin salida para trenes fuera de servicio")
                .longitudKm(0.3)
                .tipoVia(Via.TipoVia.VIA_MUERTA)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4200)
                        .longitud(-3.7070)
                        .altitud(646.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4203)
                        .longitud(-3.7073)
                        .altitud(646.0)
                        .build())
                .estacionOrigenId("TALLER-MANT-001")
                .estacionDestinoId(null)
                .velocidadMaxima(10)
                .anchoVia(1.668)
                .electrificada(false)
                .voltaje(0)
                .fechaConstruccion(LocalDateTime.now().minusYears(15))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(6))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(6))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Vía muerta 3: Estacionamiento nocturno
        Via viaMuerta3 = Via.builder()
                .id(UUID.randomUUID().toString())
                .codigoVia("VMT-003")
                .nombre("Vía Muerta Estacionamiento Nocturno")
                .descripcion("Vía sin salida para estacionamiento nocturno de trenes")
                .longitudKm(0.6)
                .tipoVia(Via.TipoVia.VIA_MUERTA)
                .estado(Via.EstadoVia.OPERATIVA)
                .coordenadaInicio(Via.Coordenada.builder()
                        .latitud(40.4140)
                        .longitud(-3.7010)
                        .altitud(654.0)
                        .build())
                .coordenadaFin(Via.Coordenada.builder()
                        .latitud(40.4146)
                        .longitud(-3.7016)
                        .altitud(654.0)
                        .build())
                .estacionOrigenId("EST-SUR-001")
                .estacionDestinoId(null)
                .velocidadMaxima(20)
                .anchoVia(1.668)
                .electrificada(true)
                .voltaje(3000)
                .fechaConstruccion(LocalDateTime.now().minusYears(7))
                .fechaUltimaMantenimiento(LocalDateTime.now().minusMonths(1))
                .proximaMantenimiento(LocalDateTime.now().plusMonths(11))
                .segmentos(new ArrayList<>())
                .señalIds(new ArrayList<>())
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        viasMuertas.add(viaMuerta1);
        viasMuertas.add(viaMuerta2);
        viasMuertas.add(viaMuerta3);
        
        viasMuertas.forEach(viaService::save);
        log.info("Vías muertas creadas: {}", viasMuertas.size());
    }

    public List<Via> obtenerDesvios() {
        return viaService.findByTipoVia(Via.TipoVia.DESVIO);
    }

    public List<Via> obtenerViasMuertas() {
        return viaService.findByTipoVia(Via.TipoVia.VIA_MUERTA);
    }

    public boolean cambiarEstadoDesvio(String viaId, Via.EstadoVia nuevoEstado) {
        try {
            Via desvio = viaService.findById(viaId).orElse(null);
            if (desvio != null && desvio.getTipoVia() == Via.TipoVia.DESVIO) {
                desvio.setEstado(nuevoEstado);
                desvio.setFechaActualizacion(LocalDateTime.now());
                viaService.save(desvio);
                log.info("Estado del desvío {} cambiado a: {}", viaId, nuevoEstado);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error al cambiar estado del desvío: {}", viaId, e);
            return false;
        }
    }

    public boolean asignarTrenAViaMuerta(String trenId, String viaMuertaId) {
        try {
            Via viaMuerta = viaService.findById(viaMuertaId).orElse(null);
            if (viaMuerta != null && viaMuerta.getTipoVia() == Via.TipoVia.VIA_MUERTA) {
                // Aquí se podría implementar la lógica para asignar un tren a la vía muerta
                // Por ahora solo registramos la operación
                log.info("Tren {} asignado a vía muerta: {}", trenId, viaMuertaId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error al asignar tren a vía muerta: {}", viaMuertaId, e);
            return false;
        }
    }
}
