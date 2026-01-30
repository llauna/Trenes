package com.david.trenes.repository;

import com.david.trenes.model.Mantenimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MantenimientoRepository extends MongoRepository<Mantenimiento, String> {
    
    Optional<Mantenimiento> findByCodigoMantenimiento(String codigoMantenimiento);
    
    List<Mantenimiento> findByElementoId(String elementoId);
    
    List<Mantenimiento> findByTipoElemento(Mantenimiento.TipoElemento tipoElemento);
    
    List<Mantenimiento> findByTipoMantenimiento(Mantenimiento.TipoMantenimiento tipoMantenimiento);
    
    List<Mantenimiento> findByPrioridad(Mantenimiento.PrioridadMantenimiento prioridad);
    
    List<Mantenimiento> findByEstado(Mantenimiento.EstadoMantenimiento estado);
    
    List<Mantenimiento> findByTecnicoPrincipalId(String tecnicoPrincipalId);
    
    List<Mantenimiento> findByTecnicosSecundariosContaining(String tecnicoId);
    
    @Query("{'fechaProgramada': {$gte: ?0, $lte: ?1}}")
    List<Mantenimiento> findByFechaProgramadaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaProgramada': {$gte: ?0}}")
    List<Mantenimiento> findByFechaProgramadaAfter(LocalDateTime fecha);
    
    @Query("{'fechaProgramada': {$lte: ?0}}")
    List<Mantenimiento> findByFechaProgramadaBefore(LocalDateTime fecha);
    
    @Query("{'fechaInicio': {$gte: ?0, $lte: ?1}}")
    List<Mantenimiento> findByFechaInicioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaFin': {$gte: ?0, $lte: ?1}}")
    List<Mantenimiento> findByFechaFinBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaProgramada': {$lte: ?0}, 'estado': {$ne: 'COMPLETADO'}}")
    List<Mantenimiento> findMantenimientosPendientes(LocalDateTime fecha);
    
    @Query("{'fechaProgramada': {$lte: ?0}, 'estado': 'PROGRAMADO'}")
    List<Mantenimiento> findMantenimientosProgramadosParaHoy(LocalDateTime fecha);
    
    @Query("{'estado': 'EN_PROGRESO'}")
    List<Mantenimiento> findMantenimientosEnProgreso();
    
    @Query("{'estado': 'COMPLETADO', 'fechaFin': {$gte: ?0, $lte: ?1}}")
    List<Mantenimiento> findMantenimientosCompletadosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'duracionEstimadaHoras': {$gte: ?0}}")
    List<Mantenimiento> findByDuracionEstimadaMinima(Integer duracionMinima);
    
    @Query("{'costoEstimado': {$gte: ?0}}")
    List<Mantenimiento> findByCostoEstimadoMinimo(Double costoMinimo);
    
    @Query("{'costoReal': {$gte: ?0}}")
    List<Mantenimiento> findByCostoRealMinimo(Double costoMinimo);
    
    @Query("{'impactoOperativo.afectaServicio': true}")
    List<Mantenimiento> findMantenimientosAfectanServicio();
    
    @Query("{'impactoOperativo.afectaServicio': false}")
    List<Mantenimiento> findMantenimientosNoAfectanServicio();
    
    @Query("{'prioridad': {$in: ['ALTA', 'CRITICA', 'EMERGENCIA']}, 'estado': {$ne: 'COMPLETADO'}}")
    List<Mantenimiento> findMantenimientosUrgentes();
    
    @Query("{'tareas.estado': 'PENDIENTE'}")
    List<Mantenimiento> findMantenimientosConTareasPendientes();
    
    @Query("{'recursos.tipo': ?0}")
    List<Mantenimiento> findByTipoRecurso(String tipoRecurso);
    
    @Query("{'materiales.proveedor': ?0}")
    List<Mantenimiento> findByProveedor(String proveedor);
    
    boolean existsByCodigoMantenimiento(String codigoMantenimiento);
    
    long countByTipoElemento(Mantenimiento.TipoElemento tipoElemento);
    
    long countByTipoMantenimiento(Mantenimiento.TipoMantenimiento tipoMantenimiento);
    
    long countByPrioridad(Mantenimiento.PrioridadMantenimiento prioridad);
    
    long countByEstado(Mantenimiento.EstadoMantenimiento estado);
    
    long countByTecnicoPrincipalId(String tecnicoPrincipalId);
    
    @Query("{'fechaProgramada': {$gte: ?0, $lte: ?1}}")
    long countByFechaProgramadaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'costoEstimado': {$sum: 1}}")
    Double sumCostoEstimado();
    
    @Query("{'costoReal': {$sum: 1}}")
    Double sumCostoReal();
    
    @Query("{'duracionEstimadaHoras': {$sum: 1}}")
    Integer sumDuracionEstimada();
    
    @Query("{'duracionRealHoras': {$sum: 1}}")
    Integer sumDuracionReal();
}
