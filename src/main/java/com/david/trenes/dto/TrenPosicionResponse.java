package com.david.trenes.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrenPosicionResponse {
    private String trenId;
    private String rutaId;
    private String viaId;

    private String estacionActualId;
    private String estacionDestinoId;

    private Double kilometroEnVia;
    private Double latitud;
    private Double longitud;
    private Double altitud;

    private Double velocidadKmh;
    private Long segundosDesdeInicio;
    private Double distanciaTotalRecorridaKm;
}