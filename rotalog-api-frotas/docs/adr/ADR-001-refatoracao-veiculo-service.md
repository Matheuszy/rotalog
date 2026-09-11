# ADR-001: Refatoração de VeiculoService

- **Status:** Proposed
- **Data:** 2026-04-01
- **Autores:** Time de Engenharia Rotalog

---

## Contexto

A classe `VeiculoService.java` acumula múltiplas responsabilidades em 340 linhas de código e concentra a maior parte dos problemas de dívida técnica identificados no `TECH_DEBT.md` para o serviço `rotalog-api-frotas`. O diagnóstico revela três categorias de risco:

### 1. Problemas de Arquitetura

**God Class com responsabilidades misturadas**
A classe gerencia, ao mesmo tempo, operações CRUD de veículos, validação de entrada, regras de negócio de manutenção preventiva, envio de notificações externas e geração de estatísticas. Isso viola o Princípio da Responsabilidade Única (SRP) e torna qualquer alteração propensa a efeitos colaterais.

**Notificações acopladas à lógica de negócio**
Os métodos `registrarVeiculo`, `atualizarQuilometragem`, `agendarManutencaoPreventiva` e `desativarVeiculo` chamam diretamente `NotificacaoClient`. Isso significa que uma falha no serviço de notificações afeta o fluxo principal de veículos, e que testes unitários de negócio obrigatoriamente precisam mockar infraestrutura.

**Lógica de manutenção preventiva sem separação**
Os métodos `precisaDeManutencao`, `agendarManutencaoPreventiva` e `calcularCustoManutencao` representam um subdomínio distinto de manutenção de veículos, mas vivem no mesmo service de CRUD.

**Estatísticas com design inconsistente**
`obterEstatisticasFreita` (typo documentado no TECH_DEBT.md) realiza quatro queries `findByStatus` e monta o resultado em memória, em vez de usar queries de agregação. O retorno é uma `String` com JSON manual, sem DTO.

### 2. Problemas de Código

**Exceptions genéricas**
Oito ocorrências de `throw new RuntimeException(...)` ao longo da classe, sem distinção entre "veículo não encontrado", "placa duplicada", "quilometragem inválida" e "status inválido". Isso impede que o controller trate cada caso com o HTTP status correto e dificulta o diagnóstico de erros em produção.

**Magic strings para status**
Os literais `"ATIVO"`, `"INATIVO"` e `"MANUTENCAO"` aparecem em seis pontos distintos da classe (linhas 99, 196, 267, 292, 308–310). Um erro de digitação em qualquer um deles passa despercebido em compile time.

**Constantes de negócio hardcoded**
- `intervaloQuilometragem = 10000L` — intervalo de manutenção preventiva (linha 213)
- `intervaloMeses = 3` — declarada mas nunca utilizada (linha 214)
- `limiteQuilometragem = 50000L` — limite para alerta (linha 254)
- `custoPorKm = 0.05` e `custoBase = 500.0` — custo de manutenção (linhas 239–240)
- `"gestor@rotalog.com"` — destinatário hardcoded em quatro chamadas de notificação

**Variáveis declaradas e não utilizadas**
`intervaloMeses` em `agendarManutencaoPreventiva` (linha 214) é declarada e nunca referenciada.

**Mistura de `System.out.println` com SLF4J**
Três ocorrências de `System.out.println` convivem com chamadas ao `log` (linhas 117, 165, 328), quebrando a convenção de logging definida no projeto e impedindo controle por nível de log.

**Javadoc redundante**
Todos os métodos possuem blocos `/** ... */` que repetem o nome do método ou descrevem o óbvio. Vários desses blocos contêm apenas FIXMEs que já constam no corpo do método, duplicando o ruído.

**Injeção de dependência por campo**
`@Autowired` em campos (linhas 28–32) impede declarar dependências como `final` e dificulta a escrita de testes sem frameworks como `@SpringBootTest` ou reflexão.

**Gestão manual de timestamps**
`veiculo.setDataCadastro(LocalDateTime.now())` e `veiculo.setDataAtualizacao(LocalDateTime.now())` repetidos em múltiplos métodos, em vez de usar `@PrePersist` / `@PreUpdate` na entidade.

### 3. Ausência de Testes

O diretório `src/test/java/` está vazio. Não existe nenhuma cobertura para `VeiculoService`, impossibilitando refatorações seguras e aumentando o risco de regressão.

---

## Decisão

Refatorar `VeiculoService.java` e seus arredores em múltiplas etapas, descritas abaixo. Nenhuma mudança de comportamento externo (contratos de API) deve ocorrer como resultado desta refatoração.

### Etapa 1 — Enums de status e canal

Criar `com.rotalog.model.VeiculoStatus` com valores `ATIVO`, `INATIVO`, `MANUTENCAO`. Substituir todas as ocorrências de magic strings na classe.

### Etapa 2 — Exceptions específicas de domínio

Criar em `com.rotalog.exception`:

| Classe | Uso |
|---|---|
| `VeiculoNotFoundException` | Veículo não encontrado por ID ou placa |
| `PlacaDuplicadaException` | Tentativa de cadastrar placa já existente |
| `VeiculoValidacaoException` | Campos obrigatórios ausentes ou inválidos (placa, modelo, ano, quilometragem) |
| `VeiculoStatusInvalidoException` | Status recebido não pertence ao enum `VeiculoStatus` |

Cada exception deve retornar o HTTP status adequado via `@ResponseStatus` ou handler centralizado.

### Etapa 3 — Extração de responsabilidades

Extrair dois novos services:

**`VeiculoNotificacaoService`**
- Encapsula todas as chamadas a `NotificacaoClient`
- Métodos: `notificarNovoVeiculo`, `notificarAlertaManutencao`, `notificarManutencaoAgendada`, `notificarVeiculoDesativado`
- `VeiculoService` passa a depender desta abstração

**`VeiculoManutencaoService`**
- Encapsula regras de negócio de manutenção preventiva
- Métodos: `precisaDeManutencao`, `agendarManutencaoPreventiva`, `calcularCustoManutencao`
- Limites e intervalos migrados para `application.properties` (e lidos via `@Value`)

### Etapa 4 — Refatoração do VeiculoService

Após as etapas anteriores, aplicar no `VeiculoService` resultante:

- Substituir `@Autowired` em campos por injeção via construtor com campos `final`
- Substituir todas as `RuntimeException` pelas exceptions específicas criadas na Etapa 2
- Remover todos os `System.out.println`
- Remover Javadoc redundante
- Remover a variável `intervaloMeses` (não utilizada)
- Corrigir o typo: renomear `obterEstatisticasFreita` → `obterEstatisticasFrota` (com alinhamento no controller)
- Substituir as quatro chamadas `findByStatus(...).size()` em `obterEstatisticasFrota` por queries de contagem no repositório
- Substituir o retorno `String` de estatísticas por um DTO `EstatisticasFrotaResponse`
- Delegar notificações para `VeiculoNotificacaoService`
- Delegar lógica de manutenção para `VeiculoManutencaoService`

### Etapa 5 — Testes unitários

Criar `VeiculoServiceTest` com cobertura mínima de 90% dos métodos públicos de `VeiculoService`:

- JUnit 5 + Mockito
- Nomenclatura: `when[Condição]_then[ResultadoEsperado]`
- Estrutura AAA (Arrange / Act / Assert)
- `@Nested` com `@DisplayName` para agrupar cenários por método
- Cobrir caminho feliz e casos de exceção para cada método público

---

## Consequências

### Positivas

- `VeiculoService` passa de 340 linhas para menos de 150, com uma única responsabilidade
- Exceptions específicas permitem mapeamento preciso para HTTP status codes e mensagens de erro consistentes para clientes da API
- A remoção de magic strings elimina uma classe inteira de bugs silenciosos em tempo de compilação
- A separação de `VeiculoNotificacaoService` permite testar a lógica de negócio de veículos sem mockar infraestrutura de notificação
- Cobertura de 90%+ cria uma rede de segurança para futuras refatorações
- Configurações externalizadas (`@Value`) permitem ajustar limites de manutenção por ambiente sem redeployar

### Riscos e Mitigações

| Risco | Mitigação |
|---|---|
| Renomear `obterEstatisticasFreita` quebra contratos do `VeiculoController` | Atualizar controller na mesma PR; buscar outras referências via `grep` antes de remover |
| Extração de `VeiculoManutencaoService` pode causar dependência circular se `VeiculoService` também for injetado nele | Garantir que `VeiculoManutencaoService` receba apenas o objeto `Veiculo`, não o service |
| Migração de campos `@Autowired` para construtor pode quebrar testes que usam `@SpringBootTest` com reflexão | Todos os novos testes devem usar Mockito puro (`@ExtendWith(MockitoExtension.class)`) |

### O que não muda

- Contratos REST expostos pelo `VeiculoController` (nenhuma alteração de endpoint)
- Schema do banco de dados
- Comportamento observável de qualquer método público

---

## Arquivos Afetados

| Ação | Arquivo |
|---|---|
| Criar | `com/rotalog/model/VeiculoStatus.java` |
| Criar | `com/rotalog/exception/VeiculoNotFoundException.java` |
| Criar | `com/rotalog/exception/PlacaDuplicadaException.java` |
| Criar | `com/rotalog/exception/VeiculoValidacaoException.java` |
| Criar | `com/rotalog/exception/VeiculoStatusInvalidoException.java` |
| Criar | `com/rotalog/service/VeiculoNotificacaoService.java` |
| Criar | `com/rotalog/service/VeiculoManutencaoService.java` |
| Criar | `com/rotalog/dto/EstatisticasFrotaResponse.java` |
| Criar | `src/test/java/com/rotalog/service/VeiculoServiceTest.java` |
| Modificar | `com/rotalog/service/VeiculoService.java` |
| Modificar | `com/rotalog/controller/VeiculoController.java` (renomear endpoint de estatísticas + ajustar injeções) |
| Modificar | `src/main/resources/application.properties` (externalizar constantes de manutenção) |
