package pe.soporte.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.soporte.api.model.EstadoSolicitud;
import pe.soporte.api.model.Prioridad;

/**
 * Datos de entrada para registrar (POST) o actualizar (PUT) una solicitud.
 * Separar el DTO del modelo evita que el cliente envíe campos que controla
 * el sistema (id, fechas).
 */
@Schema(description = "Datos para registrar o actualizar una solicitud de soporte")
public class SolicitudRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 120, message = "El título debe tener entre 5 y 120 caracteres")
    @Schema(example = "No hay conexión a internet en el área de ventas")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    @Schema(example = "Desde las 9:00 a.m. las 5 PCs del área de ventas no tienen acceso a internet.")
    private String descripcion;

    @NotNull(message = "La prioridad es obligatoria (BAJA, MEDIA, ALTA, CRITICA)")
    @Schema(example = "ALTA")
    private Prioridad prioridad;

    @NotNull(message = "El id del cliente es obligatorio")
    @Positive(message = "El id del cliente debe ser un número positivo")
    @Schema(example = "1")
    private Long clienteId;

    @Positive(message = "El id del técnico debe ser un número positivo")
    @Schema(description = "Opcional. Técnico asignado a la solicitud", example = "2")
    private Long tecnicoId;

    @Schema(description = "Opcional y solo para PUT. Al registrar, la solicitud siempre inicia en ABIERTA",
            example = "EN_PROCESO")
    private EstadoSolicitud estado;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}
