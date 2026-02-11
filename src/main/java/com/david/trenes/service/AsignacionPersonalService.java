package com.david.trenes.service;

import com.david.trenes.model.Personal;
import com.david.trenes.model.Tren;
import com.david.trenes.repository.PersonalRepository;
import com.david.trenes.repository.TrenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AsignacionPersonalService {

    private final PersonalRepository personalRepository;
    private final TrenRepository trenRepository;
    private final Random random = new Random();

    /**
     * Asigna personal automáticamente a todos los trenes activos
     */
    public void asignarPersonalATodosLosTrenes() {
        List<Tren> trenes = trenRepository.findByActivo(true);
        List<Personal> conductores = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.CONDUCTOR, true);
        List<Personal> personalCabina = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.CABINA, true);
        List<Personal> personalRestaurante = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.RESTAURANTE, true);

        log.info("Asignando personal a {} trenes", trenes.size());
        log.info("Disponibles: {} conductores, {} cabina, {} restaurante", 
                conductores.size(), personalCabina.size(), personalRestaurante.size());

        if (conductores.size() < trenes.size()) {
            throw new IllegalStateException("No hay suficientes conductores. Se necesitan " + trenes.size() + 
                    ", disponibles: " + conductores.size());
        }

        // Mezclar listas para distribución aleatoria
        Collections.shuffle(conductores);
        Collections.shuffle(personalCabina);
        Collections.shuffle(personalRestaurante);

        int conductorIndex = 0;
        int cabinaIndex = 0;
        int restauranteIndex = 0;

        for (Tren tren : trenes) {
            // Asignar conductor (1 por tren)
            if (conductorIndex < conductores.size()) {
                tren.setConductorActualId(conductores.get(conductorIndex).getId());
                conductorIndex++;
            }

            // Asignar personal de cabina solo a trenes de pasajeros, no de carga
            if (cabinaIndex < personalCabina.size() && !esTrenDeCarga(tren)) {
                tren.setPersonalCabinaIds(List.of(personalCabina.get(cabinaIndex).getId()));
                cabinaIndex++;
            } else {
                tren.setPersonalCabinaIds(new ArrayList<>());
            }

            // Asignar personal de restaurante solo a trenes que tengan vagones de restaurante/cafetería
            if (restauranteIndex < personalRestaurante.size() && tieneVagonRestauranteOCafeteria(tren)) {
                tren.setPersonalRestauranteIds(List.of(personalRestaurante.get(restauranteIndex).getId()));
                restauranteIndex++;
            } else {
                tren.setPersonalRestauranteIds(new ArrayList<>());
            }

            tren.setFechaActualizacion(LocalDateTime.now());
        }

        trenRepository.saveAll(trenes);
        log.info("Personal asignado exitosamente a {} trenes", trenes.size());
    }

    /**
     * Asigna personal a un tren específico
     */
    public Tren asignarPersonalATren(String trenId) {
        Tren tren = trenRepository.findById(trenId)
                .orElseThrow(() -> new RuntimeException("Tren no encontrado: " + trenId));

        // Obtener personal disponible
        List<Personal> conductores = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.CONDUCTOR, true);
        List<Personal> personalCabina = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.CABINA, true);
        List<Personal> personalRestaurante = personalRepository.findByTipoPersonalAndActivo(Personal.TipoPersonal.RESTAURANTE, true);

        if (conductores.isEmpty()) {
            throw new IllegalStateException("No hay conductores disponibles");
        }

        // Asignar conductor aleatorio
        Personal conductor = conductores.get(random.nextInt(conductores.size()));
        tren.setConductorActualId(conductor.getId());

        // Asignar personal de cabina solo a trenes de pasajeros, no de carga
        if (!personalCabina.isEmpty() && !esTrenDeCarga(tren)) {
            Personal cabina = personalCabina.get(random.nextInt(personalCabina.size()));
            tren.setPersonalCabinaIds(List.of(cabina.getId()));
        } else {
            tren.setPersonalCabinaIds(new ArrayList<>());
        }

        // Asignar personal de restaurante solo a trenes que tengan vagones de restaurante/cafetería
        if (!personalRestaurante.isEmpty() && tieneVagonRestauranteOCafeteria(tren)) {
            Personal restaurante = personalRestaurante.get(random.nextInt(personalRestaurante.size()));
            tren.setPersonalRestauranteIds(List.of(restaurante.getId()));
        } else {
            tren.setPersonalRestauranteIds(new ArrayList<>());
        }

        tren.setFechaActualizacion(LocalDateTime.now());
        return trenRepository.save(tren);
    }

    /**
     * Reasigna personal aleatoriamente para rotar turnos
     */
    public void reasignarPersonal() {
        log.info("Reasignando personal para rotación de turnos");
        
        // Limpiar asignaciones actuales
        List<Tren> trenes = trenRepository.findByActivo(true);
        trenes.forEach(tren -> {
            tren.setConductorActualId(null);
            tren.setPersonalCabinaIds(new ArrayList<>());
            tren.setPersonalRestauranteIds(new ArrayList<>());
            tren.setFechaActualizacion(LocalDateTime.now());
        });
        
        // Volver a asignar
        asignarPersonalATodosLosTrenes();
    }

    /**
     * Obtiene el personal asignado a un tren
     */
    public PersonalAsignadoResponse obtenerPersonalAsignado(String trenId) {
        Tren tren = trenRepository.findById(trenId)
                .orElseThrow(() -> new RuntimeException("Tren no encontrado: " + trenId));

        PersonalAsignadoResponse response = new PersonalAsignadoResponse();
        response.setTrenId(trenId);
        response.setNumeroTren(tren.getNumeroTren());

        // Obtener conductor
        if (tren.getConductorActualId() != null) {
            personalRepository.findById(tren.getConductorActualId())
                    .ifPresent(response::setConductor);
        }

        // Obtener personal de cabina
        if (tren.getPersonalCabinaIds() != null && !tren.getPersonalCabinaIds().isEmpty()) {
            List<Personal> cabina = personalRepository.findAllById(tren.getPersonalCabinaIds());
            response.setPersonalCabina(cabina);
        }

        // Obtener personal de restaurante
        if (tren.getPersonalRestauranteIds() != null && !tren.getPersonalRestauranteIds().isEmpty()) {
            List<Personal> restaurante = personalRepository.findAllById(tren.getPersonalRestauranteIds());
            response.setPersonalRestaurante(restaurante);
        }

        return response;
    }

    /**
     * Verifica si un tren es de carga
     */
    private boolean esTrenDeCarga(Tren tren) {
        return tren.getTipoTren() == Tren.TipoTren.CARGA || 
               (tren.getModelo() != null && tren.getModelo().toLowerCase().contains("mercanc"));
    }

    /**
     * Verifica si un tren tiene vagones de restaurante o cafetería
     */
    private boolean tieneVagonRestauranteOCafeteria(Tren tren) {
        if (tren.getVagones() == null || tren.getVagones().isEmpty()) {
            return false;
        }
        
        return tren.getVagones().stream()
                .anyMatch(vagon -> vagon.getTipo() == Tren.TipoVagon.RESTAURANTE || 
                                   vagon.getTipo() == Tren.TipoVagon.CAFETERIA);
    }

    /**
     * DTO para respuesta de personal asignado
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PersonalAsignadoResponse {
        private String trenId;
        private String numeroTren;
        private Personal conductor;
        private List<Personal> personalCabina;
        private List<Personal> personalRestaurante;
    }
}
