package pe.soporte.api.service.impl;

import org.springframework.stereotype.Service;
import pe.soporte.api.exception.RecursoNoEncontradoException;
import pe.soporte.api.exception.ReglaNegocioException;
import pe.soporte.api.model.Tecnico;
import pe.soporte.api.repository.SolicitudRepository;
import pe.soporte.api.repository.TecnicoRepository;
import pe.soporte.api.service.TecnicoService;

import java.util.List;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final SolicitudRepository solicitudRepository;

    public TecnicoServiceImpl(TecnicoRepository tecnicoRepository, SolicitudRepository solicitudRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public List<Tecnico> listar(Boolean disponible) {
        return tecnicoRepository.listarTodos().stream()
                .filter(t -> disponible == null || disponible.equals(t.getDisponible()))
                .toList();
    }

    @Override
    public Tecnico buscarPorId(Long id) {
        return tecnicoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico", id));
    }

    @Override
    public Tecnico registrar(Tecnico tecnico) {
        validarEmailUnico(tecnico.getEmail(), null);
        tecnico.setId(null);
        return tecnicoRepository.guardar(tecnico);
    }

    @Override
    public Tecnico actualizar(Long id, Tecnico datos) {
        Tecnico existente = buscarPorId(id);
        validarEmailUnico(datos.getEmail(), id);
        existente.setNombre(datos.getNombre());
        existente.setEmail(datos.getEmail());
        existente.setEspecialidad(datos.getEspecialidad());
        existente.setDisponible(datos.getDisponible());
        return tecnicoRepository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        if (solicitudRepository.existePorTecnicoId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el técnico " + id + " porque tiene solicitudes asignadas");
        }
        tecnicoRepository.eliminarPorId(id);
    }

    private void validarEmailUnico(String email, Long idExcluido) {
        if (tecnicoRepository.existeEmail(email, idExcluido)) {
            throw new ReglaNegocioException("Ya existe un técnico registrado con el email " + email);
        }
    }
}
