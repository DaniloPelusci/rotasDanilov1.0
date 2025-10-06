package com.rotasdanilov.rotas.controller;

import com.rotasdanilov.rotas.dto.RotaRequest;
import com.rotasdanilov.rotas.dto.RotaResponse;
import com.rotasdanilov.rotas.service.ClienteService;
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
@RequestMapping("/rotas")
@Tag(name = "Rotas", description = "Operações relacionadas à otimização de rotas de clientes")
public class RotaController {

    private final ClienteService service;

    public RotaController(ClienteService service) {
        this.service = service;
    }

    @PostMapping("/otimizar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Calcula a melhor rota",
            description = "Recebe a lista de clientes cadastrados e retorna a sequência otimizada de visitas, " +
                          "informando distância total e tempo estimado de deslocamento."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Rota otimizada gerada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RotaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (ex.: lista vazia ou identificadores inexistentes)",
                    content = @Content
            )
    })
    public RotaResponse otimizar(@Valid @RequestBody RotaRequest request) {
        return service.otimizar(request);
    }
}
