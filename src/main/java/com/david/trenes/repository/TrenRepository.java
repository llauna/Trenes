package com.david.trenes.repository;

import com.david.trenes.model.Tren;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrenRepository extends MongoRepository<Tren, String> {
    
    Optional<Tren> findByNumeroTren(String numeroTren);
    
    Optional<Tren> findByMatricula(String matricula);
    
    List<Tren> findByTipoTren(Tren.TipoTren tipoTren);
    
    List<Tren> findByEstadoActual(Tren.EstadoTren estadoActual);
    
    List<Tren> findByActivoTrue();
    
    List<Tren> findByViaActualId(String viaActualId);
    
    List<Tren> findByRutaActualId(String rutaActualId);
    
    List<Tren> findByConductorActualId(String conductorActualId);
    
    @Query("{'ubicacionActual.latitud': {$gte: ?0, $lte: ?1}, 'ubicacionActual.longitud': {$gte: ?2, $lte: ?3}}")
    List<Tren> findByUbicacionRango(Double latMin, Double latMax, Double lonMin, Double lonMax);
    
    @Query("{'velocidadMaxima': {$gte: ?0}}")
    List<Tren> findByVelocidadMaximaMinima(Integer velocidadMinima);
    
    @Query("{'capacidadPasajeros': {$gte: ?0}}")
    List<Tren> findByCapacidadPasajerosMinima(Integer capacidadMinima);
    
    @Query("{'capacidadCarga': {$gte: ?0}}")
    List<Tren> findByCapacidadCargaMinima(Double capacidadMinima);
    
    @Query("{'añoFabricacion': {$gte: ?0}}")
    List<Tren> findByAñoFabricacionMinimo(Integer añoMinimo);
    
    @Query("{'proximaRevision': {$lte: ?0}, 'estadoActual': {$ne: 'MANTENIMIENTO'}}")
    List<Tren> findTrenesRequierenRevision(java.time.LocalDateTime fecha);
    
    @Query("{'kilometrosUltimaRevision': {$gte: ?0}}")
    List<Tren> findByKilometrosDesdeUltimaRevisionMinimos(Integer kilometros);
    
    @Query("{'fabricante': ?0}")
    List<Tren> findByFabricante(String fabricante);
    
    @Query("{'modelo': ?0}")
    List<Tren> findByModelo(String modelo);
    
    @Query("{'locomotoras.estado': 'OPERATIVA', 'estadoActual': {$ne: 'FUERA_SERVICIO'}}")
    List<Tren> findTrenesOperativos();
    
    @Query("{'estadoActual': 'EN_MARCHA'}")
    List<Tren> findTrenesEnMarcha();
    
    @Query("{'estadoActual': 'EN_ESTACION'}")
    List<Tren> findTrenesEnEstacion();
    
    @Query("{'incidencias.resuelta': false}")
    List<Tren> findTrenesConIncidenciasActivas();
    
    boolean existsByNumeroTren(String numeroTren);
    
    boolean existsByMatricula(String matricula);
    
    long countByTipoTren(Tren.TipoTren tipoTren);
    
    long countByEstadoActual(Tren.EstadoTren estadoActual);
    
    long countByActivoTrue();
    
    @Query("{'kilometrajeTotal': {$sum: 1}}")
    Double sumKilometrajeTotal();
    
    @Query("{'capacidadPasajeros': {$sum: 1}}")
    Integer sumCapacidadTotalPasajeros();
}
