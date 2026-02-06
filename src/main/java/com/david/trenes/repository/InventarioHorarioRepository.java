package com.david.trenes.repository;

import com.david.trenes.model.InventarioHorario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioHorarioRepository extends MongoRepository<InventarioHorario, String> {
}
