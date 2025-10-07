package com.rotasdanilov.rotas.integration.osrm;

import com.rotasdanilov.rotas.model.GeoCoordinate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OsrmClient {

    private final RestClient restClient;

    public OsrmClient(RestClient.Builder builder,
                      @Value("${osrm.base-url:https://router.project-osrm.org}") String baseUrl) {
        this.restClient = builder.baseUrl(Objects.requireNonNull(baseUrl)).build();
    }

    public OsrmTableResponse table(List<GeoCoordinate> coordinates) {
        if (coordinates == null || coordinates.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "São necessários pelo menos dois pontos para calcular a matriz de distância");
        }

        String coordsParam = coordinates.stream()
                .map(coord -> String.format(Locale.US, "%.6f,%.6f", coord.longitude(), coord.latitude()))
                .collect(Collectors.joining(";"));

        try {
            OsrmTableResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/table/v1/driving/{coordinates}")
                            .queryParam("annotations", "duration,distance")
                            .build(coordsParam))
                    .retrieve()
                    .body(OsrmTableResponse.class);

            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Resposta vazia do serviço OSRM");
            }

            if (!"Ok".equalsIgnoreCase(response.code())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Serviço OSRM retornou estado inválido: " + response.code());
            }

            return response;
        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Falha ao consultar OSRM: " + ex.getStatusText(), ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Falha ao consultar OSRM", ex);
        }
    }
}
