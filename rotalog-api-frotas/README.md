# RotaLog API Frotas

Microsserviço de gestão de frotas — veículos, motoristas e manutenções.

## Stack

- Java 11
- Spring Boot 2.7
- Spring Data JPA / Hibernate
- Flyway (migrations)
- PostgreSQL (schema `frotas`)

## Como rodar

Pré-requisito: PostgreSQL rodando via `rotalog-workspace`.

```bash
./mvnw spring-boot:run
```

A API sobe na porta **8080**.

## Estrutura do projeto

```
src/main/java/com/rotalog/frotas/
├── controller/       # Endpoints REST
├── service/          # Regras de negócio
├── repository/       # Acesso a dados (Spring Data)
├── model/            # Entidades JPA
└── config/           # Configurações
```

## Endpoints principais

| Método | Rota                       | Descrição                    |
|--------|----------------------------|------------------------------|
| GET    | /api/veiculos              | Listar veículos              |
| GET    | /api/veiculos/{id}         | Buscar veículo por ID        |
| POST   | /api/veiculos              | Cadastrar veículo            |
| GET    | /api/motoristas            | Listar motoristas            |
| GET    | /api/manutencoes           | Listar manutenções           |

## Migrations

Flyway em `src/main/resources/db/migration/`. Seguir a sequência existente ao
adicionar novas migrations.
