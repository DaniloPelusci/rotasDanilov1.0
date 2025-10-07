package com.rotasdanilov.rotas.integration.osrm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OsrmTableResponse(
        String code,
        List<List<Double>> durations,
        List<List<Double>> distances
) {
}
