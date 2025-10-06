# Análise do projeto

## Visão geral
O repositório fornece uma API REST em Java/Spring Boot capaz de cadastrar clientes com dados de localização e calcular uma rota otimizada a partir de uma lista de clientes informada. Os dados são mantidos em memória e a heurística de vizinho mais próximo calcula a ordem de visita, distância total (km) e tempo estimado (h) assumindo velocidade média de 40 km/h.

## Estrutura atual
```
pom.xml
src/
 ├── main/java/com/rotasdanilov/rotas
 │    ├── RotasApplication.java
 │    ├── controller/
 │    │    ├── ClienteController.java
 │    │    └── RotaController.java
 │    ├── dto/
 │    │    ├── ClienteRequest.java
 │    │    ├── ClienteResponse.java
 │    │    ├── RotaRequest.java
 │    │    └── RotaResponse.java
 │    ├── model/
 │    │    └── Cliente.java
 │    ├── optimizer/
 │    │    └── RotaOtimizador.java
 │    ├── repository/
 │    │    └── ClienteRepository.java
 │    └── service/
 │         └── ClienteService.java
 └── test/java/com/rotasdanilov/rotas
      └── RotasApplicationTests.java
```

## Fluxo principal
1. `POST /clientes` cadastra um cliente com nome, endereço, latitude e longitude.
2. `GET /clientes` lista todos os registros.
3. `POST /rotas/otimizar` recebe IDs previamente cadastrados e calcula a melhor ordem de visita.

## Próximos passos sugeridos
- Persistir clientes em banco relacional (PostgreSQL ou MySQL) usando Spring Data JPA.
- Adicionar autenticação básica ou OAuth2 para proteger os endpoints.
- Permitir ponto de partida personalizado nas rotas e integração com APIs de mapas para distâncias reais.
