package pe.soporte.api.service;

import pe.soporte.api.dto.SolicitudRequest;
import pe.soporte.api.model.EstadoSolicitud;
import pe.soporte.api.model.Prioridad;
import pe.soporte.api.model.Solicitud;

import java.util.List;

/** Contrato de la lógica de negocio para solicitudes de soporte técnico. */
public interface SolicitudService {

    /** Lista solicitudes aplicando los filtros que no sean nulos. */
    List<Solicitud> listar(EstadoSolicitud estado, Prioridad prioridad, Long clienteId, Long tecnicoId);

    Solicitud buscarPorId(Long id);

    Solicitud registrar(SolicitudRequest request);

    Solicitud actualizar(Long id, SolicitudRequest request);

    Solicitud cambiarEstado(Long id, EstadoSolicitud nuevoEstado);

    Solicitud asignarTecnico(Long id, Long tecnicoId);

    void eliminar(Long id);
}
