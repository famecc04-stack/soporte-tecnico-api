package pe.soporte.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Cliente que registra solicitudes de soporte técnico. */
@Schema(description = "Cliente de la empresa que solicita soporte técnico")
public class Cliente {

    @Schema(description = "Identificador generado por el sistema", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(example = "María López")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Schema(example = "maria.lopez@comercialandina.pe")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    @Schema(example = "987654321")
    private String telefono;

    @Size(max = 100, message = "La empresa no puede superar los 100 caracteres")
    @Schema(example = "Comercial Andina S.A.C.")
    private String empresa;

    public Cliente() {
    }

    public Cliente(Long id, String nombre, String email, String telefono, String empresa) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.empresa = empresa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
}
