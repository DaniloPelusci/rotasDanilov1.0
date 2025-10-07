package com.rotasdanilov.rotas.service;

import com.rotasdanilov.rotas.dto.route.OptimizationMetric;
import com.rotasdanilov.rotas.dto.route.RouteOptimizationRequest;
import com.rotasdanilov.rotas.dto.route.RouteOptimizationResponse;
import com.rotasdanilov.rotas.dto.route.RouteWaypointRequest;
import com.rotasdanilov.rotas.integration.osrm.OsrmClient;
import com.rotasdanilov.rotas.integration.osrm.OsrmTableResponse;
import com.rotasdanilov.rotas.model.GeoCoordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteOptimizationServiceTest {

    @Mock
    private OsrmClient osrmClient;

    private RouteOptimizationService service;

    @BeforeEach
    void setUp() {
        service = new RouteOptimizationService(osrmClient);
    }

    @Test
    void shouldOptimizeRouteUsingDurationMetric() {
        RouteWaypointRequest depot = new RouteWaypointRequest("DEPOT", -23.0, -46.0);
        RouteWaypointRequest a = new RouteWaypointRequest("A", -23.1, -46.1);
        RouteWaypointRequest b = new RouteWaypointRequest("B", -23.2, -46.2);
        RouteWaypointRequest c = new RouteWaypointRequest("C", -23.3, -46.3);

        RouteOptimizationRequest request = new RouteOptimizationRequest(
                depot,
                List.of(a, b, c),
                OptimizationMetric.DURATION,
                true
        );

        List<List<Double>> matrix = List.of(
                List.of(0.0, 10.0, 20.0, 30.0),
                List.of(10.0, 0.0, 15.0, 35.0),
                List.of(20.0, 15.0, 0.0, 25.0),
                List.of(30.0, 35.0, 25.0, 0.0)
        );
        OsrmTableResponse osrmResponse = new OsrmTableResponse("Ok", matrix, matrix);
        when(osrmClient.table(anyList())).thenReturn(osrmResponse);

        RouteOptimizationResponse response = service.optimize(request);

        ArgumentCaptor<List<GeoCoordinate>> coordinatesCaptor = ArgumentCaptor.forClass(List.class);
        verify(osrmClient).table(coordinatesCaptor.capture());
        assertThat(coordinatesCaptor.getValue()).hasSize(4);

        assertThat(response.totalDurationSeconds()).isEqualTo(80.0);
        assertThat(response.totalDistanceMeters()).isEqualTo(80.0);
        assertThat(response.route()).hasSize(5);
        assertThat(response.route().get(0).id()).isEqualTo("DEPOT");
        assertThat(response.route().get(4).id()).isEqualTo("DEPOT");
        assertThat(response.route().get(4).cumulativeDurationSeconds()).isEqualTo(80.0);
        assertThat(response.route().get(1).id()).isEqualTo("A");
        assertThat(response.route().get(2).id()).isEqualTo("B");
        assertThat(response.route().get(3).id()).isEqualTo("C");
        assertThat(response.route().get(3).cumulativeDurationSeconds()).isEqualTo(50.0);
    }
}
