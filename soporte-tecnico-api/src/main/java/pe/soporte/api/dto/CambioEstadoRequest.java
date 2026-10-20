package pe.soporte.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import pe.soporte.api.model.EstadoSolicitud;

/** Cuerpo de PATCH /api/solicitudes/{id}/estado */
@Schema(description = "Nuevo estado de la solicitud")
public class CambioEstadoRequest {

    @NotNull(message = "El estado es obligatorio (ABIERTA, EN_PROCESO, RESUELTA, CERRADA)")
    @Schema(example = "EN_PROCESO")
    private EstadoSolicitud estado;

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
}
