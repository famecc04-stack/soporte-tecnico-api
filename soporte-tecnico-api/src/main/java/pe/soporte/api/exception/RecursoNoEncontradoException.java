package pe.soporte.api.exception;

/** Se lanza cuando no existe un cliente, técnico o solicitud con el id indicado (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }
}
