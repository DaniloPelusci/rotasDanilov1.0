# API de Rotas - Spring Boot

API em Spring Boot para cadastro de clientes com localização geográfica e geração de rotas otimizadas por tempo e distância.

## Pré-requisitos
- Java 17
- Maven 3.9+
- PostgreSQL 14+

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

## Configuração do banco de dados PostgreSQL

Para criar a estrutura necessária para a aplicação, execute o script abaixo em um servidor PostgreSQL:

```sql
CREATE DATABASE rotas;

\c rotas;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE clientes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome VARCHAR(255) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);
```

> Obs.: O uso da extensão `uuid-ossp` permite gerar valores UUID diretamente no banco ao inserir novos clientes.

Para popular a tabela com alguns clientes de exemplo, utilize o script a seguir após criar a estrutura:

```sql
INSERT INTO clientes (nome, endereco, latitude, longitude) VALUES
    ('Mercado Central', 'Av. Principal, 100', -23.5505, -46.6333),
    ('Padaria do Bairro', 'Rua das Flores, 45', -23.5520, -46.6320),
    ('Farmácia Saúde', 'Rua das Acácias, 210', -23.5489, -46.6351),
    ('Posto 24h', 'Av. das Nações, 500', -23.5472, -46.6308);
```
