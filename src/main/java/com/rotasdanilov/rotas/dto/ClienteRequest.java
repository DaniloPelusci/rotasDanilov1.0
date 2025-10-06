package com.rotasdanilov.rotas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        @NotBlank(message = "O endereço é obrigatório")
        String endereco,
        @Min(value = -90, message = "Latitude inválida")
        @Max(value = 90, message = "Latitude inválida")
        double latitude,
        @Min(value = -180, message = "Longitude inválida")
        @Max(value = 180, message = "Longitude inválida")
        double longitude
) {
}
