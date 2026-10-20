package pe.soporte.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Cuerpo de PATCH /api/solicitudes/{id}/tecnico */
@Schema(description = "Técnico que se asignará a la solicitud")
public class AsignacionTecnicoRequest {

    @NotNull(message = "El id del técnico es obligatorio")
    @Positive(message = "El id del técnico debe ser un número positivo")
    @Schema(example = "1")
    private Long tecnicoId;

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long tecnicoId) { this.tecnicoId = tecnicoId; }
}
