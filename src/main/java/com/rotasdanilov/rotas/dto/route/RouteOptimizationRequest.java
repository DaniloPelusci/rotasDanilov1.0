package com.rotasdanilov.rotas.dto.route;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public record RouteOptimizationRequest(
        @NotNull(message = "Informe o depósito")
        @Valid
        RouteWaypointRequest depot,
        @NotEmpty(message = "Informe pelo menos uma parada")
        List<@Valid RouteWaypointRequest> stops,
        OptimizationMetric metric,
        boolean returnToDepot
) {

    public RouteOptimizationRequest {
        Objects.requireNonNull(depot, "depot não pode ser nulo");
        stops = List.copyOf(Objects.requireNonNull(stops, "stops não pode ser nulo"));
        metric = metric == null ? OptimizationMetric.DURATION : metric;
    }
}
