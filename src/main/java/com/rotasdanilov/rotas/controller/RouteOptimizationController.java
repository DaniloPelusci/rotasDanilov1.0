package com.rotasdanilov.rotas.controller;

import com.rotasdanilov.rotas.dto.route.RouteOptimizationRequest;
import com.rotasdanilov.rotas.dto.route.RouteOptimizationResponse;
import com.rotasdanilov.rotas.service.RouteOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
@Tag(name = "Route Optimization", description = "Otimização de rotas usando matriz OSRM")
public class RouteOptimizationController {

    private final RouteOptimizationService service;

    public RouteOptimizationController(RouteOptimizationService service) {
        this.service = service;
    }

    @PostMapping("/optimize")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Resolve o TSP para um veículo",
            description = "Recebe o depósito e as paradas desejadas (latitude/longitude) e retorna a sequência ótima " +
                    "minimizando tempo ou distância."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rota otimizada calculada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteOptimizationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Falha na comunicação com o serviço de matriz",
                    content = @Content
            )
    })
    public RouteOptimizationResponse optimize(@Valid @RequestBody RouteOptimizationRequest request) {
        return service.optimize(request);
    }
}
