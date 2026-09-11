# ADR-001: Refatoração de auth.js e entregas.js

- **Status:** Proposed
- **Data:** 2026-04-01
- **Autores:** Time de Engenharia Rotalog

---

## Contexto

Dois arquivos concentram a maior parte dos problemas de dívida técnica identificados no serviço `rotalog-api-entregas`: o middleware `src/middleware/auth.js` e o arquivo de rotas `src/routes/entregas.js`. O diagnóstico revela três categorias de risco:

### 1. Vulnerabilidades de Segurança (auth.js)

**JWT secret hardcoded no código-fonte**
A constante `JWT_SECRET = 'super-secret-key-that-should-not-be-hardcoded'` está exposta diretamente no código. Qualquer pessoa com acesso ao repositório pode forjar tokens válidos se a validação fosse real. A constante sequer é utilizada na lógica atual, confirmando que nunca houve validação de assinatura.

**Bypass total de autenticação em não-produção**
O bloco `if (process.env.NODE_ENV !== 'production') { return next(); }` ignora completamente a autenticação em ambientes de desenvolvimento, staging e em qualquer execução local. Isso significa que todos os endpoints protegidos ficam abertos sem nenhum controle, o que representa risco especialmente em ambientes de homologação acessíveis externamente.

**Validação fake do token em produção**
Em produção, o middleware verifica apenas se o header `Authorization` existe e se o token possui mais de 10 caracteres. Não há decodificação JWT, não há verificação de assinatura, não há verificação de expiração e não há verificação de issuer. Qualquer string com mais de 10 caracteres passa como token válido.

**Usuário fixo injetado em `req.user`**
O objeto `req.user = { id: 1, nome: 'Admin', role: 'admin' }` é atribuído para toda requisição que passa na "validação". Isso significa que toda a aplicação opera com um único usuário fictício de perfil administrador, sem identidade real e sem possibilidade de distinguir quem fez cada ação.

**Ausência de RBAC**
Não existe qualquer mecanismo de controle de acesso baseado em roles. O campo `role: 'admin'` no usuário fake é atribuído mas nunca verificado por nenhuma rota.

**`console.log` com informações de autenticação**
As chamadas `console.log('[AUTH] Bypass em desenvolvimento')` e `console.log('[AUTH] Token aceito (sem validação real)')` usam a saída padrão em vez de logger estruturado, impedindo controle de nível de log e rastreabilidade em produção.

---

### 2. Problemas de Arquitetura e Responsabilidade (entregas.js)

**Lógica de negócio misturada na camada de rotas**
O arquivo `src/routes/entregas.js` deveria ser exclusivamente responsável por mapear endpoints HTTP para handlers. No estado atual, contém:
- Geração de número de pedido com `Math.random`
- Cálculo de distância com fórmula euclidiana
- Cálculo de tempo estimado de entrega
- Criação de eventos de rastreamento
- Validação de campos de entrada

O diretório `src/services/` existe no projeto mas não é utilizado nas rotas, evidenciando que a arquitetura planejada (routes → services → models) foi abandonada neste arquivo.

**Query SQL raw na camada de rotas**
O endpoint `GET /stats` executa uma query SQL diretamente via `sequelize.query(...)` dentro da função de rota, acoplando a camada de transporte ao banco de dados sem passar pela camada de service ou model.

**Mistura de `async/await` e callback style**
Quatro endpoints usam `async/await` enquanto três (`/stats`, `/pedido/:numeroPedido`, `DELETE /:id`) usam `.then()/.catch()`. O `DELETE` agrava o problema com promessas aninhadas em três níveis, tornando o fluxo de controle de erros difícil de seguir.

**`console.log` e `console.error` em todos os handlers**
Nenhum dos handlers usa o logger estruturado do projeto. Logs de auditoria como criação de entrega, atribuição de motorista e mudança de status são feitos via `console.log`, sem nível de log configurável e sem campos estruturados para correlação.

---

### 3. Outros Problemas de Código

**Geração de número de pedido não-determinística e sem garantia de unicidade**
`'PED-' + Date.now() + '-' + Math.floor(Math.random() * 1000)` pode gerar colisões em requisições simultâneas. O formato não segue nenhum padrão rastreável (UUID, ULID, sequência controlada).

**Cálculo de distância geograficamente impreciso**
A fórmula euclidiana (`Math.sqrt(dLat² + dLng²) * 111`) trata coordenadas como se estivessem em um plano cartesiano, sem considerar a curvatura da Terra. O erro cresce com a distância entre os pontos.

**Exposição de detalhes internos de erro**
O handler `POST /` retorna `{ error: '...', detalhes: error.message }`, expondo mensagens de exceção do banco de dados ou do ORM diretamente para o cliente.

**Ausência de validação de máquina de estados**
O endpoint `PATCH /:id/status` valida apenas se o status pertence ao conjunto válido, mas não valida transições. É possível, por exemplo, mover uma entrega de `ENTREGUE` de volta para `PENDENTE`.

**Ausência de validação de existência no api-frotas**
O endpoint `PATCH /:id/atribuir` aceita qualquer `veiculo_placa` e `motorista_id` sem verificar se esses recursos existem ou estão disponíveis no `rotalog-api-frotas`.

**Comentários excessivos e redundantes**
O arquivo possui mais de 40 comentários `// FIXME:` que descrevem problemas já visíveis no código, mais blocos JSDoc para cada rota que repetem informações óbvias. Os comentários aumentam o ruído sem agregar contexto.

---

## Decisão

Refatorar `auth.js` e `entregas.js` em múltiplas etapas, restaurando a separação de camadas planejada (routes → services → models) e eliminando as vulnerabilidades de segurança. Nenhuma mudança de contrato REST deve ocorrer como resultado desta refatoração.

### Etapa 1 — Correção do middleware de autenticação

Reescrever `src/middleware/auth.js` para:

- Remover o `JWT_SECRET` hardcoded; ler o segredo exclusivamente de `process.env.JWT_SECRET`
- Remover o bypass por `NODE_ENV`; autenticação deve funcionar igualmente em todos os ambientes
- Usar a biblioteca `jsonwebtoken` para verificar assinatura e expiração do token
- Popular `req.user` com os dados reais decodificados do payload do JWT
- Substituir todos os `console.log` pelo logger estruturado do projeto

### Etapa 2 — Extração de EntregaService

Criar `src/services/EntregaService.js` que absorva toda a lógica de negócio atualmente inline nas rotas:

| Método | Responsabilidade atual na rota |
|---|---|
| `listar(filtros)` | Consulta com filtros e include de rastreamentos |
| `buscarPorId(id)` | Busca por PK com rastreamentos |
| `buscarPorNumeroPedido(numero)` | Busca por campo único |
| `obterEstatisticas()` | Query de agregação por status |
| `criar(dados)` | Geração de número de pedido, cálculo de distância, criação do rastreamento inicial |
| `atualizar(id, dados)` | Atualização de campos com validação |
| `atualizarStatus(id, status)` | Validação de transição de estados e criação de evento |
| `atribuir(id, dados)` | Atribuição de veículo/motorista com criação de evento |
| `cancelar(id)` | Soft delete com validação de estado e criação de evento |

### Etapa 3 — Refatoração de entregas.js

Após a criação de `EntregaService`, simplificar `src/routes/entregas.js` para que cada handler:

- Delegue toda lógica ao `EntregaService`
- Trate apenas o mapeamento de resposta HTTP (status codes e serialização)
- Use exclusivamente `async/await` (remover todos os `.then()/.catch()`)
- Substitua todos os `console.log` / `console.error` pelo logger estruturado
- Remova todos os comentários `// FIXME:` e JSDoc desnecessários

### Etapa 4 — Melhorias de qualidade no EntregaService

Ao implementar `EntregaService`, aplicar as seguintes correções:

- **Número de pedido:** substituir `Math.random` por `crypto.randomUUID()` ou prefixo + nanoid
- **Cálculo de distância:** substituir fórmula euclidiana pela fórmula de Haversine
- **Máquina de estados:** implementar mapa de transições válidas para o campo `status`
- **Erros internos:** remover `detalhes: error.message` das respostas de erro; logar o erro completo apenas no logger
- **Query de estatísticas:** substituir a raw query SQL por `Entrega.findAll` com `attributes` e `group` do Sequelize

### Etapa 5 — Testes unitários

Criar `src/services/EntregaService.test.js` com cobertura mínima de 80% dos métodos públicos:

- Framework: Jest (já configurado no projeto)
- Nomenclatura: `when[Condição]_then[ResultadoEsperado]`
- Estrutura AAA (Arrange / Act / Assert)
- `describe` aninhados para agrupar cenários por método
- Mockar `Entrega` e `Rastreamento` via `jest.mock`
- Cobrir caminho feliz e casos de exceção, especialmente as transições de status inválidas

---

## Consequências

### Positivas

- A remoção do JWT hardcoded e da validação fake elimina a possibilidade de bypass de autenticação por qualquer token arbitrário
- O bypass por `NODE_ENV` removido garante que o comportamento de autenticação seja idêntico em todos os ambientes
- `EntregaService` passa a ter responsabilidade única, tornando cada método testável isoladamente
- A uniformização para `async/await` elimina o callback hell e torna o fluxo de erros previsível
- O logger estruturado substitui `console.log`, permitindo correlação de logs por `requestId` e controle de nível
- A máquina de estados explícita impede transições inválidas que corrompem o ciclo de vida da entrega
- A fórmula de Haversine produz distâncias com erro menor que 0,3%, adequado para cálculo de tempo estimado

### Riscos e Mitigações

| Risco | Mitigação |
|---|---|
| A remoção do bypass por `NODE_ENV` exige que ambientes de desenvolvimento tenham `JWT_SECRET` configurado | Documentar no README e no `docker-compose` do workspace as variáveis de ambiente obrigatórias |
| Mover lógica de cálculo de distância para Haversine pode alterar valores existentes de `distancia_km` | Aplicar Haversine apenas para novas entregas; não recalcular registros históricos |
| A validação de máquina de estados pode rejeitar transições que o `api-frotas` hoje realiza livremente | Mapear todas as chamadas de `PATCH /:id/status` feitas pelo `api-frotas` antes de ativar a validação |
| Extração para `EntregaService` pode introduzir regressões silenciosas sem testes de integração | Implementar os testes unitários da Etapa 5 antes de fazer qualquer merge para main |

### O que não muda

- Contratos REST expostos pelas rotas (nenhum endpoint é alterado, renomeado ou removido)
- Schema do banco de dados e migrations
- Comportamento observável de qualquer endpoint para clientes já integrados

---

## Arquivos Afetados

| Ação | Arquivo |
|---|---|
| Modificar | `src/middleware/auth.js` |
| Criar | `src/services/EntregaService.js` |
| Modificar | `src/routes/entregas.js` |
| Criar | `src/services/EntregaService.test.js` |
| Modificar | `.env.example` (adicionar `JWT_SECRET`) |
