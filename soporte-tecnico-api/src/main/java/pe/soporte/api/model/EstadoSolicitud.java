package pe.soporte.api.model;

/**
 * Ciclo de vida de una solicitud:
 * ABIERTA -> EN_PROCESO -> RESUELTA -> CERRADA
 */
public enum EstadoSolicitud {
    ABIERTA,
    EN_PROCESO,
    RESUELTA,
    CERRADA
}
