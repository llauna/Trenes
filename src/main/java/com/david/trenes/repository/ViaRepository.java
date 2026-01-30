package com.david.trenes.repository;

import com.david.trenes.model.Via;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViaRepository extends MongoRepository<Via, String> {
    
    Optional<Via> findByCodigoVia(String codigoVia);
    
    List<Via> findByEstacionOrigenIdOrEstacionDestinoId(String estacionOrigenId, String estacionDestinoId);
    
    List<Via> findByEstado(Via.EstadoVia estado);
    
    List<Via> findByTipoVia(Via.TipoVia tipoVia);
    
    List<Via> findByElectrificadaTrue();
    
    List<Via> findByElectrificadaFalse();
    
    @Query("{'coordenadaInicio.latitud': {$gte: ?0, $lte: ?1}, 'coordenadaInicio.longitud': {$gte: ?2, $lte: ?3}}")
    List<Via> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax);
    
    @Query("{'longitudKm': {$gte: ?0}}")
    List<Via> findByLongitudMinima(Double longitudMinima);
    
    @Query("{$or: [{'estacionOrigenId': ?0}, {'estacionDestinoId': ?0}]}")
    List<Via> findByEstacionConectada(String estacionId);
    
    @Query("{'proximaMantenimiento': {$lte: ?0}, 'estado': {$ne: 'MANTENIMIENTO'}}")
    List<Via> findViasRequierenMantenimiento(java.time.LocalDateTime fecha);
    
    @Query("{'velocidadMaxima': {$gte: ?0}}")
    List<Via> findByVelocidadMinima(Integer velocidadMinima);
    
    @Query("{'activo': true, 'estado': {$ne: 'DESACTIVADA'}}")
    List<Via> findViasActivas();
    
    @Query("{'señales': {$in: ?0}}")
    List<Via> findBySeñalId(List<String> señalIds);
    
    boolean existsByCodigoVia(String codigoVia);
    
    long countByEstado(Via.EstadoVia estado);
    
    long countByTipoVia(Via.TipoVia tipoVia);
    
    @Query("{'longitudKm': {$sum: 1}}")
    Double sumLongitudTotal();
    
    @Query("{'longitudKm': {$sum: 1}, 'estado': ?0}")
    Double sumLongitudPorEstado(Via.EstadoVia estado);
}
