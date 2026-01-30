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
@Document(collection = "vias")
public class Via {
    
    @Id
    private String id;
    
    @Field("codigo_via")
    private String codigoVia;
    
    @Field("nombre")
    private String nombre;
    
    @Field("descripcion")
    private String descripcion;
    
    @Field("longitud_km")
    private Double longitudKm;
    
    @Field("tipo_via")
    private TipoVia tipoVia;
    
    @Field("estado")
    private EstadoVia estado;
    
    @Field("coordenadas_inicio")
    private Coordenada coordenadaInicio;
    
    @Field("coordenadas_fin")
    private Coordenada coordenadaFin;
    
    @Field("estacion_origen_id")
    private String estacionOrigenId;
    
    @Field("estacion_destino_id")
    private String estacionDestinoId;
    
    @Field("velocidad_maxima")
    private Integer velocidadMaxima;
    
    @Field("ancho_via")
    private Double anchoVia;
    
    @Field("electrificada")
    private Boolean electrificada;
    
    @Field("voltaje")
    private Integer voltaje;
    
    @Field("fecha_construccion")
    private LocalDateTime fechaConstruccion;
    
    @Field("fecha_ultima_mantenimiento")
    private LocalDateTime fechaUltimaMantenimiento;
    
    @Field("proxima_mantenimiento")
    private LocalDateTime proximaMantenimiento;
    
    @Field("segmentos")
    private List<SegmentoVia> segmentos;
    
    @Field("señales")
    private List<String> señalIds;
    
    @Field("activo")
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoVia {
        PRINCIPAL,
        SECUNDARIA,
        DESVIO,
        VIA_MUERTA,
        ANDEN,
        YARDO
    }
    
    public enum EstadoVia {
        OPERATIVA,
        MANTENIMIENTO,
        BLOQUEADA,
        CONSTRUCCION,
        EMERGENCIA,
        DESACTIVADA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Coordenada {
        private Double latitud;
        private Double longitud;
        private Double altitud;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SegmentoVia {
        private String id;
        private Double longitudKm;
        private Double kilometroInicio;
        private Double kilometroFin;
        private EstadoVia estado;
        private LocalDateTime fechaUltimaInspeccion;
        private String observaciones;
    }
}
