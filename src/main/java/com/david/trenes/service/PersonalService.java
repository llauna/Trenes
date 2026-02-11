package com.david.trenes.service;

import com.david.trenes.model.Personal;
import com.david.trenes.repository.PersonalRepository;
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
public class PersonalService {

    private final PersonalRepository personalRepository;

    public List<Personal> findByUsuario(String usuarioId) {
        return personalRepository.findByUsuarioId(usuarioId);
    }

    public List<Personal> findByTipoPersonal(Personal.TipoPersonal tipoPersonal) {
        return personalRepository.findByTipoPersonal(tipoPersonal);
    }

    public List<Personal> findByUsuarioAndTipoPersonal(String usuarioId, Personal.TipoPersonal tipoPersonal) {
        return personalRepository.findByUsuarioIdAndTipoPersonal(usuarioId, tipoPersonal);
    }

    public List<Personal> findByActivo(Boolean activo) {
        return personalRepository.findByActivo(activo);
    }

    public Personal create(String usuarioId, Personal personal) {
        personal.setId(null);
        personal.setUsuarioId(usuarioId);
        personal.setActivo(personal.getActivo() != null ? personal.getActivo() : true);
        personal.setFechaCreacion(LocalDateTime.now());
        personal.setFechaActualizacion(LocalDateTime.now());
        
        if (personal.getFechaContratacion() == null) {
            personal.setFechaContratacion(LocalDateTime.now());
        }
        
        return personalRepository.save(personal);
    }

    public Personal update(String usuarioId, String personalId, Personal actualizado) {
        Personal existente = personalRepository.findById(personalId)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + personalId));

        if (!usuarioId.equals(existente.getUsuarioId())) {
            throw new RuntimeException("No tienes permisos para modificar este personal");
        }

        actualizado.setId(personalId);
        actualizado.setUsuarioId(usuarioId);
        actualizado.setFechaCreacion(existente.getFechaCreacion());
        actualizado.setFechaActualizacion(LocalDateTime.now());
        actualizado.setFechaContratacion(existente.getFechaContratacion());
        
        return personalRepository.save(actualizado);
    }

    public void delete(String usuarioId, String personalId) {
        if (!personalRepository.existsByIdAndUsuarioId(personalId, usuarioId)) {
            throw new RuntimeException("Personal no encontrado o no pertenece al usuario");
        }
        personalRepository.deleteById(personalId);
    }

    public List<Personal> seedAleatorios(String usuarioId, int count) {
        int n = Math.max(0, Math.min(count, 1000)); // límite de seguridad
        if (n == 0) return List.of();

        LocalDateTime now = LocalDateTime.now();

        Set<String> documentosUsados = new HashSet<>();
        Set<String> emailsUsados = new HashSet<>();
        Set<String> telefonosUsados = new HashSet<>();
        Set<String> numerosEmpleadoUsados = new HashSet<>();

        List<Personal> nuevos = new ArrayList<>(n);
        Personal.TipoPersonal[] tipos = Personal.TipoPersonal.values();

        for (int i = 0; i < n; i++) {
            String token = UUID.randomUUID().toString().replace("-", "");
            Personal.TipoPersonal tipo = tipos[i % tipos.length];

            String nombre = generarNombrePorTipo(tipo, token);
            String apellidos = "Apellido" + token.substring(6, 12);

            String documento = generarDocumentoUnico(token, documentosUsados);
            String email = generarEmailUnico(token, emailsUsados);
            String telefono = generarTelefonoUnico(token, telefonosUsados);
            String numeroEmpleado = generarNumeroEmpleadoUnico(token, numerosEmpleadoUsados);

            Personal.PersonalBuilder builder = Personal.builder()
                    .id(null)
                    .usuarioId(usuarioId)
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .documento(documento)
                    .email(email)
                    .telefono(telefono)
                    .numeroEmpleado(numeroEmpleado)
                    .tipoPersonal(tipo)
                    .activo(true)
                    .fechaCreacion(now)
                    .fechaActualizacion(now)
                    .fechaContratacion(now);

            // Campos específicos por tipo
            if (tipo == Personal.TipoPersonal.CONDUCTOR) {
                builder.licenciaConducir("LIC-" + token.substring(0, 8).toUpperCase());
            } else {
                builder.especialidad(generarEspecialidadPorTipo(tipo));
            }

            nuevos.add(builder.build());
        }

        return personalRepository.saveAll(nuevos);
    }

    private String generarNombrePorTipo(Personal.TipoPersonal tipo, String token) {
        String base = token.substring(0, 6);
        return switch (tipo) {
            case CONDUCTOR -> "Conductor" + base;
            case CABINA -> "Tripulante" + base;
            case RESTAURANTE -> "Camarero" + base;
        };
    }

    private String generarEspecialidadPorTipo(Personal.TipoPersonal tipo) {
        return switch (tipo) {
            case CABINA -> "Atención al cliente";
            case RESTAURANTE -> "Servicio de restauración";
            default -> null;
        };
    }

    private String generarDocumentoUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String documento = "DOC-" + token.substring(0, 12).toUpperCase();

            if (usadosEnLote.contains(documento)) continue;
            if (personalRepository.existsByDocumento(documento)) continue;

            usadosEnLote.add(documento);
            return documento;
        }
        throw new IllegalStateException("No se pudo generar un documento único tras varios intentos");
    }

    private String generarEmailUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String email = "personal-" + token.substring(0, 16).toLowerCase() + "@trenes.test";

            if (usadosEnLote.contains(email)) continue;
            if (personalRepository.existsByEmail(email)) continue;

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
            if (personalRepository.existsByTelefono(telefono)) continue;

            usadosEnLote.add(telefono);
            return telefono;
        }
        throw new IllegalStateException("No se pudo generar un teléfono único tras varios intentos");
    }

    private String generarNumeroEmpleadoUnico(String tokenBase, Set<String> usadosEnLote) {
        for (int intento = 0; intento < 50; intento++) {
            String token = (intento == 0) ? tokenBase : UUID.randomUUID().toString().replace("-", "");
            String numero = "EMP-" + token.substring(0, 8).toUpperCase();

            if (usadosEnLote.contains(numero)) continue;
            if (personalRepository.existsByNumeroEmpleado(numero)) continue;

            usadosEnLote.add(numero);
            return numero;
        }
        throw new IllegalStateException("No se pudo generar un número de empleado único tras varios intentos");
    }
}
