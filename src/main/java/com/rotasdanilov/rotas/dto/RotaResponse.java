package com.rotasdanilov.rotas.dto;

import java.util.List;

public record RotaResponse(
        List<ClienteResponse> sequencia,
        double distanciaTotalKm,
        double tempoEstimadoHoras
) {
}
