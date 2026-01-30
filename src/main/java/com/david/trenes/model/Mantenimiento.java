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
@Document(collection = "mantenimientos")
public class Mantenimiento {
    
    @Id
    private String id;
    
    @Field("codigo_mantenimiento")
    private String codigoMantenimiento;
    
    @Field("tipo_elemento")
    private TipoElemento tipoElemento;
    
    @Field("elemento_id")
    private String elementoId;
    
    @Field("nombre_elemento")
    private String nombreElemento;
    
    @Field("tipo_mantenimiento")
    private TipoMantenimiento tipoMantenimiento;
    
    @Field("prioridad")
    private PrioridadMantenimiento prioridad;
    
    @Field("estado")
    private EstadoMantenimiento estado;
    
    @Field("fecha_programada")
    private LocalDateTime fechaProgramada;
    
    @Field("fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Field("fecha_fin")
    private LocalDateTime fechaFin;
    
    @Field("duracion_estimada_horas")
    private Integer duracionEstimadaHoras;
    
    @Field("duracion_real_horas")
    private Integer duracionRealHoras;
    
    @Field("descripcion")
    private String descripcion;
    
    @Field("tareas")
    private List<TareaMantenimiento> tareas;
    
    @Field("recursos")
    private List<RecursoMantenimiento> recursos;
    
    @Field("tecnico_principal_id")
    private String tecnicoPrincipalId;
    
    @Field("tecnicos_secundarios")
    private List<String> tecnicosSecundarios;
    
    @Field("costo_estimado")
    private Double costoEstimado;
    
    @Field("costo_real")
    private Double costoReal;
    
    @Field("materiales")
    private List<MaterialMantenimiento> materiales;
    
    @Field("impacto_operativo")
    private ImpactoOperativo impactoOperativo;
    
    @Field("resultados")
    private List<ResultadoMantenimiento> resultados;
    
    @Field("observaciones")
    private String observaciones;
    
    @Field("documentos_adjuntos")
    private List<String> documentosAdjuntos;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoElemento {
        VIA,
        ESTACION,
        TREN,
        LOCOMOTORA,
        VAGON,
        SEÑAL,
        COMUNICACION,
        ELECTRICO,
        ANDEN,
        TUNEL,
        PUENTE
    }
    
    public enum TipoMantenimiento {
        PREVENTIVO,
        CORRECTIVO,
        EMERGENCIA,
        PREDICTIVO,
        REVISION_GENERAL,
        REVISION_ESPECIFICA,
        MODERNIZACION,
        REPARACION
    }
    
    public enum PrioridadMantenimiento {
        BAJA,
        MEDIA,
        ALTA,
        CRITICA,
        EMERGENCIA
    }
    
    public enum EstadoMantenimiento {
        PROGRAMADO,
        EN_PROGRESO,
        PAUSADO,
        COMPLETADO,
        CANCELADO,
        POSPUESTO,
        INSPECCIONADO
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TareaMantenimiento {
        private String id;
        private String descripcion;
        private String tipo;
        private Integer duracionEstimadaMinutos;
        private Integer duracionRealMinutos;
        private EstadoTarea estado;
        private String tecnicoAsignado;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String observaciones;
    }
    
    public enum EstadoTarea {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecursoMantenimiento {
        private String id;
        private String tipo;
        private String nombre;
        private String descripcion;
        private Integer cantidad;
        private Boolean disponible;
        private LocalDateTime fechaAsignacion;
        private LocalDateTime fechaLiberacion;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaterialMantenimiento {
        private String id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String unidad;
        private Double cantidad;
        private Double costoUnitario;
        private Double costoTotal;
        private String proveedor;
        private LocalDateTime fechaUso;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImpactoOperativo {
        private Boolean afectaServicio;
        private String descripcionImpacto;
        private List<String> rutasAfectadas;
        private List<String> trenesAfectados;
        private Integer duracionInterrupcionMinutos;
        private String medidasAlternativas;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResultadoMantenimiento {
        private String id;
        private String tipoResultado;
        private String descripcion;
        private String valor;
        private String unidad;
        private LocalDateTime fechaMedicion;
        private String tecnicoResponsable;
    }
}
