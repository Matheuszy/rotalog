# RotaLog

Sistema de logística baseado em microsserviços para gestão de frotas e entregas.

## Visão geral

O RotaLog é composto por dois microsserviços independentes que se comunicam para cobrir todo o ciclo logístico — do cadastro de veículos e motoristas até a criação e rastreamento de entregas.

```
rotalog/
├── rotalog-api-frotas/     # Microsserviço Java — veículos, motoristas e manutenções
└── rotalog-api-entregas/   # Microsserviço Node.js — pedidos, rotas e rastreamento
```

---

## Microsserviços

### rotalog-api-frotas

Responsável pela gestão da frota: cadastro de veículos, motoristas e registro de manutenções.

- **Stack:** Java 11, Spring Boot 2.7, Spring Data JPA, Flyway, PostgreSQL (schema `frotas`)
- **Porta:** 8080

#### Como rodar

```bash
cd rotalog-api-frotas
./mvnw spring-boot:run
```

#### Endpoints principais

| Método | Rota                  | Descrição             |
|--------|-----------------------|-----------------------|
| GET    | /api/veiculos         | Listar veículos       |
| GET    | /api/veiculos/{id}    | Buscar veículo por ID |
| POST   | /api/veiculos         | Cadastrar veículo     |
| GET    | /api/motoristas       | Listar motoristas     |
| GET    | /api/manutencoes      | Listar manutenções    |

---

### rotalog-api-entregas

Responsável pela gestão de entregas: criação de pedidos, definição de rotas e rastreamento em tempo real.

- **Stack:** Node.js 18, Express 4.x, Sequelize ORM, PostgreSQL (schema `entregas`)
- **Porta:** 3000

#### Como rodar

```bash
cd rotalog-api-entregas
npm install
npm start
```

#### Endpoints principais

| Método | Rota                        | Descrição                   |
|--------|-----------------------------|-----------------------------|
| GET    | /api/entregas               | Listar entregas             |
| GET    | /api/entregas/:id           | Buscar entrega por ID       |
| POST   | /api/entregas               | Criar nova entrega          |
| GET    | /api/entregas/:id/tracking  | Rastreamento de uma entrega |
| GET    | /api/rotas                  | Listar rotas                |

Para testar manualmente, use o arquivo `requests.http` na raiz de `rotalog-api-entregas`.

---

## Pré-requisitos

Ambos os serviços dependem de uma instância PostgreSQL em execução. O ambiente recomendado é o `rotalog-workspace`, que provisiona o banco com os schemas `frotas` e `entregas` separados.

## Estrutura dos serviços

```
rotalog-api-frotas/src/main/java/com/rotalog/
├── controller/     # Endpoints REST
├── service/        # Regras de negócio
├── repository/     # Acesso a dados (Spring Data)
├── domain/         # Entidades JPA
└── config/         # Configurações

rotalog-api-entregas/src/
├── routes/         # Definição de rotas e handlers
├── services/       # Lógica de negócio
├── models/         # Models do Sequelize
├── middleware/     # Autenticação e validação
└── config/         # Configurações de banco e app
```
