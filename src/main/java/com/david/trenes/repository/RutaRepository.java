package com.david.trenes.repository;

import com.david.trenes.model.Ruta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends MongoRepository<Ruta, String> {
    
    Optional<Ruta> findByCodigoRuta(String codigoRuta);
    
    List<Ruta> findByNombreContainingIgnoreCase(String nombre);
    
    List<Ruta> findByTipoRuta(Ruta.TipoRuta tipoRuta);
    
    List<Ruta> findByEstado(Ruta.EstadoRuta estado);
    
    List<Ruta> findByActivoTrue();
    
    List<Ruta> findByEstacionOrigenIdAndEstacionDestinoId(String estacionOrigenId, String estacionDestinoId);
    
    List<Ruta> findByEstacionOrigenIdOrEstacionDestinoId(String estacionId);
    
    @Query("{'estacionesIntermedias.estacionId': ?0}")
    List<Ruta> findByEstacionIntermedia(String estacionId);
    
    @Query("{'vias.viaId': ?0}")
    List<Ruta> findByViaId(String viaId);
    
    @Query("{'distanciaTotalKm': {$gte: ?0}}")
    List<Ruta> findByDistanciaMinima(Double distanciaMinima);
    
    @Query("{'distanciaTotalKm': {$lte: ?0}}")
    List<Ruta> findByDistanciaMaxima(Double distanciaMaxima);
    
    @Query("{'tiempoEstimadoMinutos': {$lte: ?0}}")
    List<Ruta> findByTiempoMaximo(Integer tiempoMaximo);
    
    @Query("{'velocidadPromedio': {$gte: ?0}}")
    List<Ruta> findByVelocidadPromedioMinima(Double velocidadMinima);
    
    @Query("{'prioridad': {$gte: ?0}}")
    List<Ruta> findByPrioridadMinima(Integer prioridadMinima);
    
    @Query("{'tarifaBase': {$lte: ?0}}")
    List<Ruta> findByTarifaMaxima(Double tarifaMaxima);
    
    @Query("{'zonas': {$in: ?0}}")
    List<Ruta> findByZona(String zona);
    
    @Query("{'frecuencia.lunes': true, 'activo': true}")
    List<Ruta> findRutasLunes();
    
    @Query("{'frecuencia.domingo': true, 'activo': true}")
    List<Ruta> findRutasDomingo();
    
    @Query("{'frecuencia.serviciosDia': {$gte: ?0}}")
    List<Ruta> findByServiciosDiaMinimo(Integer serviciosMinimo);
    
    @Query("{'estacionesIntermedias': {$size: ?0}}")
    List<Ruta> findByNumeroParadas(Integer numeroParadas);
    
    @Query("{'restricciones.tipo': ?0, 'restricciones.activa': true}")
    List<Ruta> findByRestriccionActiva(Ruta.TipoRestriccion tipoRestriccion);
    
    boolean existsByCodigoRuta(String codigoRuta);
    
    long countByTipoRuta(Ruta.TipoRuta tipoRuta);
    
    long countByEstado(Ruta.EstadoRuta estado);
    
    long countByActivoTrue();
    
    @Query("{'distanciaTotalKm': {$sum: 1}}")
    Double sumDistanciaTotal();
    
    @Query("{'distanciaTotalKm': {$sum: 1}, 'estado': ?0}")
    Double sumDistanciaPorEstado(Ruta.EstadoRuta estado);
    
    @Query("{'tiempoEstimadoMinutos': {$avg: 1}}")
    Double avgTiempoEstimado();
}
