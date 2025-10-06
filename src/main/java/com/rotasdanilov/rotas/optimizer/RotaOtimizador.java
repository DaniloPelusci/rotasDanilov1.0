package com.rotasdanilov.rotas.optimizer;

import com.rotasdanilov.rotas.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RotaOtimizador {

    private static final double VELOCIDADE_MEDIA_KMH = 40.0;

    public ResultadoRota otimizar(List<Cliente> clientes) {
        if (clientes.isEmpty()) {
            return new ResultadoRota(List.of(), 0.0, 0.0);
        }

        List<Cliente> sequencia = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Cliente atual = clientes.get(0);
        sequencia.add(atual);
        visitados.add(atual.getId());

        double distanciaTotal = 0.0;

        while (visitados.size() < clientes.size()) {
            Cliente proximo = null;
            double menorDistancia = Double.MAX_VALUE;

            for (Cliente candidato : clientes) {
                if (visitados.contains(candidato.getId())) {
                    continue;
                }
                double distancia = distancia(atual, candidato);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    proximo = candidato;
                }
            }

            if (proximo == null) {
                break;
            }

            sequencia.add(proximo);
            visitados.add(proximo.getId());
            distanciaTotal += menorDistancia;
            atual = proximo;
        }

        double tempoHoras = distanciaTotal / VELOCIDADE_MEDIA_KMH;
        return new ResultadoRota(sequencia, distanciaTotal, tempoHoras);
    }

    private double distancia(Cliente origem, Cliente destino) {
        double lat1 = Math.toRadians(origem.getLatitude());
        double lon1 = Math.toRadians(origem.getLongitude());
        double lat2 = Math.toRadians(destino.getLatitude());
        double lon2 = Math.toRadians(destino.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double raioTerraKm = 6371.0;
        return raioTerraKm * c;
    }

    public record ResultadoRota(List<Cliente> sequencia, double distanciaTotalKm, double tempoEstimadoHoras) {
    }
}
