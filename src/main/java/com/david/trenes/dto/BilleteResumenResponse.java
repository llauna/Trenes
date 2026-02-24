package com.david.trenes.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BilleteResumenResponse {
    private String billeteId;
    private String codigoBillete;
    private String horarioId;
    private String pasajeroId;
    private String estacionOrigenId;
    private String estacionDestinoId;
    private String clase;
    private String estado;
    private Double precioTotal;
    private LocalDateTime fechaCompra;

    private Integer vagonNumero;
    private Integer asientoNumero;
}
