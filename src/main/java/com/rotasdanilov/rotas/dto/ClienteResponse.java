package com.rotasdanilov.rotas.dto;

public record ClienteResponse(
        String id,
        String nome,
        String endereco,
        double latitude,
        double longitude
) {
}
