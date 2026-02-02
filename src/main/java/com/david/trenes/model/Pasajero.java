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
@Document(collection = "pasajeros")
public class Pasajero {

    @Id
    private String id;

    @Field("usuario_id")
    private String usuarioId; // opcional: vínculo a Usuario

    @Field("nombre")
    private String nombre;

    @Field("apellidos")
    private String apellidos;

    @Field("documento")
    private String documento; // DNI/NIE/Pasaporte (sin validaciones estrictas por ahora)

    @Field("email")
    private String email;

    @Field("telefono")
    private String telefono;

    @Field("activo")
    private Boolean activo;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
