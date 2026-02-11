package com.david.trenes.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedPasajerosResponse {
    private Integer creados;
    private List<SeedPasajeroItem> pasajeros;
}