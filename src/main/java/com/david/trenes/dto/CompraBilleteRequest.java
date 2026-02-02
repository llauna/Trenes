package com.david.trenes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraBilleteRequest {

    @NotBlank
    private String horarioId;

    @NotBlank
    private String estacionOrigenId;

    @NotBlank
    private String estacionDestinoId;

    @NotBlank
    private String clase;

    @NotEmpty
    private List<String> pasajeroIds;
}