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
@Document(collection = "trenes")
public class Tren {

    @Id
    private String id;

    @Field("numero_tren")
    private String numeroTren;

    @Field("matricula")
    private String matricula;

    @Field("tipo_tren")
    private TipoTren tipoTren;

    @Field("modelo")
    private String modelo;

    @Field("fabricante")
    private String fabricante;

    @Field("año_fabricacion")
    private Integer añoFabricacion;

    @Field("capacidad_pasajeros")
    private Integer capacidadPasajeros;

    @Field("capacidad_carga")
    private Double capacidadCarga;

    @Field("velocidad_maxima")
    private Integer velocidadMaxima;

    @Field("locomotoras")
    private List<Locomotora> locomotoras;

    @Field("vagones")
    private List<Vagon> vagones;

    @Field("estado_actual")
    private EstadoTren estadoActual;

    @Field("ubicacion_actual")
    private Via.Coordenada ubicacionActual;

    @Field("via_actual_id")
    private String viaActualId;

    @Field("kilometro_actual")
    private Double kilometroActual;

    @Field("ruta_actual_id")
    private String rutaActualId;

    @Field("conductor_actual_id")
    private String conductorActualId;

    @Field("estacion_actual_id")
    private String estacionActualId;

    // --- NUEVO: datos mínimos para simular el avance automáticamente ---
    @Field("fecha_inicio_viaje")
    private LocalDateTime fechaInicioViaje;

    @Field("velocidad_crucero_kmh")
    private Double velocidadCruceroKmh;

    @Field("fecha_ultima_revision")
    private LocalDateTime fechaUltimaRevision;

    @Field("proxima_revision")
    private LocalDateTime proximaRevision;

    @Field("kilometraje_total")
    private Double kilometrajeTotal;

    @Field("kilometros_ultima_revision")
    private Double kilometrosUltimaRevision;

    @Field("mantenimientos")
    private List<Mantenimiento> mantenimientos;

    @Field("incidencias")
    private List<Incidencia> incidencias;

    @Field("caracteristicas")
    private CaracteristicasTren caracteristicas;

    @Field("activo")
    private Boolean activo;

    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum TipoTren {
        ALTA_VELOCIDAD,
        REGIONAL,
        CERCANIAS,
        CARGA,
        MIXTO,
        TURISTICO,
        ESPECIAL
    }

    public enum EstadoTren {
        EN_MARCHA,
        DETENIDO,
        EN_ESTACION,
        MANTENIMIENTO,
        FUERA_SERVICIO,
        EMERGENCIA,
        EN_RUTA,
        FINALIZADO
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Locomotora {
        private String id;
        private String matricula;
        private String modelo;
        private Integer potencia;
        private String tipoCombustible;
        private Integer añoFabricacion;
        private EstadoLocomotora estado;
        private Double kilometraje;
    }

    public enum EstadoLocomotora {
        OPERATIVA,
        MANTENIMIENTO,
        REPARACION,
        FUERA_SERVICIO
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Vagon {
        private String id;
        private String numero;
        private TipoVagon tipo;
        private Double capacidad;
        private Integer capacidadPasajeros;
        private Double capacidadCarga;
        private Boolean activo;
        private String observaciones;
    }

    public enum TipoVagon {
        PASAJEROS_PRIMERA,
        PASAJEROS_TURISTA,
        RESTAURANTE,
        CAFETERIA,
        DORMITORIO,
        CARGA_CERRADO,
        CARGA_ABIERTO,
        CISTERNA,
        FRIGORIFICO,
        AUTOS,
        EQUIPAJE
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Mantenimiento {
        private String id;
        private LocalDateTime fecha;
        private TipoMantenimiento tipo;
        private String descripcion;
        private Double costo;
        private String tecnicoResponsable;
        private Integer duracionHoras;
        private String observaciones;
    }

    public enum TipoMantenimiento {
        PREVENTIVO,
        CORRECTIVO,
        EMERGENCIA,
        REVISION_GENERAL,
        REVISION_ESPECIFICA
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Incidencia {
        private String id;
        private LocalDateTime fecha;
        private TipoIncidencia tipo;
        private String descripcion;
        private String ubicacion;
        private Boolean resuelta;
        private LocalDateTime fechaResolucion;
        private String solucion;
    }

    public enum TipoIncidencia {
        MECANICA,
        ELECTRICA,
        NEUMATICA,
        FRENOS,
        COMUNICACION,
        CLIMATIZACION,
        SEGURIDAD,
        OTRO
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaracteristicasTren {
        private Boolean aireAcondicionado;
        private Boolean wifi;
        private Boolean accesibilidad;
        private Boolean restaurante;
        private Boolean cafeteria;
        private Boolean banos;
        private Boolean sistemaAudio;
        private Boolean pantallas;
        private Integer numeroPuertas;
        private String sistemaFrenos;
        private String sistemaSenalizacion;
    }
}
