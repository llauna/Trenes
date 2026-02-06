package com.david.trenes.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ValidationUtil {

    private static final String MONGODB_ID_PATTERN = "^[a-fA-F0-9]{24}$";
    private static final String UUID_PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    public static void validarMongoId(String id, String nombreCampo) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    nombreCampo + " es obligatorio");
        }

        if (!id.matches(MONGODB_ID_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    nombreCampo + " tiene formato inválido. Debe ser un ID de MongoDB válido (24 caracteres hexadecimales)");
        }
    }

    public static void validarMongoId(String id) {
        validarMongoId(id, "ID");
    }

    public static boolean esMongoIdValido(String id) {
        return id != null && id.matches(MONGODB_ID_PATTERN);
    }

    public static void validarUuid(String id, String nombreCampo) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    nombreCampo + " es obligatorio");
        }

        String trimmed = id.trim();
        if (!trimmed.matches(UUID_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    nombreCampo + " tiene formato inválido. Debe ser un UUID válido");
        }
    }

    public static void validarUuid(String id) {
        validarUuid(id, "ID");
    }

    public static boolean esUuidValido(String id) {
        return id != null && id.trim().matches(UUID_PATTERN);
    }
}