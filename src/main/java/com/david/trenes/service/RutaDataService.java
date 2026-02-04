package com.david.trenes.service;

import com.david.trenes.model.Ruta;
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
public class RutaDataService {

    private final RutaService rutaService;

    public void crearRutasDeEjemplo() {
        log.info("Creando rutas de ejemplo para enriquecer el sistema");
        
        // Crear ruta expreso Madrid-Barcelona
        crearRutaExpresoMadridBarcelona();
        
        // Crear ruta regional Valencia-Alicante
        crearRutaRegionalValenciaAlicante();
        
        // Crear ruta de mercancías Bilbao-Zaragoza
        crearRutaMercanciasBilbaoZaragoza();
        
        // Crear ruta turística Santiago de Compostela-A Coruña
        crearRutaTuristicaSantiagoCoruña();
        
        // Crear ruta de cercanías Madrid-Toledo
        crearRutaCercaniasMadridToledo();
        
        // Nuevas rutas adicionales
        crearRutaExpresoMadridSevilla();
        crearRutaCercaniasBarcelona();
        crearRutaCargaValenciaMurcia();
        crearRutaTuristicaRutaDonQuijote();
        crearRutaInternacionalMadridParis();
        crearRutaEspecialTrenNavidad();
        
        log.info("Rutas de ejemplo creadas exitosamente");
    }

    private void crearRutaExpresoMadridBarcelona() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .horaLlegada(null)
                .horaSalida(null)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ZARAGOZA-001")
                .nombreEstacion("Zaragoza-Delicias")
                .orden(2)
                .kilometro(306.0)
                .tiempoParadaMinutos(5)
                .obligatoria(false)
                .horaLlegada(null)
                .horaSalida(null)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BARCELONA-001")
                .nombreEstacion("Barcelona Sants")
                .orden(3)
                .kilometro(621.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .horaLlegada(null)
                .horaSalida(null)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-MAD-ZAR-001")
                .nombreVia("Línea de Alta Velocidad Madrid-Zaragoza")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(306.0)
                .distancia(306.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-ZARAGOZA-001")
                .build());
        
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-ZAR-BAR-001")
                .nombreVia("Línea de Alta Velocidad Zaragoza-Barcelona")
                .orden(2)
                .kilometroInicio(306.0)
                .kilometroFin(621.0)
                .distancia(315.0)
                .estacionOrigenId("EST-ZARAGOZA-001")
                .estacionDestinoId("EST-BARCELONA-001")
                .build());

        List<Ruta.RestriccionRuta> restricciones = new ArrayList<>();
        restricciones.add(Ruta.RestriccionRuta.builder()
                .tipo(Ruta.TipoRestriccion.VELOCIDAD_MAXIMA)
                .descripcion("Velocidad máxima en tramo urbano")
                .valor("300")
                .activa(true)
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(15)
                .horaPrimeraSalida("06:00")
                .horaUltimaSalida("22:00")
                .intervaloMinutos(60)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("EXP-MAD-BCN-001")
                .nombre("Expreso Madrid-Barcelona")
                .descripcion("Ruta de alta velocidad entre Madrid y Barcelona")
                .tipoRuta(Ruta.TipoRuta.EXPRESO)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-BARCELONA-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(621.0)
                .tiempoEstimadoMinutos(165)
                .velocidadPromedio(225.8)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(1)
                .restricciones(restricciones)
                .tarifaBase(85.50)
                .zonas(List.of("CENTRO", "NORESTE"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta expreso Madrid-Barcelona creada");
    }

    private void crearRutaRegionalValenciaAlicante() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-VALENCIA-001")
                .nombreEstacion("Valencia Nord")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-GANDIA-001")
                .nombreEstacion("Gandía")
                .orden(2)
                .kilometro(65.0)
                .tiempoParadaMinutos(2)
                .obligatoria(false)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-DENIA-001")
                .nombreEstacion("Denia")
                .orden(3)
                .kilometro(95.0)
                .tiempoParadaMinutos(2)
                .obligatoria(false)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ALICANTE-001")
                .nombreEstacion("Alicante Terminal")
                .orden(4)
                .kilometro(185.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-VAL-ALI-001")
                .nombreVia("Línea Valencia-Alicante")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(185.0)
                .distancia(185.0)
                .estacionOrigenId("EST-VALENCIA-001")
                .estacionDestinoId("EST-ALICANTE-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(false)
                .serviciosDia(8)
                .horaPrimeraSalida("06:30")
                .horaUltimaSalida("21:30")
                .intervaloMinutos(120)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("REG-VAL-ALI-001")
                .nombre("Regional Valencia-Alicante")
                .descripcion("Ruta regional conectando Valencia con Alicante")
                .tipoRuta(Ruta.TipoRuta.REGIONAL)
                .estacionOrigenId("EST-VALENCIA-001")
                .estacionDestinoId("EST-ALICANTE-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(185.0)
                .tiempoEstimadoMinutos(135)
                .velocidadPromedio(82.2)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(2)
                .restricciones(new ArrayList<>())
                .tarifaBase(12.75)
                .zonas(List.of("LEVANTE"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta regional Valencia-Alicante creada");
    }

    private void crearRutaMercanciasBilbaoZaragoza() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BILBAO-001")
                .nombreEstacion("Bilbao Abando")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-VITORIA-001")
                .nombreEstacion("Vitoria-Gasteiz")
                .orden(2)
                .kilometro(120.0)
                .tiempoParadaMinutos(15)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ZARAGOZA-001")
                .nombreEstacion("Zaragoza-Delicias")
                .orden(3)
                .kilometro(300.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-BIL-ZAR-001")
                .nombreVia("Línea de Mercancías Bilbao-Zaragoza")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(300.0)
                .distancia(300.0)
                .estacionOrigenId("EST-BILBAO-001")
                .estacionDestinoId("EST-ZARAGOZA-001")
                .build());

        List<Ruta.RestriccionRuta> restricciones = new ArrayList<>();
        restricciones.add(Ruta.RestriccionRuta.builder()
                .tipo(Ruta.TipoRestriccion.PESO_MAXIMO)
                .descripcion("Peso máximo por eje")
                .valor("22.5")
                .activa(true)
                .build());
        
        restricciones.add(Ruta.RestriccionRuta.builder()
                .tipo(Ruta.TipoRestriccion.TIPO_TREN)
                .descripcion("Solo trenes de mercancías")
                .valor("CARGA")
                .activa(true)
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(false)
                .domingo(false)
                .festivos(false)
                .serviciosDia(4)
                .horaPrimeraSalida("22:00")
                .horaUltimaSalida("04:00")
                .intervaloMinutos(240)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("CAR-BIL-ZAR-001")
                .nombre("Mercancías Bilbao-Zaragoza")
                .descripcion("Ruta exclusiva para trenes de mercancías")
                .tipoRuta(Ruta.TipoRuta.CARGA)
                .estacionOrigenId("EST-BILBAO-001")
                .estacionDestinoId("EST-ZARAGOZA-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(300.0)
                .tiempoEstimadoMinutos(240)
                .velocidadPromedio(75.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(3)
                .restricciones(restricciones)
                .tarifaBase(0.0) // Las rutas de mercancías no tienen tarifa de pasajeros
                .zonas(List.of("NORTE", "ARAGON"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta de mercancías Bilbao-Zaragoza creada");
    }

    private void crearRutaTuristicaSantiagoCoruña() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-SANTIAGO-001")
                .nombreEstacion("Santiago de Compostela")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ORDES-001")
                .nombreEstacion("Ordes")
                .orden(2)
                .kilometro(25.0)
                .tiempoParadaMinutos(5)
                .obligatoria(false)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BETANZOS-001")
                .nombreEstacion("Betanzos")
                .orden(3)
                .kilometro(45.0)
                .tiempoParadaMinutos(5)
                .obligatoria(false)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-CORUÑA-001")
                .nombreEstacion("A Coruña")
                .orden(4)
                .kilometro(75.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-SAN-COR-001")
                .nombreVia("Línea Turística Santiago-A Coruña")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(75.0)
                .distancia(75.0)
                .estacionOrigenId("EST-SANTIAGO-001")
                .estacionDestinoId("EST-CORUÑA-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(6)
                .horaPrimeraSalida("08:00")
                .horaUltimaSalida("20:00")
                .intervaloMinutos(180)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("TUR-SAN-COR-001")
                .nombre("Turística Santiago-A Coruña")
                .descripcion("Ruta turística con vistas panorámicas")
                .tipoRuta(Ruta.TipoRuta.TURISTICO)
                .estacionOrigenId("EST-SANTIAGO-001")
                .estacionDestinoId("EST-CORUÑA-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(75.0)
                .tiempoEstimadoMinutos(60)
                .velocidadPromedio(75.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(4)
                .restricciones(new ArrayList<>())
                .tarifaBase(8.50)
                .zonas(List.of("GALICIA"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta turística Santiago-A Coruña creada");
    }

    private void crearRutaCercaniasMadridToledo() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ARANJUEZ-001")
                .nombreEstacion("Aranjuez")
                .orden(2)
                .kilometro(47.0)
                .tiempoParadaMinutos(2)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-TOLEDO-001")
                .nombreEstacion("Toledo")
                .orden(3)
                .kilometro(74.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-MAD-TOL-001")
                .nombreVia("Línea de Cercanías Madrid-Toledo")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(74.0)
                .distancia(74.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-TOLEDO-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(20)
                .horaPrimeraSalida("05:30")
                .horaUltimaSalida("23:30")
                .intervaloMinutos(60)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("CER-MAD-TOL-001")
                .nombre("Cercanías Madrid-Toledo")
                .descripcion("Línea de cercanías conectando Madrid con Toledo")
                .tipoRuta(Ruta.TipoRuta.CERCANIAS)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-TOLEDO-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(74.0)
                .tiempoEstimadoMinutos(30)
                .velocidadPromedio(148.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(2)
                .restricciones(new ArrayList<>())
                .tarifaBase(5.25)
                .zonas(List.of("CENTRO"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta de cercanías Madrid-Toledo creada");
    }

    private void crearRutaExpresoMadridSevilla() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-CIUDADREAL-001")
                .nombreEstacion("Ciudad Real")
                .orden(2)
                .kilometro(170.0)
                .tiempoParadaMinutos(3)
                .obligatoria(false)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-CORDOBA-001")
                .nombreEstacion("Córdoba")
                .orden(3)
                .kilometro(340.0)
                .tiempoParadaMinutos(5)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-SEVILLA-001")
                .nombreEstacion("Sevilla Santa Justa")
                .orden(4)
                .kilometro(470.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-MAD-SEV-001")
                .nombreVia("Línea de Alta Velocidad Madrid-Sevilla")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(470.0)
                .distancia(470.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-SEVILLA-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(12)
                .horaPrimeraSalida("06:30")
                .horaUltimaSalida("21:30")
                .intervaloMinutos(90)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("EXP-MAD-SEV-001")
                .nombre("Expreso Madrid-Sevilla")
                .descripcion("Ruta de alta velocidad AVE Madrid-Sevilla")
                .tipoRuta(Ruta.TipoRuta.EXPRESO)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-SEVILLA-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(470.0)
                .tiempoEstimadoMinutos(150)
                .velocidadPromedio(188.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(1)
                .restricciones(new ArrayList<>())
                .tarifaBase(75.30)
                .zonas(List.of("CENTRO", "SUR"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta expreso Madrid-Sevilla creada");
    }

    private void crearRutaCercaniasBarcelona() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BARCELONA-001")
                .nombreEstacion("Barcelona Sants")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BADALONA-001")
                .nombreEstacion("Badalona")
                .orden(2)
                .kilometro(12.0)
                .tiempoParadaMinutos(2)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MATARO-001")
                .nombreEstacion("Mataró")
                .orden(3)
                .kilometro(30.0)
                .tiempoParadaMinutos(2)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BLANES-001")
                .nombreEstacion("Blanes")
                .orden(4)
                .kilometro(65.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-BAR-BLA-001")
                .nombreVia("Línea de Cercanías Barcelona-Blanes")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(65.0)
                .distancia(65.0)
                .estacionOrigenId("EST-BARCELONA-001")
                .estacionDestinoId("EST-BLANES-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(25)
                .horaPrimeraSalida("05:00")
                .horaUltimaSalida("23:30")
                .intervaloMinutos(45)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("CER-BAR-BLA-001")
                .nombre("Cercanías Barcelona-Blanes")
                .descripcion("Línea R1 de cercanías Barcelona")
                .tipoRuta(Ruta.TipoRuta.CERCANIAS)
                .estacionOrigenId("EST-BARCELONA-001")
                .estacionDestinoId("EST-BLANES-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(65.0)
                .tiempoEstimadoMinutos(55)
                .velocidadPromedio(70.9)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(2)
                .restricciones(new ArrayList<>())
                .tarifaBase(4.25)
                .zonas(List.of("CATALUÑA"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta de cercanías Barcelona-Blanes creada");
    }

    private void crearRutaCargaValenciaMurcia() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-VALENCIA-001")
                .nombreEstacion("Valencia Fuente San Luis")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ALICANTE-001")
                .nombreEstacion("Alicante Mercancías")
                .orden(2)
                .kilometro(185.0)
                .tiempoParadaMinutos(20)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MURCIA-001")
                .nombreEstacion("Murcia Mercancías")
                .orden(3)
                .kilometro(280.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-VAL-MUR-001")
                .nombreVia("Línea de Mercancías Valencia-Murcia")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(280.0)
                .distancia(280.0)
                .estacionOrigenId("EST-VALENCIA-001")
                .estacionDestinoId("EST-MURCIA-001")
                .build());

        List<Ruta.RestriccionRuta> restricciones = new ArrayList<>();
        restricciones.add(Ruta.RestriccionRuta.builder()
                .tipo(Ruta.TipoRestriccion.TIPO_TREN)
                .descripcion("Solo trenes de mercancías")
                .valor("CARGA")
                .activa(true)
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(false)
                .domingo(false)
                .festivos(false)
                .serviciosDia(6)
                .horaPrimeraSalida("22:00")
                .horaUltimaSalida("04:00")
                .intervaloMinutos(120)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("CAR-VAL-MUR-001")
                .nombre("Mercancías Valencia-Murcia")
                .descripcion("Ruta exclusiva para trenes de mercancías Levante-Sur")
                .tipoRuta(Ruta.TipoRuta.CARGA)
                .estacionOrigenId("EST-VALENCIA-001")
                .estacionDestinoId("EST-MURCIA-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(280.0)
                .tiempoEstimadoMinutos(210)
                .velocidadPromedio(80.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(3)
                .restricciones(restricciones)
                .tarifaBase(0.0)
                .zonas(List.of("LEVANTE", "MURCIA"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta de mercancías Valencia-Murcia creada");
    }

    private void crearRutaTuristicaRutaDonQuijote() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ALCALA-001")
                .nombreEstacion("Alcalá de Henares")
                .orden(2)
                .kilometro(35.0)
                .tiempoParadaMinutos(15)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-CONSUEGRA-001")
                .nombreEstacion("Consuegra")
                .orden(3)
                .kilometro(120.0)
                .tiempoParadaMinutos(20)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-CIUDADREAL-001")
                .nombreEstacion("Ciudad Real")
                .orden(4)
                .kilometro(170.0)
                .tiempoParadaMinutos(15)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-ALMAGRO-001")
                .nombreEstacion("Almagro")
                .orden(5)
                .kilometro(200.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-DON-QUIJOTE-001")
                .nombreVia("Ruta Turística Don Quijote")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(200.0)
                .distancia(200.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-ALMAGRO-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(false)
                .martes(true)
                .miercoles(false)
                .jueves(true)
                .viernes(false)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(2)
                .horaPrimeraSalida("09:00")
                .horaUltimaSalida("16:00")
                .intervaloMinutos(420)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("TUR-DON-QUIJOTE-001")
                .nombre("Turística Ruta de Don Quijote")
                .descripcion("Recorrido turístico por los lugares de Don Quijote")
                .tipoRuta(Ruta.TipoRuta.TURISTICO)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-ALMAGRO-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(200.0)
                .tiempoEstimadoMinutos(180)
                .velocidadPromedio(66.7)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(4)
                .restricciones(new ArrayList<>())
                .tarifaBase(35.50)
                .zonas(List.of("CASTILLA-LA MANCHA"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta turística Don Quijote creada");
    }

    private void crearRutaInternacionalMadridParis() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BARCELONA-001")
                .nombreEstacion("Barcelona Sants")
                .orden(2)
                .kilometro(621.0)
                .tiempoParadaMinutos(30)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-PARIS-001")
                .nombreEstacion("París Gare de Lyon")
                .orden(3)
                .kilometro(1260.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-MAD-PAR-001")
                .nombreVia("Línea Internacional Madrid-París")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(1260.0)
                .distancia(1260.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-PARIS-001")
                .build());

        List<Ruta.RestriccionRuta> restricciones = new ArrayList<>();
        restricciones.add(Ruta.RestriccionRuta.builder()
                .tipo(Ruta.TipoRestriccion.SEGURIDAD)
                .descripcion("Control fronterizo y aduanas")
                .valor("INTERNACIONAL")
                .activa(true)
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(true)
                .martes(true)
                .miercoles(true)
                .jueves(true)
                .viernes(true)
                .sabado(true)
                .domingo(true)
                .festivos(true)
                .serviciosDia(3)
                .horaPrimeraSalida("07:00")
                .horaUltimaSalida("19:00")
                .intervaloMinutos(360)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("INT-MAD-PAR-001")
                .nombre("Internacional Madrid-París")
                .descripcion("Tren internacional de alta velocidad Madrid-París")
                .tipoRuta(Ruta.TipoRuta.INTERNACIONAL)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-PARIS-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(1260.0)
                .tiempoEstimadoMinutos(390)
                .velocidadPromedio(193.8)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(1)
                .restricciones(restricciones)
                .tarifaBase(180.75)
                .zonas(List.of("INTERNACIONAL"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta internacional Madrid-París creada");
    }

    private void crearRutaEspecialTrenNavidad() {
        List<Ruta.ParadaRuta> paradas = new ArrayList<>();
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-MADRID-001")
                .nombreEstacion("Madrid Atocha")
                .orden(1)
                .kilometro(0.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-SEGOVIA-001")
                .nombreEstacion("Segovia")
                .orden(2)
                .kilometro(90.0)
                .tiempoParadaMinutos(45)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-VALLADOLID-001")
                .nombreEstacion("Valladolid")
                .orden(3)
                .kilometro(200.0)
                .tiempoParadaMinutos(30)
                .obligatoria(true)
                .build());
        
        paradas.add(Ruta.ParadaRuta.builder()
                .estacionId("EST-BURGOS-001")
                .nombreEstacion("Burgos")
                .orden(4)
                .kilometro(280.0)
                .tiempoParadaMinutos(0)
                .obligatoria(true)
                .build());

        List<Ruta.ViaRuta> vias = new ArrayList<>();
        vias.add(Ruta.ViaRuta.builder()
                .viaId("VIA-NAVIDAD-001")
                .nombreVia("Tren Especial de Navidad")
                .orden(1)
                .kilometroInicio(0.0)
                .kilometroFin(280.0)
                .distancia(280.0)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-BURGOS-001")
                .build());

        Ruta.FrecuenciaRuta frecuencia = Ruta.FrecuenciaRuta.builder()
                .lunes(false)
                .martes(false)
                .miercoles(false)
                .jueves(false)
                .viernes(false)
                .sabado(false)
                .domingo(true)
                .festivos(true)
                .serviciosDia(1)
                .horaPrimeraSalida("10:00")
                .horaUltimaSalida("10:00")
                .intervaloMinutos(0)
                .build();

        Ruta ruta = Ruta.builder()
                .id(UUID.randomUUID().toString())
                .codigoRuta("ESP-NAVIDAD-001")
                .nombre("Tren Especial de Navidad")
                .descripcion("Tren temático navideño con actividades especiales")
                .tipoRuta(Ruta.TipoRuta.ESPECIAL)
                .estacionOrigenId("EST-MADRID-001")
                .estacionDestinoId("EST-BURGOS-001")
                .estacionesIntermedias(paradas)
                .vias(vias)
                .distanciaTotalKm(280.0)
                .tiempoEstimadoMinutos(240)
                .velocidadPromedio(70.0)
                .estado(Ruta.EstadoRuta.ACTIVA)
                .frecuencia(frecuencia)
                .prioridad(5)
                .restricciones(new ArrayList<>())
                .tarifaBase(45.00)
                .zonas(List.of("CASTILLA-LEÓN"))
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        rutaService.save(ruta);
        log.info("Ruta especial Tren de Navidad creada");
    }
}
