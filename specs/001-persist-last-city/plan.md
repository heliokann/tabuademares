# Plano de Implementação: Persistir Última Cidade Selecionada como Default

**Branch**: `2027-persist-last-city` | **Data**: 2026-06-12 | **Spec**: [spec.md](spec.md)
**Input**: Especificação da feature em `/specs/001-persist-last-city/spec.md`

## Resumo

A última cidade selecionada deve ser restaurada como localização ativa ao reabrir o app, com Cabo Frio apenas como fallback. A análise do código revelou que **o mecanismo de persistência já existe e está parcialmente funcional**: a tabela `locationParam` (SQLite/OrmLite) tem a coluna `selected`, o `MainActivity.getSelectedPosition()` já restaura a cidade marcada na inicialização, e `LocationParamDao.updateSelected()` já grava a seleção. O defeito é que as cidades vindas do dataset embarcado (`CityDatasetLoader`) têm `id = 0`; o fluxo de seleção persiste uma nova linha (que recebe id gerado pelo banco) mas executa `update locationParam set selected=1 where id=0`, que não atinge linha nenhuma. Resultado: nenhuma linha fica com `selected=1` e a inicialização sempre cai na posição 0 (Cabo Frio, primeira linha semeada). A abordagem técnica é **corrigir a resolução do id da linha persistida antes de marcar a seleção**, reaproveitando toda a infraestrutura existente — sem novas dependências, sem mudança de schema.

## Contexto Técnico

**Linguagem/Versão**: Java 11 (Android)
**Dependências Principais**: OrmLite (SQLite ORM), Volley, Joda-Time — nenhuma dependência nova necessária
**Armazenamento**: SQLite via OrmLite — banco `tabuaMares_location.db`, tabela `locationParam` (versão 5), coluna `selected` já existente
**Testes**: JUnit 4 (`src/test/`, JVM) e AndroidJUnit4/Espresso (`src/androidTest/`)
**Plataforma-Alvo**: Android — minSdk 15, targetSdk 34
**Tipo de Projeto**: mobile-app (módulo único `app/`)
**Metas de Performance**: inicialização sem atraso perceptível adicional (a leitura da seleção já ocorre hoje em `createFragmentAdapter()`; nenhuma consulta extra no caminho crítico)
**Restrições**: offline-first — a restauração não pode depender de rede; gravação da seleção fora da UI thread (já é feita em `Thread` separada)
**Escala/Escopo**: 1 tela afetada (`MainActivity`), 2 classes de serviço/DAO, ~660 cidades no dataset embarcado

## Verificação da Constituição

*GATE: deve passar antes da Fase 0. Reavaliado após a Fase 1.*

A constituição do projeto (`.specify/memory/constitution.md`) ainda é o template não ratificado — não há princípios específicos do projeto. Aplicam-se gates de simplicidade por padrão:

| Gate | Status | Observação |
|------|--------|------------|
| Sem novas dependências desnecessárias | ✅ Passa | Reuso de OrmLite/SQLite já existentes |
| Sem mudança de schema/migração | ✅ Passa | Coluna `selected` já existe (DB v5); nenhuma alteração de versão de banco |
| Solução mais simples que atende a spec | ✅ Passa | Corrige o mecanismo existente em vez de introduzir SharedPreferences ou nova tabela |
| Testabilidade | ✅ Passa | Lógica de resolução de id extraível e testável com JUnit |

**Reavaliação pós-design (Fase 1)**: ✅ Sem violações — o design final não adiciona projetos, padrões ou abstrações novas.

## Estrutura do Projeto

### Documentação (desta feature)

```text
specs/001-persist-last-city/
├── plan.md              # Este arquivo (saída do /speckit-plan)
├── research.md          # Saída da Fase 0 (/speckit-plan)
├── data-model.md        # Saída da Fase 1 (/speckit-plan)
├── quickstart.md        # Saída da Fase 1 (/speckit-plan)
├── contracts/           # Saída da Fase 1 (/speckit-plan)
│   └── selection-persistence.md
├── checklists/
│   └── requirements.md
└── tasks.md             # Saída da Fase 2 (/speckit-tasks — NÃO criado pelo /speckit-plan)
```

### Código-Fonte (raiz do repositório)

```text
app/src/main/java/com/novoideal/tabuademares/
├── MainActivity.java                    # Fluxo de seleção e restauração na inicialização (alterar)
├── model/
│   └── LocationParam.java               # Entidade OrmLite com flag `selected` (sem alteração de schema)
├── dao/
│   └── LocationParamDao.java            # `updateSelected()`, `geLocationParams()` (alterar/estender)
├── service/
│   └── LocationParamService.java        # Orquestra salvar + marcar seleção (alterar)
└── util/
    └── CityDatasetLoader.java           # Origem das cidades com id=0 (sem alteração)

app/src/test/java/com/novoideal/tabuademares/
└── service/                             # Testes JUnit da lógica de seleção (novos)

app/src/androidTest/java/com/novoideal/tabuademares/
└── dao/                                 # Teste instrumentado do ciclo salvar→selecionar→restaurar (novo)
```

**Decisão de Estrutura**: Projeto Android de módulo único (`app/`), padrão MVC já documentado no CLAUDE.md. A feature toca exclusivamente as camadas existentes (Activity → Service → DAO); nenhum diretório novo é criado.

## Rastreamento de Complexidade

Nenhuma violação da Verificação da Constituição — tabela não aplicável.
