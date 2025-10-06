package com.rotasdanilov.rotas.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RotaRequest(
        @NotEmpty(message = "Informe pelo menos um cliente")
        List<String> clientesIds
) {
}
