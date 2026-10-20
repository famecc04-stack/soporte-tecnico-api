package pe.soporte.api.exception;

/**
 * Se lanza cuando la petición es válida en formato pero viola una regla de negocio
 * (email duplicado, solicitud cerrada, técnico no disponible, etc.) (HTTP 409).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
