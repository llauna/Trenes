package com.david.trenes.repository;

import com.david.trenes.model.Estacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstacionRepository extends MongoRepository<Estacion, String> {
    
    Optional<Estacion> findByCodigoEstacion(String codigoEstacion);
    
    List<Estacion> findByNombreContainingIgnoreCase(String nombre);
    
    List<Estacion> findByCiudadContainingIgnoreCase(String ciudad);
    
    List<Estacion> findByProvinciaContainingIgnoreCase(String provincia);
    
    List<Estacion> findByTipoEstacion(Estacion.TipoEstacion tipoEstacion);
    
    List<Estacion> findByCategoria(Estacion.CategoriaEstacion categoria);
    
    List<Estacion> findByActivoTrue();
    
    List<Estacion> findByAccesibilidadTrue();
    
    @Query("{'ubicacion.latitud': {$gte: ?0, $lte: ?1}, 'ubicacion.longitud': {$gte: ?2, $lte: ?3}}")
    List<Estacion> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax);
    
    @Query("{'capacidadEstacionamiento': {$gte: ?0}}")
    List<Estacion> findByCapacidadEstacionamientoMinima(Integer capacidad);
    
    @Query("{'andenes': {$size: ?0}}")
    List<Estacion> findByNumeroAndenes(Integer numeroAndenes);
    
    @Query("{'servicios.tipo': ?0, 'servicios.disponible': true}")
    List<Estacion> findByServicioDisponible(Estacion.TipoServicio tipoServicio);
    
    @Query("{'viasConectadas': {$in: ?0}}")
    List<Estacion> findByViaConectada(List<String> viaIds);
    
    List<Estacion> findBySupervisorId(String supervisorId);
    
    @Query("{'andenes.tipo': ?0, 'andenes.activo': true}")
    List<Estacion> findByAndenTipoDisponible(Estacion.TipoAnden tipoAnden);
    
    boolean existsByCodigoEstacion(String codigoEstacion);
    
    long countByTipoEstacion(Estacion.TipoEstacion tipoEstacion);
    
    long countByCategoria(Estacion.CategoriaEstacion categoria);
    
    long countByCiudad(String ciudad);
    
    long countByProvincia(String provincia);
    
    @Query("{'activo': true}")
    long countActivas();
    
    @Query("{'accesibilidad': true}")
    long countAccesibles();
}
