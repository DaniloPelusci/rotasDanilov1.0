package com.rotasdanilov.rotas.repository;

import com.rotasdanilov.rotas.model.Cliente;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClienteRepository {

    private final Map<String, Cliente> clientes = new ConcurrentHashMap<>();

    public Cliente salvar(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
        return cliente;
    }

    public Optional<Cliente> buscarPorId(String id) {
        return Optional.ofNullable(clientes.get(id));
    }

    public Collection<Cliente> listarTodos() {
        return clientes.values();
    }
}
