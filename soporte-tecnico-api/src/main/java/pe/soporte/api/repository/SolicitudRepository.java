package pe.soporte.api.repository;

import org.springframework.stereotype.Repository;
import pe.soporte.api.model.Solicitud;

import java.util.Objects;

/** Almacén en memoria de solicitudes de soporte. */
@Repository
public class SolicitudRepository extends RepositorioEnMemoria<Solicitud> {

    @Override
    protected Long obtenerId(Solicitud solicitud) {
        return solicitud.getId();
    }

    @Override
    protected void asignarId(Solicitud solicitud, Long id) {
        solicitud.setId(id);
    }

    public boolean existePorClienteId(Long clienteId) {
        return listarTodos().stream().anyMatch(s -> Objects.equals(s.getClienteId(), clienteId));
    }

    public boolean existePorTecnicoId(Long tecnicoId) {
        return listarTodos().stream().anyMatch(s -> Objects.equals(s.getTecnicoId(), tecnicoId));
    }
}
