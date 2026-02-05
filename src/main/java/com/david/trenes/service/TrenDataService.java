package com.david.trenes.service;

import com.david.trenes.model.Tren;
import com.david.trenes.repository.TrenRepository;
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
    private final TrenRepository trenRepository;

    public void crearTrenesDeEjemplo() {
        log.info("Creando trenes de ejemplo para enriquecer el sistema");

        // Mantengo estos dos por si usas rutas CARGA/REGIONAL
        crearSiNoExiste(
                () -> crearTrenMercancias("MER-001", "MER-2024-001", "Mercancias", "Siemens Vectron", 2019, 0, 1200.0, 120),
                "MER-001", "MER-2024-001"
        );

        crearSiNoExiste(
                () -> crearTrenRegional("REG-001", "REG-2024-001", "Regional", "Stadler KISS", 2021, 180, 0.0, 160),
                "REG-001", "REG-2024-001"
        );

        // 6 trenes de ALTA_VELOCIDAD
        for (int i = 1; i <= 6; i++) {
            String numero = String.format("AVE-%03d", i);
            String matricula = String.format("AVE-2024-%03d", i);

            crearSiNoExiste(
                    () -> crearTrenAltaVelocidad(numero, matricula, "Alta Velocidad", "Renfe AVE", 2022, 300, 0.0, 350),
                    numero, matricula
            );
        }

        // 6 trenes TURISTICOS
        for (int i = 1; i <= 6; i++) {
            String numero = String.format("TUR-%03d", i);
            String matricula = String.format("TUR-2024-%03d", i);

            crearSiNoExiste(
                    () -> crearTrenTuristico(numero, matricula, "Turistico", "Talgo", 2018, 220, 0.0, 160),
                    numero, matricula
            );
        }

        // 2 trenes ESPECIALES
        for (int i = 1; i <= 2; i++) {
            String numero = String.format("ESP-%03d", i);
            String matricula = String.format("ESP-2024-%03d", i);

            crearSiNoExiste(
                    () -> crearTrenEspecial(numero, matricula, "Especial", "CAF", 2017, 180, 0.0, 140),
                    numero, matricula
            );
        }

        log.info("Trenes de ejemplo creados/verificados exitosamente");
    }

    private void crearSiNoExiste(Runnable creator, String numeroTren, String matricula) {
        boolean existe = trenRepository.existsByNumeroTren(numeroTren) || trenRepository.existsByMatricula(matricula);
        if (existe) {
            log.info("Saltando creación: ya existe un tren con numeroTren={} o matricula={}", numeroTren, matricula);
            return;
        }
        creator.run();
    }

    private void crearTrenTuristico(String numeroTren, String matricula, String modelo, String fabricante,
                                    Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga,
                                    Integer velocidadMaxima) {

        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(3500)
                .tipoCombustible("DIESEL")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(65000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();

        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V001")
                .tipo(Tren.TipoVagon.RESTAURANTE)
                .capacidad(40.0)
                .capacidadPasajeros(40)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón restaurante turístico")
                .build());

        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V002")
                .tipo(Tren.TipoVagon.CAFETERIA)
                .capacidad(25.0)
                .capacidadPasajeros(25)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Vagón cafetería turístico")
                .build());

        for (int i = 3; i <= 8; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_TURISTA)
                    .capacidad(55.0)
                    .capacidadPasajeros(55)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón turista")
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
                .numeroPuertas(14)
                .sistemaFrenos("NEUMÁTICO")
                .sistemaSenalizacion("ASFA")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.TURISTICO)
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
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(2))
                .proximaRevision(LocalDateTime.now().plusMonths(10))
                .kilometrajeTotal(65000.0)
                .kilometrosUltimaRevision(61000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren turístico creado: {}", numeroTren);
    }

    private void crearTrenEspecial(String numeroTren, String matricula, String modelo, String fabricante,
                                   Integer añoFabricacion, Integer capacidadPasajeros, Double capacidadCarga,
                                   Integer velocidadMaxima) {

        List<Tren.Locomotora> locomotoras = new ArrayList<>();
        locomotoras.add(Tren.Locomotora.builder()
                .id(UUID.randomUUID().toString())
                .matricula("LOC-" + matricula)
                .modelo(modelo + " Loco")
                .potencia(3000)
                .tipoCombustible("DIESEL")
                .añoFabricacion(añoFabricacion)
                .estado(Tren.EstadoLocomotora.OPERATIVA)
                .kilometraje(90000.0)
                .build());

        List<Tren.Vagon> vagones = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_PRIMERA)
                    .capacidad(35.0)
                    .capacidadPasajeros(35)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón primera - tren especial")
                    .build());
        }

        for (int i = 3; i <= 7; i++) {
            vagones.add(Tren.Vagon.builder()
                    .id(UUID.randomUUID().toString())
                    .numero(String.format("V%03d", i))
                    .tipo(Tren.TipoVagon.PASAJEROS_TURISTA)
                    .capacidad(55.0)
                    .capacidadPasajeros(55)
                    .capacidadCarga(0.0)
                    .activo(true)
                    .observaciones("Vagón turista - tren especial")
                    .build());
        }

        vagones.add(Tren.Vagon.builder()
                .id(UUID.randomUUID().toString())
                .numero("V008")
                .tipo(Tren.TipoVagon.CAFETERIA)
                .capacidad(20.0)
                .capacidadPasajeros(20)
                .capacidadCarga(0.0)
                .activo(true)
                .observaciones("Cafetería - tren especial")
                .build());

        Tren.CaracteristicasTren caracteristicas = Tren.CaracteristicasTren.builder()
                .aireAcondicionado(true)
                .wifi(true)
                .accesibilidad(true)
                .restaurante(false)
                .cafeteria(true)
                .banos(true)
                .sistemaAudio(true)
                .pantallas(true)
                .numeroPuertas(12)
                .sistemaFrenos("NEUMÁTICO")
                .sistemaSenalizacion("ASFA")
                .build();

        Tren tren = Tren.builder()
                .id(UUID.randomUUID().toString())
                .numeroTren(numeroTren)
                .matricula(matricula)
                .tipoTren(Tren.TipoTren.ESPECIAL)
                .modelo(modelo)
                .fabricante(fabricante)
                .añoFabricacion(añoFabricacion)
                .capacidadPasajeros(capacidadPasajeros)
                .capacidadCarga(capacidadCarga)
                .velocidadMaxima(velocidadMaxima)
                .locomotoras(locomotoras)
                .vagones(vagones)
                .estadoActual(Tren.EstadoTren.DETENIDO)
                .velocidadCruceroKmh(110.0)
                .fechaUltimaRevision(LocalDateTime.now().minusMonths(3))
                .proximaRevision(LocalDateTime.now().plusMonths(9))
                .kilometrajeTotal(90000.0)
                .kilometrosUltimaRevision(86000.0)
                .mantenimientos(new ArrayList<>())
                .incidencias(new ArrayList<>())
                .caracteristicas(caracteristicas)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        trenService.save(tren);
        log.info("Tren especial creado: {}", numeroTren);
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
