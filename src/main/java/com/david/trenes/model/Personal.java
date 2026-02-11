package com.david.trenes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "personal")
public class Personal {

    @Id
    private String id;

    @Field("usuario_id")
    private String usuarioId;

    @Field("nombre")
    private String nombre;

    @Field("apellidos")
    private String apellidos;

    @Indexed(unique = true)
    @Field("documento")
    private String documento; // DNI/NIE/Pasaporte

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Indexed(unique = true)
    @Field("telefono")
    private String telefono;

    @Field("tipo_personal")
    private TipoPersonal tipoPersonal; // CONDUCTOR, CABINA, RESTAURANTE

    @Field("numero_empleado")
    private String numeroEmpleado; // Número único de empleado

    @Field("licencia_conducir")
    private String licenciaConducir; // Solo para conductores

    @Field("especialidad")
    private String especialidad; // Para personal de cabina/restaurant

    @Field("activo")
    private Boolean activo;

    @Field("fecha_contratacion")
    private LocalDateTime fechaContratacion;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum TipoPersonal {
        CONDUCTOR,
        CABINA,
        RESTAURANTE
    }
}
