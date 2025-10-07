package com.rotasdanilov.rotas.dto.route;

public record RoutePointResponse(
        String id,
        double latitude,
        double longitude,
        int sequence,
        boolean depot,
        double cumulativeDistanceMeters,
        double cumulativeDurationSeconds
) {
}
