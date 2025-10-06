# API de Rotas - Spring Boot

API em Spring Boot para cadastro de clientes com localização geográfica e geração de rotas otimizadas por tempo e distância.

## Pré-requisitos
- Java 17
- Maven 3.9+

## Como executar
```bash
mvn spring-boot:run
```
A aplicação será iniciada em `http://localhost:8080`.

## Endpoints principais
### `POST /clientes`
Registra um cliente.

Exemplo de requisição:
```json
{
  "nome": "Mercado Central",
  "endereco": "Av. Principal, 100",
  "latitude": -23.5505,
  "longitude": -46.6333
}
```

### `GET /clientes`
Lista todos os clientes cadastrados.

### `POST /rotas/otimizar`
Recebe uma lista de IDs de clientes cadastrados e devolve a melhor sequência encontrada pela heurística de vizinho mais próximo, além de distância total (km) e tempo estimado (h).

Exemplo de requisição:
```json
{
  "clientesIds": ["id-cliente-1", "id-cliente-2"]
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
