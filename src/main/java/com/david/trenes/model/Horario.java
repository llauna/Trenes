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
@Document(collection = "horarios")
public class Horario {
    
    @Id
    private String id;
    
    @Field("codigo_servicio")
    private String codigoServicio;
    
    @Field("tren_id")
    private String trenId;
    
    @Field("ruta_id")
    private String rutaId;
    
    @Field("numero_servicio")
    private String numeroServicio;
    
    @Field("tipo_servicio")
    private TipoServicio tipoServicio;
    
    @Field("fecha_salida")
    private LocalDateTime fechaSalida;
    
    @Field("fecha_llegada")
    private LocalDateTime fechaLlegada;
    
    @Field("estacion_origen_id")
    private String estacionOrigenId;
    
    @Field("estacion_destino_id")
    private String estacionDestinoId;
    
    @Field("paradas")
    private List<ParadaHorario> paradas;
    
    @Field("conductor_id")
    private String conductorId;
    
    @Field("conductor_suplente_id")
    private String conductorSuplenteId;
    
    @Field("estado")
    private EstadoHorario estado;
    
    @Field("frecuencia")
    private FrecuenciaHorario frecuencia;
    
    @Field("capacidad_pasajeros")
    private Integer capacidadPasajeros;
    
    @Field("pasajeros_actuales")
    private Integer pasajerosActuales;
    
    @Field("ocupacion_porcentaje")
    private Double ocupacionPorcentaje;
    
    @Field("tarifa")
    private Double tarifa;
    
    @Field("clases")
    private List<ClaseServicio> clases;
    
    @Field("observaciones")
    private String observaciones;
    
    @Field("incidencias")
    private List<IncidenciaHorario> incidencias;
    
    @Field("activo")
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoServicio {
        REGULAR,
        ESPECIAL,
        EXTRAORDINARIO,
        MANTENIMIENTO,
        CARGA,
        TURISTICO
    }
    
    public enum EstadoHorario {
        PROGRAMADO,
        EN_MARCHA,
        RETRASADO,
        CANCELADO,
        COMPLETADO,
        SUSPENDIDO,
        EMERGENCIA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParadaHorario {
        private String estacionId;
        private String nombreEstacion;
        private Integer orden;
        private LocalDateTime horaLlegadaProgramada;
        private LocalDateTime horaSalidaProgramada;
        private LocalDateTime horaLlegadaReal;
        private LocalDateTime horaSalidaReal;
        private Integer tiempoParadaMinutos;
        private Boolean paradaObligatoria;
        private String andenAsignado;
        private EstadoParada estado;
        private Integer retrasoMinutos;
    }
    
    public enum EstadoParada {
        PENDIENTE,
        REALIZADA,
        OMITIDA,
        RETRASADA,
        CANCELADA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrecuenciaHorario {
        private Boolean lunes;
        private Boolean martes;
        private Boolean miercoles;
        private Boolean jueves;
        private Boolean viernes;
        private Boolean sabado;
        private Boolean domingo;
        private Boolean festivos;
        private LocalDateTime fechaInicioVigencia;
        private LocalDateTime fechaFinVigencia;
        private String periodicidad; // DIARIO, SEMANAL, MENSUAL
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClaseServicio {
        private String nombre;
        private Integer capacidad;
        private Double tarifa;
        private Integer pasajerosActuales;
        private Boolean disponible;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IncidenciaHorario {
        private String id;
        private LocalDateTime fecha;
        private TipoIncidencia tipo;
        private String descripcion;
        private String estacionId;
        private Integer retrasoMinutos;
        private Boolean resuelta;
        private LocalDateTime fechaResolucion;
        private String solucion;
    }
    
    public enum TipoIncidencia {
        RETRASO,
        CANCELACION,
        EMERGENCIA_MEDICA,
        PROBLEMA_TECNICO,
        MAL_TIEMPO,
        OBRA_VIA,
        PROBLEMA_SENALIZACION,
        OTRO
    }
}
