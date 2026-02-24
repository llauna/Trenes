package com.david.trenes.service;

import com.david.trenes.model.Pasajero;
import com.david.trenes.repository.PasajeroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasajeroService {

    private final PasajeroRepository pasajeroRepository;


    public List<Pasajero> findAll() {
        return pasajeroRepository.findAll();
    }
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

    public List<Pasajero> seedAleatorios(String usuarioId, int count) {
        int n = Math.max(0, Math.min(count, 1000)); // límite de seguridad
        if (n == 0) return List.of();

        LocalDateTime now = LocalDateTime.now();

        Set<String> documentosUsados = new HashSet<>();
        Set<String> emailsUsados = new HashSet<>();
        Set<String> telefonosUsados = new HashSet<>();

        List<Pasajero> nuevos = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            String token = UUID.randomUUID().toString().replace("-", "");

            String nombre = "Nombre" + token.substring(0, 6);
            String apellidos = "Apellido" + token.substring(6, 12);

            String documento = generarDocumentoUnico(token, documentosUsados);
            String email = generarEmailUnico(token, emailsUsados);
            String telefono = generarTelefonoUnico(token, telefonosUsados);

            nuevos.add(Pasajero.builder()
                    .id(null)
                    .usuarioId(usuarioId)
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .documento(documento)
                    .email(email)
                    .telefono(telefono)
                    .activo(true)
                    .fechaCreacion(now)
                    .fechaActualizacion(now)
                    .build());
        }

        return pasajeroRepository.saveAll(nuevos);
    }

    private String generarDocumentoUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String documento = "DOC-" + token.substring(0, 12).toUpperCase();

            if (usadosEnLote.contains(documento)) continue;
            if (pasajeroRepository.existsByDocumento(documento)) continue;

            usadosEnLote.add(documento);
            return documento;
        }
        throw new IllegalStateException("No se pudo generar un documento único tras varios intentos");
    }

    private String generarEmailUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String email = "pax-" + token.substring(0, 16).toLowerCase() + "@example.test";

            if (usadosEnLote.contains(email)) continue;
            if (pasajeroRepository.existsByEmail(email)) continue;

            usadosEnLote.add(email);
            return email;
        }
        throw new IllegalStateException("No se pudo generar un email único tras varios intentos");
    }

    private String generarTelefonoUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String ochoDigitos = token.replaceAll("[^0-9]", "");
            if (ochoDigitos.length() < 8) {
                ochoDigitos = (ochoDigitos + "00000000").substring(0, 8);
            } else {
                ochoDigitos = ochoDigitos.substring(0, 8);
            }

            String telefono = "6" + ochoDigitos; // 9 dígitos empezando por 6 (formato ES simplificado)

            if (usadosEnLote.contains(telefono)) continue;
            if (pasajeroRepository.existsByTelefono(telefono)) continue;

            usadosEnLote.add(telefono);
            return telefono;
        }
        throw new IllegalStateException("No se pudo generar un teléfono único tras varios intentos");
    }
}
