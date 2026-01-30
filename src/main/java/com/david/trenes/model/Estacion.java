package com.david.trenes.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "estaciones")
public class Estacion {
    
    @Id
    private String id;
    
    @Field("codigo_estacion")
    private String codigoEstacion;
    
    @Field("nombre")
    private String nombre;
    
    @Field("tipo_estacion")
    private TipoEstacion tipoEstacion;
    
    @Field("categoria")
    private CategoriaEstacion categoria;
    
    @Field("ubicacion")
    private Via.Coordenada ubicacion;
    
    @Field("direccion")
    private String direccion;
    
    @Field("ciudad")
    private String ciudad;
    
    @Field("provincia")
    private String provincia;
    
    @Field("codigo_postal")
    private String codigoPostal;
    
    @Field("telefono")
    private String telefono;
    
    @Field("email")
    private String email;
    
    @Field("andenes")
    private List<Anden> andenes;
    
    @Field("vias_conectadas")
    private List<String> viaIds;
    
    @Field("servicios")
    private List<ServicioEstacion> servicios;
    
    @Field("horario_apertura")
    private String horarioApertura;
    
    @Field("horario_cierre")
    private String horarioCierre;
    
    @Field("accesibilidad")
    private Boolean accesibilidad;
    
    @Field("estacionamiento")
    private Integer capacidadEstacionamiento;
    
    @Field("personal_activo")
    private Integer personalActivo;
    
    @Field("supervisor_id")
    private String supervisorId;
    
    @Field("activo")
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoEstacion {
        TERMINAL,
        PASAJEROS,
        CARGA,
        MIXTA,
        DESVIO,
        PARADA
    }
    
    public enum CategoriaEstacion {
        A, // Principal - Alta demanda
        B, // Secundaria - Media demanda
        C, // Local - Baja demanda
        D, // Rural - Muy baja demanda
        E  // Industrial - Carga
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Anden {
        private String id;
        private String numero;
        private Double longitud;
        private Double altura;
        private TipoAnden tipo;
        private Boolean cubierto;
        private Boolean activo;
        private String viaAsignadaId;
    }
    
    public enum TipoAnden {
        PASAJEROS,
        CARGA,
        MIXTO,
        ESPECIAL
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicioEstacion {
        private String id;
        private TipoServicio tipo;
        private String descripcion;
        private Boolean disponible;
        private String horario;
    }
    
    public enum TipoServicio {
        TAQUILLA,
        INFORMACION,
        EQUIPAJE,
        RESTAURANTE,
        TIENDA,
        BANCO,
        WIFI,
        SALA_ESPERA,
        BANOS,
        ACCESIBILIDAD,
        ESTACIONAMIENTO,
        TRANSPORTE_PUBLICO
    }
}
