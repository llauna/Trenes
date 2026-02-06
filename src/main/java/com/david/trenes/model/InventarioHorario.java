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
@Document(collection = "inventario_horario")
public class InventarioHorario {

    @Id
    private String horarioId; // usamos el horarioId como _id para acceso directo

    @Field("tren_id")
    private String trenId;

    @Field("capacidad_turista")
    private Integer capacidadTurista;

    @Field("capacidad_primera")
    private Integer capacidadPrimera;

    @Field("vendidos_turista")
    private Integer vendidosTurista;

    @Field("vendidos_primera")
    private Integer vendidosPrimera;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
