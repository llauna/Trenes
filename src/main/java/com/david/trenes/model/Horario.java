package com.david.trenes.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "horarios")
public class Horario {

    @Id
    private String id;

    @NotBlank(message = "codigoServicio es obligatorio")
    @Field("codigo_servicio")
    private String codigoServicio;

    @NotBlank(message = "trenId es obligatorio")
    @Field("tren_id")
    private String trenId;

    @NotBlank(message = "rutaId es obligatorio")
    @Field("ruta_id")
    private String rutaId;

    @Field("numero_servicio")
    private String numeroServicio;

    @NotNull(message = "tipoServicio es obligatorio")
    @Field("tipo_servicio")
    private TipoServicio tipoServicio;

    @NotNull(message = "fechaSalida es obligatoria")
    @Field("fecha_salida")
    private LocalDateTime fechaSalida;

    @NotNull(message = "fechaLlegada es obligatoria")
    @Field("fecha_llegada")
    private LocalDateTime fechaLlegada;

    @NotBlank(message = "estacionOrigenId es obligatorio")
    @Field("estacion_origen_id")
    private String estacionOrigenId;

    @NotBlank(message = "estacionDestinoId es obligatorio")
    @Field("estacion_destino_id")
    private String estacionDestinoId;

    @Builder.Default
    @Field("paradas")
    private List<ParadaHorario> paradas = new ArrayList<>();

    @Field("conductor_id")
    private String conductorId;

    @Field("conductor_suplente_id")
    private String conductorSuplenteId;

    @NotNull(message = "estado es obligatorio")
    @Field("estado")
    private EstadoHorario estado;

    @Field("frecuencia")
    private FrecuenciaHorario frecuencia;

    @NotNull(message = "capacidadPasajeros es obligatoria")
    @Positive(message = "capacidadPasajeros debe ser > 0")
    @Field("capacidad_pasajeros")
    private Integer capacidadPasajeros;

    @Builder.Default
    @PositiveOrZero(message = "pasajerosActuales debe ser >= 0")
    @Field("pasajeros_actuales")
    private Integer pasajerosActuales = 0;

    @Builder.Default
    @PositiveOrZero(message = "ocupacionPorcentaje debe ser >= 0")
    @Field("ocupacion_porcentaje")
    private Double ocupacionPorcentaje = 0.0;

    @NotNull(message = "tarifa es obligatoria")
    @PositiveOrZero(message = "tarifa debe ser >= 0")
    @Field("tarifa")
    private Double tarifa;

    @Builder.Default
    @Field("clases")
    private List<ClaseServicio> clases = new ArrayList<>();

    @Field("observaciones")
    private String observaciones;

    @Builder.Default
    @Field("incidencias")
    private List<IncidenciaHorario> incidencias = new ArrayList<>();

    @Builder.Default
    @NotNull(message = "activo es obligatorio")
    @Field("activo")
    private Boolean activo = Boolean.TRUE;

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
