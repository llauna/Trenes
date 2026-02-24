package com.david.trenes.controller;

import com.david.trenes.dto.ApiResponse;
import com.david.trenes.dto.PagedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller base con funcionalidad común para todos los controllers de la API.
 * Proporciona métodos estándar para crear respuestas consistentes.
 */
@Slf4j
public abstract class BaseController {

    /**
     * Crea una respuesta exitosa con datos (HTTP 200)
     *
     * @param data Datos a incluir en la respuesta
     * @param <T>  Tipo de datos
     * @return ResponseEntity con ApiResponse y status 200
     */
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Crea una respuesta exitosa con datos y mensaje (HTTP 200)
     *
     * @param data    Datos a incluir en la respuesta
     * @param message Mensaje descriptivo
     * @param <T>     Tipo de datos
     * @return ResponseEntity con ApiResponse y status 200
     */
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Crea una respuesta exitosa solo con mensaje (HTTP 200)
     *
     * @param message Mensaje descriptivo
     * @return ResponseEntity con ApiResponse y status 200
     */
    protected ResponseEntity<ApiResponse<Void>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * Crea una respuesta de recurso creado (HTTP 201)
     *
     * @param data Datos del recurso creado
     * @param <T>  Tipo de datos
     * @return ResponseEntity con ApiResponse y status 201
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Recurso creado exitosamente"));
    }

    /**
     * Crea una respuesta de recurso creado con mensaje personalizado (HTTP 201)
     *
     * @param data    Datos del recurso creado
     * @param message Mensaje personalizado
     * @param <T>     Tipo de datos
     * @return ResponseEntity con ApiResponse y status 201
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }

    /**
     * Crea una respuesta sin contenido (HTTP 204)
     *
     * @return ResponseEntity con status 204
     */
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Crea una respuesta sin contenido con mensaje (HTTP 200)
     *
     * @param message Mensaje descriptivo
     * @param <T>    Tipo esperado (para compatibilidad)
     * @return ResponseEntity con ApiResponse y status 200
     */
    protected <T> ResponseEntity<ApiResponse<T>> noContent(String message) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .ok(true)
                .message(message)
                .build());
    }

    /**
     * Crea una respuesta de bad request (HTTP 400)
     *
     * @param message Mensaje de error
     * @param <T>    Tipo esperado (para compatibilidad)
     * @return ResponseEntity con ApiResponse y status 400
     */
    protected <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.<T>builder()
                        .ok(false)
                        .message(message)
                        .build());
    }

    /**
     * Crea una respuesta de no encontrado (HTTP 404)
     *
     * @param message Mensaje de error
     * @param <T>    Tipo esperado (para compatibilidad)
     * @return ResponseEntity con ApiResponse y status 404
     */
    protected <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<T>builder()
                        .ok(false)
                        .message(message)
                        .build());
    }

    /**
     * Crea una respuesta paginada (HTTP 200)
     *
     * @param content Lista de elementos
     * @param <T>     Tipo de elementos
     * @return ResponseEntity con PagedResponse y status 200
     */
    protected <T> ResponseEntity<PagedResponse<T>> paged(java.util.List<T> content) {
        PagedResponse.PageMetadata metadata = PagedResponse.PageMetadata.builder()
                .number(0)
                .size(content.size())
                .totalElements(content.size())
                .totalPages(1)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        return ResponseEntity.ok(PagedResponse.of(content, metadata));
    }

    /**
     * Crea una respuesta paginada con ApiResponse (HTTP 200)
     *
     * @param content       Lista de elementos
     * @param totalElements Total de elementos
     * @param <T>          Tipo de elementos
     * @return ResponseEntity con ApiResponse<PagedResponse> y status 200
     */
    protected <T> ResponseEntity<ApiResponse<PagedResponse<T>>> paged(java.util.List<T> content, long totalElements) {
        PagedResponse.PageMetadata metadata = PagedResponse.PageMetadata.builder()
                .number(0)
                .size(content.size())
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / content.size()))
                .first(true)
                .last(content.size() >= totalElements)
                .hasNext(content.size() < totalElements)
                .hasPrevious(false)
                .build();

        PagedResponse<T> pagedResponse = PagedResponse.of(content, metadata);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * Crea una respuesta paginada a partir de una página de Spring Data
     *
     * @param page Página de Spring Data
     * @param <T>  Tipo de elementos
     * @return ResponseEntity con PagedResponse y status 200
     */
    protected <T> ResponseEntity<PagedResponse<T>> paged(org.springframework.data.domain.Page<T> page) {
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    /**
     * Maneja Optional y crea respuesta apropiada
     *
     * @param optional Optional con el recurso
     * @param <T>      Tipo del recurso
     * @return ResponseEntity con el recurso si existe, o 404 si no
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleOptional(Optional<T> optional) {
        return optional.map(this::ok)
                .orElse(notFound("Recurso no encontrado"));
    }

    /**
     * Maneja Optional con mensaje personalizado
     *
     * @param optional Optional con el recurso
     * @param message  Mensaje para caso de no encontrado
     * @param <T>      Tipo del recurso
     * @return ResponseEntity con el recurso si existe, o 404 si no
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleOptional(Optional<T> optional, String message) {
        return optional.map(this::ok)
                .orElse(notFound(message));
    }

    /**
     * Registra información de la petición
     *
     * @param operation Operación siendo ejecutada
     * @param params    Parámetros relevantes
     */
    protected void logRequest(String operation, Object... params) {
        if (params.length == 0) {
            log.info("Ejecutando: {}", operation);
        } else {
            log.info("Ejecutando: {} | Parámetros: {}", operation, java.util.Arrays.toString(params));
        }
    }

    /**
     * Registra error en la petición
     *
     * @param operation Operación fallida
     * @param error     Error ocurrido
     */
    protected void logError(String operation, Throwable error) {
        log.error("Error en {}: {}", operation, error.getMessage(), error);
    }
}
