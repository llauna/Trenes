package com.david.trenes.service;

import com.david.trenes.model.Tren;
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
public class TrenDataService {

    private final TrenService trenService;

    public void crearTrenesDeEjemplo() {
        log.info("Creando trenes de ejemplo para enriquecer el sistema");
        
        // Crear tren de pasajeros con 8 vagones incluyendo restaurante
        crearTrenPasajeros("EXP-001", "PAS-2024-001", "Alvia", "Talgo", 2020, 250, 0.0, 280);
        
        // Crear tren de mercancías con 8 vagones
        crearTrenMercancias("MER-001", "MER-2024-001", "Mercancías", "Siemens Vectron", 2019, 0, 1200.0, 120);
        
        // Crear tren regional con 8 vagones
        crearTrenRegional("REG-001", "REG-2024-001", "Regional", "Stadler KISS", 2021, 180, 0.0, 160);
        
        // Crear tren de alta velocidad con 10 vagones
        crearTrenAltaVelocidad("AVE-001", "AVE-2024-001", "Alta Velocidad", "Renfe AVE", 2022, 300, 0.0, 350);
        
        log.info("Trenes de ejemplo creados exitosamente");
    }

    private void crearTrenPasajeros(String numeroTren, String matricula, String modelo, String fabricante, 
                                  Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga, 
                                  Integer velocidadMaxima) {
        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(4000)
                .tipoCombustible("ELÉCTRICO")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(50000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();
        
        // 2 vagones de primera clase
        for (int i = 1; i <= 2; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_PRIMERA)
                    .capacidad(40.0)
                    .capacidadPasajeros(40)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón primera clase con asientos reclinables")
                    .build());
        }
        
        // 1 vagón restaurante
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V003")
                .tipo(Tren.TipoVagon.RESTAURANTE)
                .capacidad(30.0)
                .capacidadPasajeros(30)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón restaurante con servicio completo")
                .build());
        
        // 1 vagón cafetería
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V004")
                .tipo(Tren.TipoVagon.CAFETERIA)
                .capacidad(20.0)
                .capacidadPasajeros(20)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón cafetería con snacks y bebidas")
                .build());
        
        // 4 vagones de clase turista
        for (int i = 5; i <= 8; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_TURISTA)
                    .capacidad(60.0)
                    .capacidadPasajeros(60)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón clase turista")
                    .build());
        }

        Tren.CaracteristicasTren caracteristicas = Tren.CaracteristicasTren.builder()
                .aireAcondicionado(true)
                .wifi(true)
                .accesibilidad(true)
                .restaurante(true)
                .cafeteria(true)
                .banos(true)
                .sistemaAudio(true)
                .pantallas(true)
                .numeroPuertas(16)
                .sistemaFrenos("NEUMÁTICO ELÉCTRICO")
                .sistemaSenalizacion("ETCS N2")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.ALTA_VELOCIDAD)
                .modelo(modelo)
                .fabricante(fabricante)
                .añoFabricacion(añoFabricacion)
                .capacidadPasajeros(capacidadPasajeros)
                .capacidadCarga(capacidadCarga)
                .velocidadMaxima(velocidadMaxima)
                .locomotoras(locomotoras)
                .vagones(vagones)
                .estadoActual(Tren.EstadoTren.DETENIDO)
                .velocidadCruceroKmh(250.0)
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(3))
                .proximaRevision(LocalDateTime.now().plusMonths(9))
                .kilometrajeTotal(50000.0)
                .kilometrosUltimaRevision(45000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren de pasajeros creado: {}", numeroTren);
    }

    private void crearTrenMercancias(String numeroTren, String matricula, String modelo, String fabricante,
                                    Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga,
                                    Integer velocidadMaxima) {
        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(6000)
                .tipoCombustible("DIESEL")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(75000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();
        
        // 4 vagones de carga cerrada
        for (int i = 1; i <= 4; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("VC%03d", i))
                    .tipo(Tren.TipoVagon.CARGA_CERRADO)
                    .capacidad(150.0)
                    .capacidadPasajeros(0)
                    .capacidadCarga(150.0)
                    .activo(true)
                    .observaciones("Vagón cerrado para mercancías secas")
                    .build());
        }
        
        // 2 vagones cisterna
        for (int i = 5; i <= 6; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("VCI%03d", i))
                    .tipo(Tren.TipoVagon.CISTERNA)
                    .capacidad(200.0)
                    .capacidadPasajeros(0)
                    .capacidadCarga(200.0)
                    .activo(true)
                    .observaciones("Vagón cisterna para líquidos")
                    .build());
        }
        
        // 2 vagones de carga abierta
        for (int i = 7; i <= 8; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("VA%03d", i))
                    .tipo(Tren.TipoVagon.CARGA_ABIERTO)
                    .capacidad(100.0)
                    .capacidadPasajeros(0)
                    .capacidadCarga(100.0)
                    .activo(true)
                    .observaciones("Vagón abierto para contenedores")
                    .build());
        }

        Tren.CaracteristicasTren caracteristicas = Tren.CaracteristicasTren.builder()
                .aireAcondicionado(false)
                .wifi(false)
                .accesibilidad(false)
                .restaurante(false)
                .cafeteria(false)
                .banos(false)
                .sistemaAudio(false)
                .pantallas(false)
                .numeroPuertas(4)
                .sistemaFrenos("NEUMÁTICO")
                .sistemaSenalizacion("ASFA")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.CARGA)
                .modelo(modelo)
                .fabricante(fabricante)
                .añoFabricacion(añoFabricacion)
                .capacidadPasajeros(capacidadPasajeros)
                .capacidadCarga(capacidadCarga)
                .velocidadMaxima(velocidadMaxima)
                .locomotoras(locomotoras)
                .vagones(vagones)
                .estadoActual(Tren.EstadoTren.DETENIDO)
                .velocidadCruceroKmh(100.0)
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(2))
                .proximaRevision(LocalDateTime.now().plusMonths(10))
                .kilometrajeTotal(75000.0)
                .kilometrosUltimaRevision(70000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren de mercancías creado: {}", numeroTren);
    }

    private void crearTrenRegional(String numeroTren, String matricula, String modelo, String fabricante,
                                  Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga,
                                  Integer velocidadMaxima) {
        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(2500)
                .tipoCombustible("DIESEL")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(35000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();
        
        // 1 vagón de primera clase
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V001")
                .tipo(Tren.TipoVagon.PASAJEROS_PRIMERA)
                .capacidad(30.0)
                .capacidadPasajeros(30)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón primera clase regional")
                .build());
        
        // 1 vagón cafetería
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V002")
                .tipo(Tren.TipoVagon.CAFETERIA)
                .capacidad(15.0)
                .capacidadPasajeros(15)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón cafetería regional")
                .build());
        
        // 6 vagones de clase turista
        for (int i = 3; i <= 8; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_TURISTA)
                    .capacidad(50.0)
                    .capacidadPasajeros(50)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón clase turista regional")
                    .build());
        }

        Tren.CaracteristicasTren caracteristicas = Tren.CaracteristicasTren.builder()
                .aireAcondicionado(true)
                .wifi(true)
                .accesibilidad(true)
                .restaurante(false)
                .cafeteria(true)
                .banos(true)
                .sistemaAudio(true)
                .pantallas(false)
                .numeroPuertas(12)
                .sistemaFrenos("NEUMÁTICO")
                .sistemaSenalizacion("ASFA")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.REGIONAL)
                .modelo(modelo)
                .fabricante(fabricante)
                .añoFabricacion(añoFabricacion)
                .capacidadPasajeros(capacidadPasajeros)
                .capacidadCarga(capacidadCarga)
                .velocidadMaxima(velocidadMaxima)
                .locomotoras(locomotoras)
                .vagones(vagones)
                .estadoActual(Tren.EstadoTren.DETENIDO)
                .velocidadCruceroKmh(120.0)
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(1))
                .proximaRevision(LocalDateTime.now().plusMonths(11))
                .kilometrajeTotal(35000.0)
                .kilometrosUltimaRevision(32000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren regional creado: {}", numeroTren);
    }

    private void crearTrenAltaVelocidad(String numeroTren, String matricula, String modelo, String fabricante,
                                       Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga,
                                       Integer velocidadMaxima) {
        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(8000)
                .tipoCombustible("ELÉCTRICO")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(25000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();
        
        // 3 vagones de primera clase
        for (int i = 1; i <= 3; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_PRIMERA)
                    .capacidad(35.0)
                    .capacidadPasajeros(35)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón primera clase alta velocidad")
                    .build());
        }
        
        // 1 vagón restaurante
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V004")
                .tipo(Tren.TipoVagon.RESTAURANTE)
                .capacidad(40.0)
                .capacidadPasajeros(40)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón restaurante alta velocidad")
                .build());
        
        // 1 vagón cafetería
        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V005")
                .tipo(Tren.TipoVagon.CAFETERIA)
                .capacidad(25.0)
                .capacidadPasajeros(25)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón cafetería alta velocidad")
                .build());
        
        // 5 vagones de clase turista
        for (int i = 6; i <= 10; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_TURISTA)
                    .capacidad(55.0)
                    .capacidadPasajeros(55)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón clase turista alta velocidad")
                    .build());
        }

        Tren.CaracteristicasTren caracteristicas = Tren.CaracteristicasTren.builder()
                .aireAcondicionado(true)
                .wifi(true)
                .accesibilidad(true)
                .restaurante(true)
                .cafeteria(true)
                .banos(true)
                .sistemaAudio(true)
                .pantallas(true)
                .numeroPuertas(20)
                .sistemaFrenos("ELÉCTRICO REGENERATIVO")
                .sistemaSenalizacion("ETCS N2")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.ALTA_VELOCIDAD)
                .modelo(modelo)
                .fabricante(fabricante)
                .añoFabricacion(añoFabricacion)
                .capacidadPasajeros(capacidadPasajeros)
                .capacidadCarga(capacidadCarga)
                .velocidadMaxima(velocidadMaxima)
                .locomotoras(locomotoras)
                .vagones(vagones)
                .estadoActual(Tren.EstadoTren.DETENIDO)
                .velocidadCruceroKmh(300.0)
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(4))
                .proximaRevision(LocalDateTime.now().plusMonths(8))
                .kilometrajeTotal(25000.0)
                .kilometrosUltimaRevision(22000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren de alta velocidad creado: {}", numeroTren);
    }
}
