package com.david.trenes.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraBilleteResponse {
    private String billeteId;
    private String codigoBillete;
    private String estado;
}
