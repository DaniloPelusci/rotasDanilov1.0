package com.rotasdanilov.rotas.dto.route;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record RouteWaypointRequest(
        @NotBlank(message = "Informe um identificador para o ponto")
        String id,
        @DecimalMin(value = "-90.0", message = "Latitude deve ser maior ou igual a -90")
        @DecimalMax(value = "90.0", message = "Latitude deve ser menor ou igual a 90")
        double latitude,
        @DecimalMin(value = "-180.0", message = "Longitude deve ser maior ou igual a -180")
        @DecimalMax(value = "180.0", message = "Longitude deve ser menor ou igual a 180")
        double longitude
) {
}
