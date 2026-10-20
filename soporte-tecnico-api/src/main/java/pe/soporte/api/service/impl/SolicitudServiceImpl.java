package pe.soporte.api.service.impl;

import org.springframework.stereotype.Service;
import pe.soporte.api.dto.SolicitudRequest;
import pe.soporte.api.exception.RecursoNoEncontradoException;
import pe.soporte.api.exception.ReglaNegocioException;
import pe.soporte.api.model.EstadoSolicitud;
import pe.soporte.api.model.Prioridad;
import pe.soporte.api.model.Solicitud;
import pe.soporte.api.model.Tecnico;
import pe.soporte.api.repository.SolicitudRepository;
import pe.soporte.api.service.ClienteService;
import pe.soporte.api.service.SolicitudService;
import pe.soporte.api.service.TecnicoService;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    /**
     * Transiciones de estado permitidas.
     * ABIERTA -> EN_PROCESO | CERRADA
     * EN_PROCESO -> RESUELTA | ABIERTA
     * RESUELTA -> CERRADA | EN_PROCESO (reapertura)
     * CERRADA -> (estado final)
     */
    private static final Map<EstadoSolicitud, Set<EstadoSolicitud>> TRANSICIONES = new EnumMap<>(EstadoSolicitud.class);

    static {
        TRANSICIONES.put(EstadoSolicitud.ABIERTA, EnumSet.of(EstadoSolicitud.EN_PROCESO, EstadoSolicitud.CERRADA));
        TRANSICIONES.put(EstadoSolicitud.EN_PROCESO, EnumSet.of(EstadoSolicitud.RESUELTA, EstadoSolicitud.ABIERTA));
        TRANSICIONES.put(EstadoSolicitud.RESUELTA, EnumSet.of(EstadoSolicitud.CERRADA, EstadoSolicitud.EN_PROCESO));
        TRANSICIONES.put(EstadoSolicitud.CERRADA, EnumSet.noneOf(EstadoSolicitud.class));
    }

    private final SolicitudRepository solicitudRepository;
    private final ClienteService clienteService;
    private final TecnicoService tecnicoService;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                ClienteService clienteService,
                                TecnicoService tecnicoService) {
        this.solicitudRepository = solicitudRepository;
        this.clienteService = clienteService;
        this.tecnicoService = tecnicoService;
    }

    @Override
    public List<Solicitud> listar(EstadoSolicitud estado, Prioridad prioridad, Long clienteId, Long tecnicoId) {
        return solicitudRepository.listarTodos().stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> prioridad == null || s.getPrioridad() == prioridad)
                .filter(s -> clienteId == null || Objects.equals(s.getClienteId(), clienteId))
                .filter(s -> tecnicoId == null || Objects.equals(s.getTecnicoId(), tecnicoId))
                .toList();
    }

    @Override
    public Solicitud buscarPorId(Long id) {
        return solicitudRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", id));
    }

    @Override
    public Solicitud registrar(SolicitudRequest request) {
        clienteService.buscarPorId(request.getClienteId()); // 404 si el cliente no existe
        if (request.getTecnicoId() != null) {
            validarTecnicoAsignable(request.getTecnicoId());
        }

        LocalDateTime ahora = LocalDateTime.now();
        Solicitud solicitud = new Solicitud();
        solicitud.setTitulo(request.getTitulo().trim());
        solicitud.setDescripcion(request.getDescripcion().trim());
        solicitud.setPrioridad(request.getPrioridad());
        solicitud.setClienteId(request.getClienteId());
        solicitud.setTecnicoId(request.getTecnicoId());
        solicitud.setEstado(EstadoSolicitud.ABIERTA); // toda solicitud nueva inicia ABIERTA
        solicitud.setFechaCreacion(ahora);
        solicitud.setFechaActualizacion(ahora);
        return solicitudRepository.guardar(solicitud);
    }

    @Override
    public Solicitud actualizar(Long id, SolicitudRequest request) {
        Solicitud solicitud = buscarPorId(id);
        validarNoCerrada(solicitud);
        clienteService.buscarPorId(request.getClienteId());

        // Solo se valida disponibilidad si se está cambiando de técnico
        if (request.getTecnicoId() != null && !request.getTecnicoId().equals(solicitud.getTecnicoId())) {
            validarTecnicoAsignable(request.getTecnicoId());
        }

        EstadoSolicitud nuevoEstado = request.getEstado() != null ? request.getEstado() : solicitud.getEstado();
        validarTransicion(solicitud.getEstado(), nuevoEstado, request.getTecnicoId());

        solicitud.setTitulo(request.getTitulo().trim());
        solicitud.setDescripcion(request.getDescripcion().trim());
        solicitud.setPrioridad(request.getPrioridad());
        solicitud.setClienteId(request.getClienteId());
        solicitud.setTecnicoId(request.getTecnicoId());
        solicitud.setEstado(nuevoEstado);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudRepository.guardar(solicitud);
    }

    @Override
    public Solicitud cambiarEstado(Long id, EstadoSolicitud nuevoEstado) {
        Solicitud solicitud = buscarPorId(id);
        validarTransicion(solicitud.getEstado(), nuevoEstado, solicitud.getTecnicoId());
        solicitud.setEstado(nuevoEstado);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudRepository.guardar(solicitud);
    }

    @Override
    public Solicitud asignarTecnico(Long id, Long tecnicoId) {
        Solicitud solicitud = buscarPorId(id);
        validarNoCerrada(solicitud);
        validarTecnicoAsignable(tecnicoId);
        solicitud.setTecnicoId(tecnicoId);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return solicitudRepository.guardar(solicitud);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id); // 404 si no existe
        solicitudRepository.eliminarPorId(id);
    }

    // ---------- Reglas de negocio ----------

    private void validarNoCerrada(Solicitud solicitud) {
        if (solicitud.getEstado() == EstadoSolicitud.CERRADA) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getId() + " está CERRADA y no puede modificarse");
        }
    }

    private void validarTecnicoAsignable(Long tecnicoId) {
        Tecnico tecnico = tecnicoService.buscarPorId(tecnicoId); // 404 si no existe
        if (!Boolean.TRUE.equals(tecnico.getDisponible())) {
            throw new ReglaNegocioException("El técnico " + tecnico.getNombre() + " no está disponible");
        }
    }

    private void validarTransicion(EstadoSolicitud actual, EstadoSolicitud nuevo, Long tecnicoId) {
        if (actual == nuevo) {
            return; // sin cambio de estado
        }
        if (!TRANSICIONES.get(actual).contains(nuevo)) {
            throw new ReglaNegocioException("No se puede cambiar el estado de " + actual + " a " + nuevo);
        }
        if (nuevo == EstadoSolicitud.EN_PROCESO && tecnicoId == null) {
            throw new ReglaNegocioException("Debe asignar un técnico antes de pasar la solicitud a EN_PROCESO");
        }
    }
}
