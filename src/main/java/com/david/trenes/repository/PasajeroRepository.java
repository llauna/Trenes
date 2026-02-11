package com.david.trenes.repository;

import com.david.trenes.model.Pasajero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasajeroRepository extends MongoRepository<Pasajero, String> {

    List<Pasajero> findByUsuarioId(String usuarioId);

    boolean existsByIdAndUsuarioId(String id, String usuarioId);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);
}