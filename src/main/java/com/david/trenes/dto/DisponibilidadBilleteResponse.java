package com.david.trenes.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadBilleteResponse {
    private String horarioId;
    private String trenId;
    private Integer capacidadEfectiva;
    private Integer vendidos;
    private Integer disponibles;
    private Double ocupacionPorcentaje;
}
