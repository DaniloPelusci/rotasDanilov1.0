# API de Rotas - Spring Boot

API em Spring Boot para otimizar rotas a partir de um depósito e uma lista de paradas usando matriz de distâncias/tempos do OSRM.

## Pré-requisitos
- Java 17
- Maven 3.9+

## Como executar
```bash
mvn spring-boot:run
```
A aplicação será iniciada em `http://localhost:8080`.

## Endpoint principal
### `POST /route/optimize`
Resolve um problema de caixeiro viajante (TSP) para um veículo a partir de um depósito e múltiplas paradas.

O endpoint consulta a matriz de distância/tempo no serviço [OSRM](http://project-osrm.org/) e aplica o algoritmo Held-Karp
para encontrar a sequência ótima minimizando duração (padrão) ou distância. Também é possível solicitar retorno ao depósito.

Exemplo de requisição:
```json
{
  "depot": {
    "id": "DEPOT",
    "latitude": -23.5505,
    "longitude": -46.6333
  },
  "stops": [
    { "id": "A", "latitude": -23.5520, "longitude": -46.6320 },
    { "id": "B", "latitude": -23.5489, "longitude": -46.6351 }
  ],
  "metric": "DURATION",
  "returnToDepot": true
}
```

Resposta (exemplo):
```json
{
  "route": [
    {"id": "DEPOT", "sequence": 0, "depot": true, "cumulativeDistanceMeters": 0.0, "cumulativeDurationSeconds": 0.0},
    {"id": "A", "sequence": 1, "depot": false, "cumulativeDistanceMeters": 1530.4, "cumulativeDurationSeconds": 230.1},
    {"id": "B", "sequence": 2, "depot": false, "cumulativeDistanceMeters": 2740.7, "cumulativeDurationSeconds": 410.9},
    {"id": "DEPOT", "sequence": 3, "depot": true, "cumulativeDistanceMeters": 3891.5, "cumulativeDurationSeconds": 580.4}
  ],
  "totalDistanceMeters": 3891.5,
  "totalDurationSeconds": 580.4
}
```

## Documentação OpenAPI (Swagger)

Com a aplicação em execução é possível acessar a documentação interativa no Swagger UI em:

```
http://localhost:8080/swagger-ui.html
```

O documento OpenAPI em formato JSON está disponível em `http://localhost:8080/v3/api-docs`.

## Testes
```bash
mvn test
```
