package com.david.trenes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private Integer status;
    private String code;
    private String message;
    private LocalDateTime timestamp;

    private NextHorarioSuggestion next;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NextHorarioSuggestion {
        private String horarioId;
        private String codigoServicio;
        private LocalDateTime fechaSalida;
    }
}