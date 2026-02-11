package com.david.trenes.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedPasajeroItem {
    private String pasajeroId;
    private String nombre;
    private String apellidos;
    private String documento;
    private String email;
    private String telefono;
}
