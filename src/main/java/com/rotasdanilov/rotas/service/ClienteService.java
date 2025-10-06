package com.rotasdanilov.rotas.service;

import com.rotasdanilov.rotas.dto.ClienteRequest;
import com.rotasdanilov.rotas.dto.ClienteResponse;
import com.rotasdanilov.rotas.dto.RotaRequest;
import com.rotasdanilov.rotas.dto.RotaResponse;
import com.rotasdanilov.rotas.model.Cliente;
import com.rotasdanilov.rotas.optimizer.RotaOtimizador;
import com.rotasdanilov.rotas.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final RotaOtimizador otimizador;

    public ClienteService(ClienteRepository repository, RotaOtimizador otimizador) {
        this.repository = repository;
        this.otimizador = otimizador;
    }

    public ClienteResponse cadastrar(ClienteRequest request) {
        Cliente cliente = new Cliente(request.nome(), request.endereco(), request.latitude(), request.longitude());
        repository.salvar(cliente);
        return toResponse(cliente);
    }

    public List<ClienteResponse> listar() {
        return repository.listarTodos().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RotaResponse otimizar(RotaRequest request) {
        List<Cliente> clientes = request.clientesIds().stream()
                .map(id -> repository.buscarPorId(id).orElseThrow(() ->
                        new ResponseStatusException(BAD_REQUEST, "Cliente não encontrado: " + id)))
                .toList();

        RotaOtimizador.ResultadoRota resultado = otimizador.otimizar(clientes);

        List<ClienteResponse> sequencia = resultado.sequencia().stream()
                .map(this::toResponse)
                .toList();

        return new RotaResponse(sequencia, resultado.distanciaTotalKm(), resultado.tempoEstimadoHoras());
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEndereco(),
                cliente.getLatitude(), cliente.getLongitude());
    }
}
