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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.soporte.api.dto.AsignacionTecnicoRequest;
import pe.soporte.api.dto.CambioEstadoRequest;
import pe.soporte.api.dto.ErrorResponse;
import pe.soporte.api.dto.SolicitudRequest;
import pe.soporte.api.model.EstadoSolicitud;
import pe.soporte.api.model.Prioridad;
import pe.soporte.api.model.Solicitud;
import pe.soporte.api.service.SolicitudService;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de solicitudes de soporte técnico.
 * El controlador solo recibe/valida la petición y delega la lógica al servicio.
 */
@RestController
@RequestMapping("/api/solicitudes")
@Tag(name = "Solicitudes", description = "Registro, consulta, actualización y eliminación de solicitudes de soporte")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    @Operation(summary = "Listar solicitudes", description = "Todos los filtros son opcionales y se pueden combinar")
    @ApiResponse(responseCode = "200", description = "Lista de solicitudes")
    public List<Solicitud> listar(
            @Parameter(description = "Filtrar por estado") @RequestParam(required = false) EstadoSolicitud estado,
            @Parameter(description = "Filtrar por prioridad") @RequestParam(required = false) Prioridad prioridad,
            @Parameter(description = "Filtrar por cliente") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Filtrar por técnico") @RequestParam(required = false) Long tecnicoId) {
        return solicitudService.listar(estado, prioridad, clienteId, tecnicoId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una solicitud por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe la solicitud",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Solicitud obtener(@PathVariable Long id) {
        return solicitudService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Registrar una solicitud", description = "La solicitud se crea siempre en estado ABIERTA")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud registrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente o técnico inexistente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Técnico no disponible",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Solicitud> registrar(@Valid @RequestBody SolicitudRequest request) {
        Solicitud creada = solicitudService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una solicitud completa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Solicitud, cliente o técnico inexistente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Solicitud cerrada o transición de estado no permitida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Solicitud actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudRequest request) {
        return solicitudService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una solicitud",
            description = "ABIERTA→EN_PROCESO|CERRADA, EN_PROCESO→RESUELTA|ABIERTA, RESUELTA→CERRADA|EN_PROCESO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "409", description = "Transición no permitida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Solicitud cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return solicitudService.cambiarEstado(id, request.getEstado());
    }

    @PatchMapping("/{id}/tecnico")
    @Operation(summary = "Asignar un técnico a la solicitud")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Técnico asignado"),
            @ApiResponse(responseCode = "404", description = "Solicitud o técnico inexistente",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Técnico no disponible o solicitud cerrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Solicitud asignarTecnico(@PathVariable Long id, @Valid @RequestBody AsignacionTecnicoRequest request) {
        return solicitudService.asignarTecnico(id, request.getTecnicoId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una solicitud")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitud eliminada"),
            @ApiResponse(responseCode = "404", description = "No existe la solicitud",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
