package com.david.trenes.repository;

import com.david.trenes.model.Incidente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenteRepository extends MongoRepository<Incidente, String> {
    
    Optional<Incidente> findByCodigoIncidente(String codigoIncidente);
    
    List<Incidente> findByTituloContainingIgnoreCase(String titulo);
    
    List<Incidente> findByTipoIncidente(Incidente.TipoIncidente tipoIncidente);
    
    List<Incidente> findBySeveridad(Incidente.SeveridadIncidente severidad);
    
    List<Incidente> findByEstado(Incidente.EstadoIncidente estado);
    
    List<Incidente> findByOperadorAsignado(String operadorAsignado);
    
    List<Incidente> findByReportadoPor(String reportadoPor);
    
    List<Incidente> findByEquiposResponsablesContaining(String equipoId);
    
    @Query("{'ubicacion.latitud': {$gte: ?0, $lte: ?1}, 'ubicacion.longitud': {$gte: ?2, $lte: ?3}}")
    List<Incidente> findByCoordenadasRango(Double latMin, Double latMax, Double lonMin, Double lonMax);
    
    @Query("{'ubicacionDescripcion': {$regex: ?0, $options: 'i'}}")
    List<Incidente> findByUbicacionDescripcionContaining(String ubicacion);
    
    @Query("{'fechaHora': {$gte: ?0, $lte: ?1}}")
    List<Incidente> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'fechaHora': {$gte: ?0}}")
    List<Incidente> findByFechaHoraAfter(LocalDateTime fecha);
    
    @Query("{'fechaHora': {$lte: ?0}}")
    List<Incidente> findByFechaHoraBefore(LocalDateTime fecha);
    
    @Query("{'fechaDeteccion': {$gte: ?0, $lte: ?1}}")
    List<Incidente> findByFechaDeteccionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'duracionMinutos': {$gte: ?0}}")
    List<Incidente> findByDuracionMinima(Integer duracionMinima);
    
    @Query("{'duracionMinutos': {$lte: ?0}}")
    List<Incidente> findByDuracionMaxima(Integer duracionMaxima);
    
    @Query("{'elementosAfectados.tipo': ?0}")
    List<Incidente> findByElementoAfectadoTipo(String tipoElemento);
    
    @Query("{'elementosAfectados.id': ?0}")
    List<Incidente> findByElementoAfectadoId(String elementoId);
    
    @Query("{'impactoOperativo.afectaServicio': true}")
    List<Incidente> findIncidentesAfectanServicio();
    
    @Query("{'impactoOperativo.serviciosAfectados': {$gte: ?0}}")
    List<Incidente> findByServiciosAfectadosMinimos(Integer serviciosMinimos);
    
    @Query("{'impactoOperativo.pasajerosAfectados': {$gte: ?0}}")
    List<Incidente> findByPasajerosAfectadosMinimos(Integer pasajerosMinimos);
    
    @Query("{'impactoOperativo.trenesAfectados': {$gte: ?0}}")
    List<Incidente> findByTrenesAfectadosMinimos(Integer trenesMinimos);
    
    @Query("{'costoEstimado': {$gte: ?0}}")
    List<Incidente> findByCostoEstimadoMinimo(Double costoMinimo);
    
    @Query("{'costoReal': {$gte: ?0}}")
    List<Incidente> findByCostoRealMinimo(Double costoMinimo);
    
    @Query("{'severidad': {$in: ['ALTA', 'CRITICA', 'CATASTROFICA']}, 'estado': {$ne: 'CERRADO'}}")
    List<Incidente> findIncidentesGravesActivos();
    
    @Query("{'estado': 'EN_PROGRESO'}")
    List<Incidente> findIncidentesEnProgreso();
    
    @Query("{'estado': 'CONTENIDO'}")
    List<Incidente> findIncidentesContenidos();
    
    @Query("{'estado': 'RESUELTO'}")
    List<Incidente> findIncidentesResueltos();
    
    @Query("{'estado': 'REPORTADO'}")
    List<Incidente> findIncidentesReportados();
    
    @Query("{'accionesTomadas.estado': 'PENDIENTE'}")
    List<Incidente> findIncidentesConAccionesPendientes();
    
    @Query("{'recursosAsignados.tipo': ?0}")
    List<Incidente> findByTipoRecursoAsignado(String tipoRecurso);
    
    @Query("{'comunicaciones.tipo': ?0}")
    List<Incidente> findByTipoComunicacion(String tipoComunicacion);
    
    @Query("{'comunicaciones.confirmada': false}")
    List<Incidente> findIncidentesConComunicacionesNoConfirmadas();
    
    @Query("{'incidentesRelacionados': {$in: ?0}}")
    List<Incidente> findByIncidentesRelacionados(List<String> incidenteIds);
    
    boolean existsByCodigoIncidente(String codigoIncidente);
    
    long countByTipoIncidente(Incidente.TipoIncidente tipoIncidente);
    
    long countBySeveridad(Incidente.SeveridadIncidente severidad);
    
    long countByEstado(Incidente.EstadoIncidente estado);
    
    long countByOperadorAsignado(String operadorAsignado);
    
    @Query("{'fechaHora': {$gte: ?0, $lte: ?1}}")
    long countByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("{'impactoOperativo.afectaServicio': true}")
    long countAfectanServicio();
    
    @Query("{'costoEstimado': {$sum: 1}}")
    Double sumCostoEstimado();
    
    @Query("{'costoReal': {$sum: 1}}")
    Double sumCostoReal();
    
    @Query("{'duracionMinutos': {$avg: 1}}")
    Double avgDuracion();
    
    @Query("{'impactoOperativo.pasajerosAfectados': {$sum: 1}}")
    Integer sumPasajerosAfectados();
    
    @Query("{'impactoOperativo.trenesAfectados': {$sum: 1}}")
    Integer sumTrenesAfectados();
}
