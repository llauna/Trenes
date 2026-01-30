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
@Document(collection = "señales")
public class Signal {
    
    @Id
    private String id;
    
    @Field("codigo_señal")
    private String codigoSignal;
    
    @Field("nombre")
    private String nombre;
    
    @Field("tipo_señal")
    private TipoSignal tipoSignal;
    
    @Field("categoria")
    private CategoriaSignal categoria;
    
    @Field("ubicacion")
    private Via.Coordenada ubicacion;
    
    @Field("via_id")
    private String viaId;
    
    @Field("kilometro")
    private Double kilometro;
    
    @Field("orientacion")
    private OrientacionSignal orientacion;
    
    @Field("estado_actual")
    private EstadoSignal estadoActual;
    
    @Field("estado_anterior")
    private EstadoSignal estadoAnterior;
    
    @Field("configuracion")
    private ConfiguracionSignal configuracion;
    
    @Field("controlado_por")
    private String controladoPor; // Centro de control o post
    @Field("comunicaciones")
    private List<ComunicacionSignal> comunicaciones;
    
    @Field("alimentacion")
    private AlimentacionSignal alimentacion;
    
    @Field("mantenimiento")
    private MantenimientoSignal mantenimiento;
    
    @Field("incidencias")
    private List<IncidenciaSignal> incidencias;
    
    @Field("historial_estados")
    private List<HistorialEstado> historialEstados;
    
    @Field("activo")
    private Boolean activo;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoSignal {
        ENTRADA,
        SALIDA,
        BLOQUEO,
        AVANZADA,
        PROTECCION,
        MANIOBRA,
        PERMISO,
        RESTRICCION,
        INFORMATIVA,
        ADVERTENCIA,
        CRUZAMIENTO,
        PASO_A_NIVEL
    }
    
    public enum CategoriaSignal {
        LUMINOSA,
        MECANICA,
        SEMAFORICA,
        ACUSTICA,
        DIGITAL,
        LED,
        FIBRA_OPTICA
    }
    
    public enum OrientacionSignal {
        IZQUIERDA,
        DERECHA,
        CENTRAL,
        DOBLE,
        MULTIPLE
    }
    
    public enum EstadoSignal {
        VERDE,    // Vía libre
        AMARILLO, // Precaución
        ROJO,     // Alto
        AMARILLO_INTERMITENTE, // Anuncio de parada
        BLANCO,   // Apagada o sin efecto
        LUNETA,   // Rebase autorizado
        CUADRADOS, // Restricción de velocidad
        CRUZ,     // Anuncio de precaución
        APAGADA,  // Fuera de servicio
        EMERGENCIA // Modo de emergencia
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfiguracionSignal {
        private Boolean automatizada;
        private Boolean manual;
        private Boolean remota;
        private Boolean local;
        private Integer velocidadLimitante;
        private String tipoControl;
        private List<String> signalsDependientes;
        private List<String> signalsRelacionadas;
        private Integer tiempoRespuestaSegundos;
        private String protocoloComunicacion;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComunicacionSignal {
        private String tipo;
        private String protocolo;
        private String direccion;
        private Integer puerto;
        private Boolean activa;
        private LocalDateTime ultimaComunicacion;
        private String estadoConexion;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlimentacionSignal {
        private String tipo; // ELECTRICA, SOLAR, BATERIA
        private Integer voltaje;
        private Integer amperaje;
        private Boolean redundancia;
        private Boolean ups;
        private Integer autonomiaHoras;
        private LocalDateTime ultimaRevision;
        private String estadoBateria;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MantenimientoSignal {
        private LocalDateTime fechaUltimaInspeccion;
        private LocalDateTime proximaInspeccion;
        private String tecnicoResponsable;
        private Integer diasSinIncidencias;
        private String estadoGeneral;
        private List<String> componentesRevisados;
        private String observaciones;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IncidenciaSignal {
        private String id;
        private LocalDateTime fecha;
        private TipoIncidencia tipo;
        private String descripcion;
        private EstadoSignal estadoFallido;
        private Boolean resuelta;
        private LocalDateTime fechaResolucion;
        private String solucion;
        private String tecnicoResponsable;
    }
    
    public enum TipoIncidencia {
        FALLO_ALIMENTACION,
        FALLO_COMUNICACION,
        FALLO_LAMPARA,
        FALLO_SENSOR,
        FALLO_CONTROL,
        DANIO_FISICO,
        INTERFERENCIA,
        CALIBRACION,
        OTRO
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistorialEstado {
        private LocalDateTime fecha;
        private EstadoSignal estado;
        private String causa;
        private String operador;
        private String origenCambio; // AUTOMATICO, MANUAL, REMOTO
    }
}
