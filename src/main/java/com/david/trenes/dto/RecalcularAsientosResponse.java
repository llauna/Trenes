package com.david.trenes.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecalcularAsientosResponse {
    private boolean dryRun;
    private boolean overwrite;

    private int totalEncontrados;
    private int totalProcesados;
    private int totalActualizados;

    private Map<String, Integer> actualizadosPorHorario;
}
