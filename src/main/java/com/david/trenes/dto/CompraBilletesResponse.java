package com.david.trenes.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraBilletesResponse {
    private Integer totalBilletes;
    private List<CompraBilleteResponse> billetes;
}
