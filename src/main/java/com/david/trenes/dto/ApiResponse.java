package com.david.trenes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Respuesta estándar para operaciones exitosas en la API.
 * Proporciona una estructura consistente para todas las respuestas exitosas.
 *
 * @param <T> Tipo de dato contenido en la respuesta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    /**
     * Indica si la operación fue exitosa
     */
    private boolean ok;

    /**
     * Datos retornados por la operación
     */
    private T data;

    /**
     * Mensaje descriptivo de la operación
     */
    private String message;

    /**
     * Timestamp de la respuesta
     */
    private LocalDateTime timestamp;

    /**
     * Crea una respuesta exitosa con datos
     *
     * @param data Datos a incluir en la respuesta
     * @param <T>  Tipo de datos
     * @return ApiResponse con ok=true y los datos proporcionados
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .ok(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Crea una respuesta exitosa con datos y mensaje
     *
     * @param data    Datos a incluir en la respuesta
     * @param message Mensaje descriptivo
     * @param <T>     Tipo de datos
     * @return ApiResponse con ok=true, datos y mensaje
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .ok(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Crea una respuesta exitosa solo con mensaje
     *
     * @param message Mensaje descriptivo
     * @return ApiResponse con ok=true y mensaje
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .ok(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
