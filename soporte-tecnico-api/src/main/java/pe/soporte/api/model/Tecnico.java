package pe.soporte.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Técnico encargado de atender las solicitudes de soporte. */
@Schema(description = "Técnico que atiende solicitudes de soporte")
public class Tecnico {

    @Schema(description = "Identificador generado por el sistema", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(example = "Carlos Ramírez")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Schema(example = "carlos.ramirez@soportetech.pe")
    private String email;

    @NotBlank(message = "La especialidad es obligatoria")
    @Schema(example = "Redes y conectividad")
    private String especialidad;

    @NotNull(message = "Debe indicar si el técnico está disponible")
    @Schema(example = "true")
    private Boolean disponible;

    public Tecnico() {
    }

    public Tecnico(Long id, String nombre, String email, String especialidad, Boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.especialidad = especialidad;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
