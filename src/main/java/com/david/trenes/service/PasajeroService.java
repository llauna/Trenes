package com.david.trenes.service;

import com.david.trenes.model.Pasajero;
import com.david.trenes.repository.PasajeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PasajeroService {

    private final PasajeroRepository pasajeroRepository;

    public List<Pasajero> findByUsuario(String usuarioId) {
        return pasajeroRepository.findByUsuarioId(usuarioId);
    }

    public Pasajero create(String usuarioId, Pasajero pasajero) {
        pasajero.setId(null);
        pasajero.setUsuarioId(usuarioId);
        pasajero.setActivo(pasajero.getActivo() != null ? pasajero.getActivo() : true);
        pasajero.setFechaCreacion(LocalDateTime.now());
        pasajero.setFechaActualizacion(LocalDateTime.now());
        return pasajeroRepository.save(pasajero);
    }

    public Pasajero update(String usuarioId, String pasajeroId, Pasajero actualizado) {
        Pasajero existente = pasajeroRepository.findById(pasajeroId)
                .orElseThrow(() -> new RuntimeException("Pasajero no encontrado con ID: " + pasajeroId));

        if (!usuarioId.equals(existente.getUsuarioId())) {
            throw new RuntimeException("No tienes permisos para modificar este pasajero");
        }

        actualizado.setId(pasajeroId);
        actualizado.setUsuarioId(usuarioId);
        actualizado.setFechaCreacion(existente.getFechaCreacion());
        actualizado.setFechaActualizacion(LocalDateTime.now());
        return pasajeroRepository.save(actualizado);
    }

    public void delete(String usuarioId, String pasajeroId) {
        if (!pasajeroRepository.existsByIdAndUsuarioId(pasajeroId, usuarioId)) {
            throw new RuntimeException("Pasajero no encontrado o no pertenece al usuario");
        }
        pasajeroRepository.deleteById(pasajeroId);
    }
}