package com.david.trenes.repository;

import com.david.trenes.model.Horario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends MongoRepository<Horario, String> {
    
    Optional<Horario> findByCodigoServicio(String codigoServicio);
    
    List<Horario> findByNumeroServicio(String numeroServicio);
    
    List<Horario> findByTrenId(String trenId);
    
    List<Horario> findByRutaId(String rutaId);
    
    List<Horario> findByConductorId(String conductorId);
    
    List<Horario> findByTipoServicio(Horario.TipoServicio tipoServicio);
    
    List<Horario> findByEstado(Horario.EstadoHorario estado);
    
    List<Horario> findByActivoTrue();
    
    List<Horario> findByEstacionOrigenIdAndEstacionDestinoId(String estacionOrigenId, String estacionDestinoId);
    
    @Query("{'fechaSalida': {$gte: ?0, $lte: ?1}}")
    List<Horario> findByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaLlegada': {$gte: ?0, $lte: ?1}}")
    List<Horario> findByFechaLlegadaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaSalida': {$gte: ?0}}")
    List<Horario> findByFechaSalidaAfter(LocalDateTime fecha);
    
    @Query("{'fechaSaluta': {$lte: ?0}}")
    List<Horario> findByFechaSalidaBefore(LocalDateTime fecha);
    
    @Query("{'paradas.estacionId': ?0}")
    List<Horario> findByEstacionParada(String estacionId);
    
    @Query("{'paradas.horaLlegadaProgramada': {$gte: ?0, $lte: ?1}}")
    List<Horario> findByLlegadaEstacionEntreFechas(String estacionId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'capacidadPasajeros': {$gte: ?0}}")
    List<Horario> findByCapacidadMinima(Integer capacidadMinima);
    
    @Query("{'ocupacionPorcentaje': {$gte: ?0}}")
    List<Horario> findByOcupacionMinima(Double ocupacionMinima);
    
    @Query("{'ocupacionPorcentaje': {$gte: 90}}")
    List<Horario> findHorariosCasiLlenos();
    
    @Query("{'ocupacionPorcentaje': {$lt: 30}}")
    List<Horario> findHorariosConPocaOcupacion();
    
    @Query("{'tarifa': {$lte: ?0}}")
    List<Horario> findByTarifaMaxima(Double tarifaMaxima);
    
    @Query("{'clases.nombre': ?0, 'clases.disponible': true}")
    List<Horario> findByClaseDisponible(String nombreClase);
    
    @Query("{'frecuencia.lunes': true, 'activo': true}")
    List<Horario> findHorariosLunes();
    
    @Query("{'frecuencia.domingo': true, 'activo': true}")
    List<Horario> findHorariosDomingo();
    
    @Query("{'incidencias.resuelta': false}")
    List<Horario> findHorariosConIncidenciasActivas();
    
    @Query("{'estado': 'RETRASADO'}")
    List<Horario> findHorariosRetrasados();
    
    @Query("{'estado': 'CANCELADO'}")
    List<Horario> findHorariosCancelados();
    
    @Query("{'estado': 'EN_MARCHA'}")
    List<Horario> findHorariosEnMarcha();
    
    boolean existsByCodigoServicio(String codigoServicio);
    
    long countByTipoServicio(Horario.TipoServicio tipoServicio);
    
    long countByEstado(Horario.EstadoHorario estado);
    
    long countByTrenId(String trenId);
    
    long countByRutaId(String rutaId);
    
    @Query("{'fechaSalida': {$gte: ?0, $lte: ?1}}")
    long countByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'pasajerosActuales': {$sum: 1}}")
    Integer sumPasajerosActuales();
    
    @Query("{'capacidadPasajeros': {$sum: 1}}")
    Integer sumCapacidadTotal();
    
    @Query("{'tarifa': {$avg: 1}}")
    Double avgTarifa();
}
