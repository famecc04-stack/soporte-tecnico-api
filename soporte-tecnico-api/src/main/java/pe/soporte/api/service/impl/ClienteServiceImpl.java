package pe.soporte.api.service.impl;

import org.springframework.stereotype.Service;
import pe.soporte.api.exception.RecursoNoEncontradoException;
import pe.soporte.api.exception.ReglaNegocioException;
import pe.soporte.api.model.Cliente;
import pe.soporte.api.repository.ClienteRepository;
import pe.soporte.api.repository.SolicitudRepository;
import pe.soporte.api.service.ClienteService;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final SolicitudRepository solicitudRepository;

    // Inyección de dependencias por constructor
    public ClienteServiceImpl(ClienteRepository clienteRepository, SolicitudRepository solicitudRepository) {
        this.clienteRepository = clienteRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public List<Cliente> listar() {
        return clienteRepository.listarTodos();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    @Override
    public Cliente registrar(Cliente cliente) {
        validarEmailUnico(cliente.getEmail(), null);
        cliente.setId(null); // el id siempre lo genera el sistema
        return clienteRepository.guardar(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = buscarPorId(id);
        validarEmailUnico(datos.getEmail(), id);
        existente.setNombre(datos.getNombre());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        existente.setEmpresa(datos.getEmpresa());
        return clienteRepository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        if (solicitudRepository.existePorClienteId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el cliente " + id + " porque tiene solicitudes registradas");
        }
        clienteRepository.eliminarPorId(id);
    }

    private void validarEmailUnico(String email, Long idExcluido) {
        if (clienteRepository.existeEmail(email, idExcluido)) {
            throw new ReglaNegocioException("Ya existe un cliente registrado con el email " + email);
        }
    }
}
