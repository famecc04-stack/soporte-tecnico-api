package pe.soporte.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.soporte.api.dto.ErrorResponse;
import pe.soporte.api.model.Tecnico;
import pe.soporte.api.service.TecnicoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
@Tag(name = "Técnicos", description = "Gestión de técnicos de soporte")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @GetMapping
    @Operation(summary = "Listar técnicos")
    public List<Tecnico> listar(
            @Parameter(description = "Filtrar por disponibilidad") @RequestParam(required = false) Boolean disponible) {
        return tecnicoService.listar(disponible);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un técnico por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Técnico encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe el técnico",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Tecnico obtener(@PathVariable Long id) {
        return tecnicoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Registrar un técnico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Técnico registrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Tecnico> registrar(@Valid @RequestBody Tecnico tecnico) {
        Tecnico creado = tecnicoService.registrar(tecnico);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un técnico")
    public Tecnico actualizar(@PathVariable Long id, @Valid @RequestBody Tecnico tecnico) {
        return tecnicoService.actualizar(id, tecnico);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un técnico", description = "No se permite si tiene solicitudes asignadas")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Técnico eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe el técnico",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Tiene solicitudes asignadas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tecnicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
