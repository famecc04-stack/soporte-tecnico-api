package pe.soporte.api.service;

import pe.soporte.api.model.Tecnico;

import java.util.List;

/** Contrato de la lógica de negocio para técnicos. */
public interface TecnicoService {

    /** @param disponible si no es nulo, filtra por disponibilidad */
    List<Tecnico> listar(Boolean disponible);

    Tecnico buscarPorId(Long id);

    Tecnico registrar(Tecnico tecnico);

    Tecnico actualizar(Long id, Tecnico tecnico);

    void eliminar(Long id);
}
