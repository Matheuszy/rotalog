# RotaLog API Entregas

Microsserviço de gestão de entregas — pedidos, rotas e rastreamento.

## Stack

- Node.js 18
- Express 4.x
- Sequelize ORM
- PostgreSQL (schema `entregas`)

## Como rodar

Pré-requisito: PostgreSQL rodando via `rotalog-workspace`.

```bash
npm install
npm start
```

A API sobe na porta **3000**.

## Estrutura do projeto

```
src/
├── routes/          # Definição de rotas e handlers
├── services/        # Lógica de negócio
├── models/          # Models do Sequelize
├── middlewares/     # Autenticação, validação
└── config/          # Configurações de banco e app
```

## Endpoints principais

| Método | Rota                          | Descrição                     |
|--------|-------------------------------|-------------------------------|
| GET    | /api/entregas                 | Listar entregas               |
| GET    | /api/entregas/:id             | Buscar entrega por ID         |
| POST   | /api/entregas                 | Criar nova entrega            |
| GET    | /api/entregas/:id/tracking    | Rastreamento de uma entrega   |
| GET    | /api/rotas                    | Listar rotas                  |

## Testes

```bash
npm test
```

Para testar manualmente, há um arquivo `requests.http` na raiz do projeto.
