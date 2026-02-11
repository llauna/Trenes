package com.david.trenes.repository;

import com.david.trenes.model.Personal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalRepository extends MongoRepository<Personal, String> {

    List<Personal> findByUsuarioId(String usuarioId);

    List<Personal> findByTipoPersonal(Personal.TipoPersonal tipoPersonal);

    List<Personal> findByUsuarioIdAndTipoPersonal(String usuarioId, Personal.TipoPersonal tipoPersonal);

    List<Personal> findByActivo(Boolean activo);

    List<Personal> findByUsuarioIdAndActivo(String usuarioId, Boolean activo);

    List<Personal> findByTipoPersonalAndActivo(Personal.TipoPersonal tipoPersonal, Boolean activo);

    boolean existsByIdAndUsuarioId(String id, String usuarioId);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);

    boolean existsByNumeroEmpleado(String numeroEmpleado);
}
