package com.david.trenes.repository;

import com.david.trenes.model.Signal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignalRepository extends MongoRepository<Signal, String> {
    
    Optional<Signal> findByCodigoSignal(String codigoSignal);
    
    List<Signal> findByNombreContainingIgnoreCase(String nombre);
    
    List<Signal> findByTipoSignal(Signal.TipoSignal tipoSignal);
    
    List<Signal> findByCategoria(Signal.CategoriaSignal categoria);
    
    List<Signal> findByEstadoActual(Signal.EstadoSignal estadoActual);
    
    List<Signal> findByViaId(String viaId);
    
    List<Signal> findByControladoPor(String controladoPor);
    
    List<Signal> findByActivoTrue();
    
    List<Signal> findByOrientacion(Signal.OrientacionSignal orientacion);
    
    @Query("{'ubicacion.latitud': {$gte: ?0, $lte: ?1}, 'ubicacion.longitud': {$gte: ?2, $lte: ?3}}")
    List<Signal> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax);
    
    @Query("{'kilometro': {$gte: ?0, $lte: ?1}}")
    List<Signal> findByKilometroRango(Double kilometroMin, Double kilometroMax);
    
    @Query("{'kilometro': {$gte: ?0}}")
    List<Signal> findByKilometroMinimo(Double kilometroMin);
    
    @Query("{'configuracion.velocidadLimitante': {$gte: ?0}}")
    List<Signal> findByVelocidadLimitanteMinima(Integer velocidadMinima);
    
    @Query("{'configuracion.automatizada': true}")
    List<Signal> findSignalesAutomatizadas();
    
    @Query("{'configuracion.manual': true}")
    List<Signal> findSignalesManuales();
    
    @Query("{'configuracion.remota': true}")
    List<Signal> findSignalesRemotas();
    
    @Query("{'alimentacion.tipo': ?0}")
    List<Signal> findByTipoAlimentacion(String tipoAlimentacion);
    
    @Query("{'alimentacion.voltaje': ?0}")
    List<Signal> findByVoltaje(Integer voltaje);
    
    @Query("{'alimentacion.redundancia': true}")
    List<Signal> findSignalesConRedundancia();
    
    @Query("{'alimentacion.ups': true}")
    List<Signal> findSignalesConUPS();
    
    @Query("{'alimentacion.autonomiaHoras': {$gte: ?0}}")
    List<Signal> findByAutonomiaMinima(Integer autonomiaMinima);
    
    @Query("{'comunicaciones.activa': true}")
    List<Signal> findSignalesConComunicacionActiva();
    
    @Query("{'comunicaciones.tipo': ?0}")
    List<Signal> findByTipoComunicacion(String tipoComunicacion);
    
    @Query("{'mantenimiento.ultimaRevision': {$lte: ?0}}")
    List<Signal> findSignalesRequierenRevision(LocalDateTime fecha);
    
    @Query("{'mantenimiento.proximaInspeccion': {$lte: ?0}}")
    List<Signal> findSignalesConInspeccionVencida(LocalDateTime fecha);
    
    @Query("{'incidencias.resuelta': false}")
    List<Signal> findSignalesConIncidenciasActivas();
    
    @Query("{'estadoActual': {$ne: 'APAGADA'}, 'activo': true}")
    List<Signal> findSignalesOperativas();
    
    @Query("{'estadoActual': 'APAGADA'}")
    List<Signal> findSignalesApagadas();
    
    @Query("{'estadoActual': 'EMERGENCIA'}")
    List<Signal> findSignalesEnEmergencia();
    
    @Query("{'configuracion.SignalesDependientes': {$in: ?0}}")
    List<Signal> findSignalesDependientesDe(List<String> SignalIds);
    
    @Query("{'configuracion.SignalesRelacionadas': {$in: ?0}}")
    List<Signal> findSignalesRelacionadasCon(List<String> SignalIds);
    
    boolean existsByCodigoSignal(String codigoSignal);
    
    long countByTipoSignal(Signal.TipoSignal tipoSignal);
    
    long countByCategoria(Signal.CategoriaSignal categoria);
    
    long countByEstadoActual(Signal.EstadoSignal estadoActual);
    
    long countByViaId(String viaId);
    
    long countByControladoPor(String controladoPor);
    
    @Query("{'activo': true}")
    long countActivas();
    
    @Query("{'estadoActual': {$ne: 'APAGADA'}, 'activo': true}")
    long countOperativas();
    
    @Query("{'incidencias.resuelta': false}")
    long countConIncidenciasActivas();
}
