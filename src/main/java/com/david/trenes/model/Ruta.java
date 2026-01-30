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
@Document(collection = "rutas")
public class Ruta {
    
    @Id
    private String id;
    
    @Field("codigo_ruta")
    private String codigoRuta;
    
    @Field("nombre")
    private String nombre;
    
    @Field("descripcion")
    private String descripcion;
    
    @Field("tipo_ruta")
    private TipoRuta tipoRuta;
    
    @Field("estacion_origen_id")
    private String estacionOrigenId;
    
    @Field("estacion_destino_id")
    private String estacionDestinoId;
    
    @Field("estaciones_intermedias")
    private List<ParadaRuta> estacionesIntermedias;
    
    @Field("vias")
    private List<ViaRuta> vias;
    
    @Field("distancia_total_km")
    private Double distanciaTotalKm;
    
    @Field("tiempo_estimado_minutos")
    private Integer tiempoEstimadoMinutos;
    
    @Field("velocidad_promedio")
    private Double velocidadPromedio;
    
    @Field("estado")
    private EstadoRuta estado;
    
    @Field("frecuencia")
    private FrecuenciaRuta frecuencia;
    
    @Field("prioridad")
    private Integer prioridad;
    
    @Field("restricciones")
    private List<RestriccionRuta> restricciones;
    
    @Field("tarifa_base")
    private Double tarifaBase;
    
    @Field("zonas")
    private List<String> zonas;
    
    @Field("activo")
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoRuta {
        EXPRESO,
        REGIONAL,
        CERCANIAS,
        CARGA,
        TURISTICO,
        INTERNACIONAL,
        ESPECIAL
    }
    
    public enum EstadoRuta {
        ACTIVA,
        INACTIVA,
        MANTENIMIENTO,
        SUSPENDIDA,
        EMERGENCIA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParadaRuta {
        private String estacionId;
        private String nombreEstacion;
        private Integer orden;
        private Double kilometro;
        private Integer tiempoParadaMinutos;
        private Boolean obligatoria;
        private LocalDateTime horaLlegada;
        private LocalDateTime horaSalida;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ViaRuta {
        private String viaId;
        private String nombreVia;
        private Integer orden;
        private Double kilometroInicio;
        private Double kilometroFin;
        private Double distancia;
        private String estacionOrigenId;
        private String estacionDestinoId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrecuenciaRuta {
        private Boolean lunes;
        private Boolean martes;
        private Boolean miercoles;
        private Boolean jueves;
        private Boolean viernes;
        private Boolean sabado;
        private Boolean domingo;
        private Boolean festivos;
        private Integer serviciosDia;
        private String horaPrimeraSalida;
        private String horaUltimaSalida;
        private Integer intervaloMinutos;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RestriccionRuta {
        private TipoRestriccion tipo;
        private String descripcion;
        private String valor;
        private Boolean activa;
    }
    
    public enum TipoRestriccion {
        VELOCIDAD_MAXIMA,
        PESO_MAXIMO,
        TIPO_TREN,
        HORARIO,
        MANTENIMIENTO,
        METEOROLOGICA,
        SEGURIDAD
    }
}
