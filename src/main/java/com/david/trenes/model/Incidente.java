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
@Document(collection = "incidentes")
public class Incidente {
    
    @Id
    private String id;
    
    @Field("codigo_incidente")
    private String codigoIncidente;
    
    @Field("titulo")
    private String titulo;
    
    @Field("descripcion")
    private String descripcion;
    
    @Field("tipo_incidente")
    private TipoIncidente tipoIncidente;
    
    @Field("severidad")
    private SeveridadIncidente severidad;
    
    @Field("estado")
    private EstadoIncidente estado;
    
    @Field("ubicacion")
    private Via.Coordenada ubicacion;
    
    @Field("ubicacion_descripcion")
    private String ubicacionDescripcion;
    
    @Field("elementos_afectados")
    private List<ElementoAfectado> elementosAfectados;
    
    @Field("fecha_hora")
    private LocalDateTime fechaHora;
    
    @Field("fecha_deteccion")
    private LocalDateTime fechaDeteccion;
    
    @Field("fecha_inicio")
    private LocalDateTime fechaInicio;
    
    @Field("fecha_fin_estimada")
    private LocalDateTime fechaFinEstimada;
    
    @Field("fecha_fin_real")
    private LocalDateTime fechaFinReal;
    
    @Field("duracion_minutos")
    private Integer duracionMinutos;
    
    @Field("reportado_por")
    private String reportadoPor;
    
    @Field("rol_reportante")
    private String rolReportante;
    
    @Field("contacto_reportante")
    private String contactoReportante;
    
    @Field("operador_asignado")
    private String operadorAsignado;
    
    @Field("equipos_responsables")
    private List<String> equiposResponsables;
    
    @Field("impacto_operativo")
    private ImpactoIncidente impactoOperativo;
    
    @Field("acciones_tomadas")
    private List<AccionIncidente> accionesTomadas;
    
    @Field("comunicaciones")
    private List<ComunicacionIncidente> comunicaciones;
    
    @Field("recursos_asignados")
    private List<RecursoIncidente> recursosAsignados;
    
    @Field("costo_estimado")
    private Double costoEstimado;
    
    @Field("costo_real")
    private Double costoReal;
    
    @Field("causa_raiz")
    private String causaRaiz;
    
    @Field("lecciones_aprendidas")
    private String leccionesAprendidas;
    
    @Field("documentos_adjuntos")
    private List<String> documentosAdjuntos;
    
    @Field("incidentes_relacionados")
    private List<String> incidentesRelacionados;
    
    @Field("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public enum TipoIncidente {
        ACCIDENTE_TREN,
        DESCARRILAMIENTO,
        COLISION,
        FALLO_TECNICO,
        FALLO_SEÑALIZACION,
        OBSTRUCCION_VIA,
        MAL_TIEMPO,
        INCENDIO,
        EMERGENCIA_MEDICA,
        SEGURIDAD,
        VANDALISMO,
        ROBO,
        FUGA_MATERIALES,
        OBRA_VIA,
        FALLO_COMUNICACIONES,
        FALLO_ELECTRICO,
        OTRO
    }
    
    public enum SeveridadIncidente {
        BAJA,      // Impacto mínimo
        MEDIA,     // Impacto moderado
        ALTA,      // Impacto significativo
        CRITICA,   // Impacto severo
        CATASTROFICA // Impacto masivo
    }
    
    public enum EstadoIncidente {
        REPORTADO,
        CONFIRMADO,
        EN_PROGRESO,
        CONTENIDO,
        RESUELTO,
        CERRADO,
        FALSO_ALARMA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ElementoAfectado {
        private String tipo; // VIA, ESTACION, TREN, SEÑAL, etc.
        private String id;
        private String nombre;
        private String descripcionImpacto;
        private Integer porcentajeImpacto;
        private LocalDateTime fechaAfectacion;
        private LocalDateTime fechaRecuperacion;
        private String estadoActual;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImpactoIncidente {
        private Boolean afectaServicio;
        private Integer serviciosAfectados;
        private Integer pasajerosAfectados;
        private Integer trenesAfectados;
        private Integer estacionesAfectadas;
        private Integer viasAfectadas;
        private List<String> rutasInterrumpidas;
        private Integer retrasoPromedioMinutos;
        private String descripcionImpacto;
        private Double perdidaEconomicaEstimada;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccionIncidente {
        private String id;
        private String descripcion;
        private String tipoAccion;
        private String responsable;
        private LocalDateTime fechaProgramada;
        private LocalDateTime fechaEjecucion;
        private EstadoAccion estado;
        private String resultado;
        private String observaciones;
    }
    
    public enum EstadoAccion {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA,
        POSPUESTA
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComunicacionIncidente {
        private String id;
        private String tipo; // INTERNA, EXTERNA, EMERGENCIA
        private String canal; // RADIO, TELEFONO, EMAIL, SISTEMA
        private String destinatario;
        private String mensaje;
        private LocalDateTime fecha;
        private String remitente;
        private Boolean recibida;
        private Boolean confirmada;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecursoIncidente {
        private String id;
        private String tipo; // PERSONAL, EQUIPO, VEHICULO, MATERIAL
        private String nombre;
        private String descripcion;
        private Integer cantidad;
        private LocalDateTime fechaAsignacion;
        private LocalDateTime fechaLiberacion;
        private String responsable;
        private String estado;
    }
}
