package pe.soporte.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.soporte.api.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo centralizado de excepciones.
 * <p>
 * {@code @RestControllerAdvice} = {@code @ControllerAdvice} + {@code @ResponseBody}:
 * intercepta las excepciones lanzadas por cualquier controlador y las convierte
 * en una respuesta JSON uniforme ({@link ErrorResponse}) con el código HTTP adecuado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 - El recurso solicitado no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex,
                                                             HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /** 409 - La operación viola una regla de negocio. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex,
                                                             HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /** 400 - Falló alguna anotación de validación (@NotBlank, @Email, @NotNull, ...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // Si un campo tiene varios errores se conserva el primero
            errores.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return construir(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos", request, errores);
    }

    /** 400 - JSON mal formado o valor no permitido en un enum (ej. prioridad "URGENTE"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        String mensaje = "El cuerpo de la petición no es un JSON válido o contiene valores no permitidos. "
                + "Prioridad: BAJA, MEDIA, ALTA, CRITICA. Estado: ABIERTA, EN_PROCESO, RESUELTA, CERRADA";
        return construir(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    /** 400 - Parámetro de ruta o de consulta con tipo incorrecto (ej. /api/solicitudes/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue();
        return construir(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    /** 405 - Método HTTP no soportado por el endpoint. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED,
                "El método " + ex.getMethod() + " no está permitido en este endpoint", request, null);
    }

    /** 415 - El cuerpo no se envió como JSON (falta Content-Type: application/json). */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarTipoContenido(HttpMediaTypeNotSupportedException ex,
                                                              HttpServletRequest request) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El cuerpo debe enviarse con Content-Type: application/json", request, null);
    }

    /** 404 - Ruta inexistente. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRutaInexistente(NoResourceFoundException ex,
                                                                HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request, null);
    }

    /** 500 - Cualquier error no previsto. No se expone el detalle interno al cliente. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Intente nuevamente más tarde", request, null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje,
                                                    HttpServletRequest request, Map<String, String> detalles) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                detalles
        );
        return ResponseEntity.status(status).body(body);
    }
}
