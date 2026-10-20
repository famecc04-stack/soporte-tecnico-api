package pe.soporte.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato único de error que devuelve la API (lo construye GlobalExceptionHandler).
 *
 * @param timestamp momento del error
 * @param status    código HTTP
 * @param error     nombre del código HTTP
 * @param mensaje   descripción legible del problema
 * @param ruta      endpoint invocado
 * @param detalles  errores por campo (solo en errores de validación)
 */
@Schema(description = "Respuesta de error estándar")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> detalles
) {
}
