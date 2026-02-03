package com.david.trenes.util;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

public class ValidationUtil {
    
    private static final String MONGODB_ID_PATTERN = "^[a-fA-F0-9]{24}$";
    
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
}
