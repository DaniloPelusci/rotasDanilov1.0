package com.rotasdanilov.rotas.dto.route;

import java.util.List;

public record RouteOptimizationResponse(
        List<RoutePointResponse> route,
        double totalDistanceMeters,
        double totalDurationSeconds
) {
}
