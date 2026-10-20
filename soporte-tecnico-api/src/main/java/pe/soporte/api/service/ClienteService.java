package pe.soporte.api.service;

import pe.soporte.api.model.Cliente;

import java.util.List;

/** Contrato de la lógica de negocio para clientes. */
public interface ClienteService {

    List<Cliente> listar();

    Cliente buscarPorId(Long id);

    Cliente registrar(Cliente cliente);

    Cliente actualizar(Long id, Cliente cliente);

    void eliminar(Long id);
}
