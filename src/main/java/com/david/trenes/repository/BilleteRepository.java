package com.david.trenes.repository;

import com.david.trenes.model.Billete;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilleteRepository extends MongoRepository<Billete, String> {

    List<Billete> findByHorarioId(String horarioId);

    long countByHorarioIdAndEstado(String horarioId, Billete.EstadoBillete estado);

    List<Billete> findByPasajeroId(String pasajeroId);

    List<Billete> findByPasajeroIdIn(List<String> pasajeroIds);
}
