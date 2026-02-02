package com.david.trenes.service;

import com.david.trenes.model.Horario;
import com.david.trenes.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HorarioService {
    
    private final HorarioRepository horarioRepository;
    
    public List<Horario> findAll() {
        log.debug("Buscando todos los horarios");
        return horarioRepository.findAll();
    }
    
    public Optional<Horario> findById(String id) {
        log.debug("Buscando horario por ID: {}", id);
        return horarioRepository.findById(id);
    }
    
    public Optional<Horario> findByCodigoServicio(String codigoServicio) {
        log.debug("Buscando horario por código de servicio: {}", codigoServicio);
        return horarioRepository.findByCodigoServicio(codigoServicio);
    }
    
    public List<Horario> findByNumeroServicio(String numeroServicio) {
        log.debug("Buscando horarios por número de servicio: {}", numeroServicio);
        return horarioRepository.findByNumeroServicio(numeroServicio);
    }
    
    public List<Horario> findByTrenId(String trenId) {
        log.debug("Buscando horarios del tren: {}", trenId);
        return horarioRepository.findByTrenId(trenId);
    }
    
    public List<Horario> findByRutaId(String rutaId) {
        log.debug("Buscando horarios de la ruta: {}", rutaId);
        return horarioRepository.findByRutaId(rutaId);
    }
    
    public List<Horario> findByConductorId(String conductorId) {
        log.debug("Buscando horarios del conductor: {}", conductorId);
        return horarioRepository.findByConductorId(conductorId);
    }
    
    public List<Horario> findByTipoServicio(Horario.TipoServicio tipoServicio) {
        log.debug("Buscando horarios por tipo de servicio: {}", tipoServicio);
        return horarioRepository.findByTipoServicio(tipoServicio);
    }
    
    public List<Horario> findByEstado(Horario.EstadoHorario estado) {
        log.debug("Buscando horarios por estado: {}", estado);
        return horarioRepository.findByEstado(estado);
    }
    
    public List<Horario> findHorariosActivos() {
        log.debug("Buscando horarios activos");
        return horarioRepository.findByActivoTrue();
    }
    
    public List<Horario> findByEstaciones(String estacionOrigenId, String estacionDestinoId) {
        log.debug("Buscando horarios entre estaciones {} y {}", estacionOrigenId, estacionDestinoId);
        return horarioRepository.findByEstacionOrigenIdAndEstacionDestinoId(estacionOrigenId, estacionDestinoId);
    }
    
    public List<Horario> findByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Buscando horarios con salida entre {} y {}", fechaInicio, fechaFin);
        return horarioRepository.findByFechaSalidaBetween(fechaInicio, fechaFin);
    }
    
    public List<Horario> findByFechaLlegadaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Buscando horarios con llegada entre {} y {}", fechaInicio, fechaFin);
        return horarioRepository.findByFechaLlegadaBetween(fechaInicio, fechaFin);
    }
    
    public List<Horario> findByFechaSalidaAfter(LocalDateTime fecha) {
        log.debug("Buscando horarios con salida después de: {}", fecha);
        return horarioRepository.findByFechaSalidaAfter(fecha);
    }
    
    public List<Horario> findByFechaSalidaBefore(LocalDateTime fecha) {
        log.debug("Buscando horarios con salida antes de: {}", fecha);
        return horarioRepository.findByFechaSalidaBefore(fecha);
    }
    
    public List<Horario> findByEstacionParada(String estacionId) {
        log.debug("Buscando horarios que paran en estación: {}", estacionId);
        return horarioRepository.findByEstacionParada(estacionId);
    }
    
    public List<Horario> findByLlegadaEstacionEntreFechas(String estacionId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Buscando horarios con llegada a estación {} entre {} y {}", estacionId, fechaInicio, fechaFin);
        return horarioRepository.findByLlegadaEstacionEntreFechas(estacionId, fechaInicio, fechaFin);
    }
    
    public List<Horario> findByCapacidadMinima(Integer capacidadMinima) {
        log.debug("Buscando horarios con capacidad mínima de {} pasajeros", capacidadMinima);
        return horarioRepository.findByCapacidadMinima(capacidadMinima);
    }
    
    public List<Horario> findByOcupacionMinima(Double ocupacionMinima) {
        log.debug("Buscando horarios con ocupación mínima del {}%", ocupacionMinima);
        return horarioRepository.findByOcupacionMinima(ocupacionMinima);
    }
    
    public List<Horario> findHorariosCasiLlenos() {
        log.debug("Buscando horarios casi llenos (>90% ocupación)");
        return horarioRepository.findHorariosCasiLlenos();
    }
    
    public List<Horario> findHorariosConPocaOcupacion() {
        log.debug("Buscando horarios con poca ocupación (<30%)");
        return horarioRepository.findHorariosConPocaOcupacion();
    }
    
    public List<Horario> findByTarifaMaxima(Double tarifaMaxima) {
        log.debug("Buscando horarios con tarifa máxima de {}", tarifaMaxima);
        return horarioRepository.findByTarifaMaxima(tarifaMaxima);
    }
    
    public List<Horario> findByClaseDisponible(String nombreClase) {
        log.debug("Buscando horarios con clase disponible: {}", nombreClase);
        return horarioRepository.findByClaseDisponible(nombreClase);
    }
    
    public List<Horario> findHorariosLunes() {
        log.debug("Buscando horarios que operan los lunes");
        return horarioRepository.findHorariosLunes();
    }
    
    public List<Horario> findHorariosDomingo() {
        log.debug("Buscando horarios que operan los domingos");
        return horarioRepository.findHorariosDomingo();
    }
    
    public List<Horario> findHorariosConIncidenciasActivas() {
        log.debug("Buscando horarios con incidencias activas");
        return horarioRepository.findHorariosConIncidenciasActivas();
    }
    
    public List<Horario> findHorariosRetrasados() {
        log.debug("Buscando horarios retrasados");
        return horarioRepository.findHorariosRetrasados();
    }
    
    public List<Horario> findHorariosCancelados() {
        log.debug("Buscando horarios cancelados");
        return horarioRepository.findHorariosCancelados();
    }
    
    public List<Horario> findHorariosEnMarcha() {
        log.debug("Buscando horarios en marcha");
        return horarioRepository.findHorariosEnMarcha();
    }
    
    public Horario save(Horario horario) {
        log.info("Guardando horario: {}", horario.getCodigoServicio());
        
        if (horario.getFechaCreacion() == null) {
            horario.setFechaCreacion(LocalDateTime.now());
        }
        horario.setFechaActualizacion(LocalDateTime.now());
        
        return horarioRepository.save(horario);
    }
    
    public Horario update(String id, Horario horarioActualizado) {
        log.info("Actualizando horario con ID: {}", id);
        
        return horarioRepository.findById(id)
            .map(horarioExistente -> {
                horarioActualizado.setId(id);
                horarioActualizado.setFechaCreacion(horarioExistente.getFechaCreacion());
                horarioActualizado.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horarioActualizado);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public void deleteById(String id) {
        log.info("Eliminando horario con ID: {}", id);
        
        if (!horarioRepository.existsById(id)) {
            throw new RuntimeException("Horario no encontrado con ID: " + id);
        }
        
        horarioRepository.deleteById(id);
    }
    
    public boolean existsByCodigoServicio(String codigoServicio) {
        return horarioRepository.existsByCodigoServicio(codigoServicio);
    }
    
    public long countByTipoServicio(Horario.TipoServicio tipoServicio) {
        return horarioRepository.countByTipoServicio(tipoServicio);
    }
    
    public long countByEstado(Horario.EstadoHorario estado) {
        return horarioRepository.countByEstado(estado);
    }
    
    public long countByTrenId(String trenId) {
        return horarioRepository.countByTrenId(trenId);
    }
    
    public long countByRutaId(String rutaId) {
        return horarioRepository.countByRutaId(rutaId);
    }
    
    public long countByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return horarioRepository.countByFechaSalidaBetween(fechaInicio, fechaFin);
    }
    
    public Integer sumPasajerosActuales() {
        return horarioRepository.sumPasajerosActuales();
    }
    
    public Integer sumCapacidadTotal() {
        return horarioRepository.sumCapacidadTotal();
    }
    
    public Double avgTarifa() {
        return horarioRepository.avgTarifa();
    }
    
    public Horario cambiarEstado(String id, Horario.EstadoHorario nuevoEstado) {
        log.info("Cambiando estado de horario {} a: {}", id, nuevoEstado);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                horario.setEstado(nuevoEstado);
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public Horario asignarConductor(String id, String conductorId) {
        log.info("Asignando conductor {} a horario: {}", conductorId, id);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                horario.setConductorId(conductorId);
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public Horario asignarConductorSuplente(String id, String conductorSuplenteId) {
        log.info("Asignando conductor suplente {} a horario: {}", conductorSuplenteId, id);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                horario.setConductorSuplenteId(conductorSuplenteId);
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }

    public Horario actualizarOcupacion(String id, Integer pasajerosActuales) {
        log.info("Actualizando ocupación de horario {}: {} pasajeros", id, pasajerosActuales);

        return horarioRepository.findById(id)
                .map(horario -> {
                    int pax = (pasajerosActuales == null) ? 0 : pasajerosActuales;
                    horario.setPasajerosActuales(pax);

                    Integer cap = horario.getCapacidadPasajeros();
                    if (cap != null && cap > 0) {
                        horario.setOcupacionPorcentaje((double) pax / cap * 100);
                    } else {
                        horario.setOcupacionPorcentaje(0.0);
                    }

                    horario.setFechaActualizacion(LocalDateTime.now());
                    return horarioRepository.save(horario);
                })
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public Horario actualizarTarifa(String id, Double nuevaTarifa) {
        log.info("Actualizando tarifa de horario {} a: {}", id, nuevaTarifa);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                horario.setTarifa(nuevaTarifa);
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public Horario registrarIncidencia(String id, Horario.IncidenciaHorario incidencia) {
        log.info("Registrando incidencia en horario: {}", id);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                incidencia.setId(java.util.UUID.randomUUID().toString());
                incidencia.setFecha(LocalDateTime.now());
                horario.getIncidencias().add(incidencia);
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
    
    public Horario registrarRetraso(String id, Integer minutosRetraso, String motivo) {
        log.info("Registrando retraso de {} minutos en horario: {}", minutosRetraso, id);
        
        return horarioRepository.findById(id)
            .map(horario -> {
                Horario.IncidenciaHorario incidencia = Horario.IncidenciaHorario.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .fecha(LocalDateTime.now())
                    .tipo(Horario.TipoIncidencia.RETRASO)
                    .descripcion("Retraso de " + minutosRetraso + " minutos: " + motivo)
                    .retrasoMinutos(minutosRetraso)
                    .resuelta(false)
                    .build();
                
                horario.getIncidencias().add(incidencia);
                
                if (horario.getEstado() == Horario.EstadoHorario.PROGRAMADO) {
                    horario.setEstado(Horario.EstadoHorario.RETRASADO);
                }
                
                horario.setFechaActualizacion(LocalDateTime.now());
                return horarioRepository.save(horario);
            })
            .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id));
    }
}
