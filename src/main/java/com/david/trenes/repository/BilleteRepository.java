package com.david.trenes.repository;

import com.david.trenes.model.Billete;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilleteRepository extends MongoRepository<Billete, String> {

    List<Billete> findByHorarioId(String horarioId);

    long countByHorarioIdAndEstado(String horarioId, Billete.EstadoBillete estado);

    List<Billete> findByPasajeroId(String pasajeroId);

    List<Billete> findByPasajeroIdIn(List<String> pasajeroIds);

    boolean existsByPasajeroId(String pasajeroId);

    long countByPasajeroIdIn(List<String> pasajeroIds);

    long countByPasajeroIdInAndEstado(List<String> pasajeroIds, Billete.EstadoBillete estado);

    @Query("{ $or: [ { 'vagon_numero': null }, { 'asiento_numero': null } ] }")
    List<Billete> findConUbicacionFaltante();

    @Query("{ 'horario_id': ?0, $or: [ { 'vagon_numero': null }, { 'asiento_numero': null } ] }")
    List<Billete> findConUbicacionFaltanteByHorarioId(String horarioId);
}
