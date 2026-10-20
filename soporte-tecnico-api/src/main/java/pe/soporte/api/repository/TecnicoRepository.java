package pe.soporte.api.repository;

import org.springframework.stereotype.Repository;
import pe.soporte.api.model.Tecnico;

/** Almacén en memoria de técnicos. */
@Repository
public class TecnicoRepository extends RepositorioEnMemoria<Tecnico> {

    @Override
    protected Long obtenerId(Tecnico tecnico) {
        return tecnico.getId();
    }

    @Override
    protected void asignarId(Tecnico tecnico, Long id) {
        tecnico.setId(id);
    }

    /** true si otro técnico (distinto de idExcluido) ya usa ese email. */
    public boolean existeEmail(String email, Long idExcluido) {
        return listarTodos().stream()
                .anyMatch(t -> t.getEmail().equalsIgnoreCase(email) && !t.getId().equals(idExcluido));
    }
}
