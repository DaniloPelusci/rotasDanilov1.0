package com.rotasdanilov.rotas.service;

import com.rotasdanilov.rotas.dto.route.OptimizationMetric;
import com.rotasdanilov.rotas.dto.route.RouteOptimizationRequest;
import com.rotasdanilov.rotas.dto.route.RouteOptimizationResponse;
import com.rotasdanilov.rotas.dto.route.RoutePointResponse;
import com.rotasdanilov.rotas.dto.route.RouteWaypointRequest;
import com.rotasdanilov.rotas.integration.osrm.OsrmClient;
import com.rotasdanilov.rotas.integration.osrm.OsrmTableResponse;
import com.rotasdanilov.rotas.model.GeoCoordinate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class RouteOptimizationService {

    private final OsrmClient osrmClient;

    public RouteOptimizationService(OsrmClient osrmClient) {
        this.osrmClient = osrmClient;
    }

    public RouteOptimizationResponse optimize(RouteOptimizationRequest request) {
        List<RouteLocation> locations = buildLocations(request);
        List<GeoCoordinate> coordinates = locations.stream()
                .map(location -> new GeoCoordinate(location.latitude(), location.longitude()))
                .toList();

        OsrmTableResponse table = osrmClient.table(coordinates);
        double[][] durations = toMatrix(table.durations(), "duration");
        double[][] distances = toMatrix(table.distances(), "distance");

        double[][] metricMatrix = request.metric() == OptimizationMetric.DISTANCE ? distances : durations;
        TspSolution solution = solveTsp(metricMatrix, request.returnToDepot());

        List<Integer> routeIndices = solution.route();
        List<RoutePointResponse> route = buildRoute(locations, routeIndices, distances, durations);

        RoutePointResponse lastPoint = route.get(route.size() - 1);
        return new RouteOptimizationResponse(route,
                lastPoint.cumulativeDistanceMeters(),
                lastPoint.cumulativeDurationSeconds());
    }

    private List<RouteLocation> buildLocations(RouteOptimizationRequest request) {
        List<RouteLocation> locations = new ArrayList<>();
        RouteWaypointRequest depot = request.depot();
        locations.add(new RouteLocation(depot.id(), depot.latitude(), depot.longitude(), true));
        for (RouteWaypointRequest stop : request.stops()) {
            locations.add(new RouteLocation(stop.id(), stop.latitude(), stop.longitude(), false));
        }
        return locations;
    }

    private double[][] toMatrix(List<List<Double>> values, String field) {
        if (values == null || values.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Serviço OSRM não retornou matriz de " + field);
        }
        int size = values.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            List<Double> row = values.get(i);
            if (row == null || row.size() != size) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Matriz de " + field + " inválida retornada pelo OSRM");
            }
            for (int j = 0; j < size; j++) {
                Double cell = row.get(j);
                if (cell == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                            "Valor ausente na matriz de " + field + " do OSRM");
                }
                matrix[i][j] = cell;
            }
        }
        return matrix;
    }

    private List<RoutePointResponse> buildRoute(List<RouteLocation> locations,
                                                List<Integer> routeIndices,
                                                double[][] distances,
                                                double[][] durations) {
        List<RoutePointResponse> route = new ArrayList<>();
        double cumulativeDistance = 0.0;
        double cumulativeDuration = 0.0;

        for (int sequence = 0; sequence < routeIndices.size(); sequence++) {
            int locationIndex = routeIndices.get(sequence);
            RouteLocation location = locations.get(locationIndex);
            route.add(new RoutePointResponse(
                    location.id(),
                    location.latitude(),
                    location.longitude(),
                    sequence,
                    location.depot(),
                    cumulativeDistance,
                    cumulativeDuration
            ));

            if (sequence < routeIndices.size() - 1) {
                int nextIndex = routeIndices.get(sequence + 1);
                cumulativeDistance += distances[locationIndex][nextIndex];
                cumulativeDuration += durations[locationIndex][nextIndex];
            }
        }

        return route;
    }

    private TspSolution solveTsp(double[][] matrix, boolean returnToDepot) {
        Objects.requireNonNull(matrix, "matrix não pode ser nulo");
        int points = matrix.length;
        if (points == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matriz vazia recebida");
        }
        int stops = points - 1;
        if (stops <= 0) {
            List<Integer> route = new ArrayList<>();
            route.add(0);
            if (returnToDepot) {
                route.add(0);
            }
            return new TspSolution(route, 0.0);
        }

        int combinations = 1 << stops;
        double[][] dp = new double[combinations][stops];
        int[][] parent = new int[combinations][stops];
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        for (int stop = 0; stop < stops; stop++) {
            dp[1 << stop][stop] = matrix[0][stop + 1];
        }

        for (int mask = 1; mask < combinations; mask++) {
            for (int last = 0; last < stops; last++) {
                if ((mask & (1 << last)) == 0) {
                    continue;
                }
                double currentCost = dp[mask][last];
                if (Double.isInfinite(currentCost)) {
                    continue;
                }
                for (int next = 0; next < stops; next++) {
                    if ((mask & (1 << next)) != 0) {
                        continue;
                    }
                    int nextMask = mask | (1 << next);
                    double newCost = currentCost + matrix[last + 1][next + 1];
                    if (newCost < dp[nextMask][next]) {
                        dp[nextMask][next] = newCost;
                        parent[nextMask][next] = last;
                    }
                }
            }
        }

        int fullMask = combinations - 1;
        double bestCost = Double.POSITIVE_INFINITY;
        int bestLast = -1;
        for (int last = 0; last < stops; last++) {
            double cost = dp[fullMask][last];
            if (Double.isInfinite(cost)) {
                continue;
            }
            double finalCost = returnToDepot ? cost + matrix[last + 1][0] : cost;
            if (finalCost < bestCost) {
                bestCost = finalCost;
                bestLast = last;
            }
        }

        if (bestLast == -1) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Não foi possível encontrar solução para o TSP");
        }

        List<Integer> stopOrder = new ArrayList<>();
        int mask = fullMask;
        int current = bestLast;
        while (current != -1) {
            stopOrder.add(current);
            int previous = parent[mask][current];
            mask &= ~(1 << current);
            current = previous;
        }
        Collections.reverse(stopOrder);

        List<Integer> route = new ArrayList<>();
        route.add(0);
        for (int stop : stopOrder) {
            route.add(stop + 1);
        }
        if (returnToDepot) {
            route.add(0);
        }

        return new TspSolution(route, bestCost);
    }

    private record RouteLocation(String id, double latitude, double longitude, boolean depot) {
    }

    private record TspSolution(List<Integer> route, double cost) {
    }
}
