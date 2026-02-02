package com.david.trenes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "billetes")
public class Billete {

    @Id
    private String id;

    @Field("codigo_billete")
    private String codigoBillete;

    @Field("horario_id")
    private String horarioId;

    @Field("pasajero_id")
    private String pasajeroId;

    @Field("estacion_origen_id")
    private String estacionOrigenId;

    @Field("estacion_destino_id")
    private String estacionDestinoId;

    @Field("clase")
    private String clase; // TURISTA, PRIMERA, etc.

    @Field("cantidad")
    private Integer cantidad;

    @Field("precio_total")
    private Double precioTotal;

    @Field("estado")
    private EstadoBillete estado;

    @Field("fecha_compra")
    private LocalDateTime fechaCompra;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum EstadoBillete {
        COMPRADO,
        CANCELADO,
        USADO
    }
}