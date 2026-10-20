package pe.soporte.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Solicitud de soporte técnico registrada por un cliente.
 * Se construye en la capa de servicio a partir de un SolicitudRequest validado.
 */
@Schema(description = "Solicitud de soporte técnico")
public class Solicitud {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "No hay conexión a internet en el área de ventas")
    private String titulo;

    @Schema(example = "Desde las 9:00 a.m. las 5 PCs del área de ventas no tienen acceso a internet.")
    private String descripcion;

    @Schema(example = "ALTA")
    private Prioridad prioridad;

    @Schema(example = "ABIERTA")
    private EstadoSolicitud estado;

    @Schema(example = "1")
    private Long clienteId;

    @Schema(description = "Técnico asignado (puede ser nulo)", example = "2")
    private Long tecnicoId;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public Solicitud() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
