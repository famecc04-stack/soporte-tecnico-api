package pe.soporte.api.repository;

import org.springframework.stereotype.Repository;
import pe.soporte.api.model.Cliente;

/** Almacén en memoria de clientes. */
@Repository
public class ClienteRepository extends RepositorioEnMemoria<Cliente> {

    @Override
    protected Long obtenerId(Cliente cliente) {
        return cliente.getId();
    }

    @Override
    protected void asignarId(Cliente cliente, Long id) {
        cliente.setId(id);
    }

    /** true si otro cliente (distinto de idExcluido) ya usa ese email. */
    public boolean existeEmail(String email, Long idExcluido) {
        return listarTodos().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email) && !c.getId().equals(idExcluido));
    }
}
