---
description: "Lista de tarefas para implementação da feature"
---

# Tarefas: Persistir Última Cidade Selecionada como Default

**Input**: Documentos de design em `/specs/001-persist-last-city/`
**Pré-requisitos**: plan.md (obrigatório), spec.md (histórias de usuário), research.md, data-model.md, contracts/

**Testes**: Incluídos — a estratégia de testes está definida no research.md (R5: JUnit para a lógica de decisão + teste instrumentado para o ciclo salvar→selecionar→restaurar).

**Organização**: Tarefas agrupadas por história de usuário para permitir implementação e teste independentes.

## Formato: `[ID] [P?] [História] Descrição`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependências)
- **[História]**: A qual história de usuário a tarefa pertence (US1, US2, US3)
- Caminhos de arquivo exatos incluídos nas descrições

## Convenções de Caminho

Projeto Android de módulo único:
- Código: `app/src/main/java/com/novoideal/tabuademares/`
- Testes JVM: `app/src/test/java/com/novoideal/tabuademares/`
- Testes instrumentados: `app/src/androidTest/java/com/novoideal/tabuademares/`

---

## Fase 1: Setup (Infraestrutura Compartilhada)

**Propósito**: Confirmar o ambiente de build e a baseline antes de alterar o código.

- [X] T001 Confirmar baseline compilando o projeto com `./gradlew assembleDebug` e rodando `./gradlew test` para garantir que a suíte atual passa antes de qualquer mudança
- [X] T002 [P] Confirmar que existe device/emulador disponível para testes instrumentados com `./gradlew connectedAndroidTest` (ou registrar a indisponibilidade no PR para validação manual) — **nenhum device/adb disponível no ambiente; execução instrumentada fica para validação manual no PR**

---

## Fase 2: Fundação (Pré-requisitos Bloqueantes)

**Propósito**: Criar o método de DAO que resolve a linha persistida (id real do banco) a partir da chave natural (latitude+longetude). Este é o bloco que corrige a causa raiz e do qual todas as histórias dependem.

**⚠️ CRÍTICO**: Nenhuma história de usuário pode ser concluída antes desta fase.

- [X] T003 [P] Adicionar `findPersisted(LocationParam)` em `app/src/main/java/com/novoideal/tabuademares/dao/LocationParamDao.java` que retorna a linha persistida (com `id` real) correspondente a uma cidade por (`latitude`, `longetude`), ou `null` se não existir — reaproveitando a consulta de `geLocationParams(city)`
- [X] T004 Adicionar `saveAndSelect(LocationParam)` em `app/src/main/java/com/novoideal/tabuademares/service/LocationParamService.java` que executa a ordem correta: `saveIfNew` → resolver a linha via `findPersisted` → `updateSelected` usando o **id real** resolvido (depende de T003)

**Checkpoint**: O serviço passa a marcar a seleção na linha correta do banco; histórias podem ser implementadas.

---

## Fase 3: História de Usuário 1 - Restaurar última cidade ao reabrir (Prioridade: P1) 🎯 MVP

**Objetivo**: Após selecionar uma cidade ≠ Cabo Frio e reabrir o app, ele abre na cidade escolhida.

**Teste Independente**: Selecionar "Fortaleza - CE", fechar o app por completo, reabrir → app abre em Fortaleza com todos os painéis.

### Testes para a História de Usuário 1 ⚠️

> Escreva os testes ANTES e garanta que falham antes da implementação.

- [X] T005 [P] [US1] Teste instrumentado do ciclo salvar→selecionar→restaurar em `app/src/androidTest/java/com/novoideal/tabuademares/LocationSelectionDaoTest.java`: inserir cidade vinda do dataset (id 0), chamar `saveAndSelect`, reconsultar e verificar que exatamente uma linha tem `selected=1` e que é a cidade certa (cobre o defeito de id 0) — *escrito; execução requer device*
- [X] T006 [P] [US1] Teste instrumentado de sobrescrita em `app/src/androidTest/java/com/novoideal/tabuademares/LocationSelectionDaoTest.java`: selecionar cidade C, depois D, verificar que só D fica com `selected=1` (RF-005) — *escrito; execução requer device*

### Implementação para a História de Usuário 1

- [X] T007 [US1] Substituir, em `onCitySelected` de `app/src/main/java/com/novoideal/tabuademares/MainActivity.java`, as chamadas separadas `saveIfNew` + `updateSelected` pela nova `saveAndSelect`, mantendo a execução em thread de background; adotar o id real retornado no objeto em memória (depende de T004)
- [X] T008 [US1] Corrigir `refreshOnUserIteration` em `app/src/main/java/com/novoideal/tabuademares/MainActivity.java`: remover a regravação redundante de `updateSelected` (com objeto id 0) que zerava a seleção numa corrida — a persistência passa a ser única em `onCitySelected` via `saveAndSelect` (depende de T004)
- [X] T009 [US1] Garantir em `createFragmentAdapter`/`selectedPositionOf` de `app/src/main/java/com/novoideal/tabuademares/MainActivity.java` que a linha restaurada (com id real) é a usada como `currentLocation` e alimenta todos os painéis (RF-007) — verificado: `currentLocation = locations.get(selectedPosition)` já usa a linha do banco

**Checkpoint**: US1 funcional e testável de forma independente — o MVP da feature.

---

## Fase 4: História de Usuário 2 - Primeiro acesso mantém default funcional (Prioridade: P2)

**Objetivo**: Instalação nova (sem cidade salva) abre em Cabo Frio com todas as abas funcionando.

**Teste Independente**: `adb shell pm clear com.novoideal.tabuademares`, abrir → Cabo Frio ativo e funcional.

### Testes para a História de Usuário 2 ⚠️

- [X] T010 [P] [US2] Teste instrumentado de fallback de primeiro acesso em `app/src/androidTest/java/com/novoideal/tabuademares/LocationSelectionDaoTest.java`: banco vazio → após inicialização, `defaultCity` (Cabo Frio) está semeada e é a localização resolvida (RF-003) — *escrito; execução requer device*

### Implementação para a História de Usuário 2

- [X] T011 [US2] Verificar/preservar em `app/src/main/java/com/novoideal/tabuademares/MainActivity.java` o caminho de banco vazio (`createFragmentAdapter` → `saveIfNew(defaultCity)`) garantindo que a correção da US1 não regrediu o primeiro acesso — preservado dentro do `try` com fallback explícito para `defaultCity`

**Checkpoint**: US1 e US2 funcionam de forma independente.

---

## Fase 5: História de Usuário 3 - Cidade salva inválida não quebra a abertura (Prioridade: P3)

**Objetivo**: Quando a cidade salva não pode ser resolvida (ausente/corrompida), o app abre em Cabo Frio sem crash.

**Teste Independente**: Estado sem nenhuma linha `selected=1` (ou linha inconsistente) → app abre em Cabo Frio (posição 0) sem erro.

### Testes para a História de Usuário 3 ⚠️

- [X] T012 [P] [US3] Teste JUnit da decisão de posição em `app/src/test/java/com/novoideal/tabuademares/MainActivitySelectionTest.java`: dada uma lista sem nenhuma cidade selecionada, `selectedPositionOf` retorna 0 (fallback); dada uma com seleção, retorna o índice correto (RF-004) — **passou em `./gradlew testDebugUnitTest`**

### Implementação para a História de Usuário 3

- [X] T013 [US3] Extrair a lógica de escolha de posição de `getSelectedPosition` em `app/src/main/java/com/novoideal/tabuademares/MainActivity.java` para o método estático testável `selectedPositionOf(List<LocationParam>)`, mantendo o fallback para 0 (habilita T012)
- [X] T014 [US3] Envolver a restauração na inicialização (`createFragmentAdapter`) em `app/src/main/java/com/novoideal/tabuademares/MainActivity.java` com `try/catch` que, em qualquer falha de leitura, degrada para `defaultCity` sem propagar exceção para a UI (RF-004 / CS-005)

**Checkpoint**: Todas as histórias funcionam de forma independente.

---

## Fase 6: Polimento e Questões Transversais

**Propósito**: Validação final e limpeza.

- [X] T015 Rodar `./gradlew test` e `./gradlew connectedAndroidTest` e confirmar suíte verde — **JVM verde (`testDebugUnitTest` OK); `connectedAndroidTest` não executado por falta de device no ambiente**
- [ ] T016 Executar a verificação manual do `specs/001-persist-last-city/quickstart.md` (US1, US1-AC2, US2) em device/emulador — **pendente: requer device (validação no PR)**
- [ ] T017 [P] Inspecionar o banco via adb (query de `select id, name, selected from locationParam`) confirmando a invariante de no máximo uma linha com `selected=1` — **pendente: requer device (validação no PR)**

---

## Dependências e Ordem de Execução

### Dependências entre Fases

- **Setup (Fase 1)**: sem dependências — pode iniciar imediatamente
- **Fundação (Fase 2)**: depende do Setup — **BLOQUEIA** todas as histórias
- **Histórias (Fases 3-5)**: dependem da Fundação concluída
  - US1 (P1) é o MVP e deve vir primeiro
  - US2 e US3 são, em essência, garantias de não-regressão sobre o caminho corrigido — podem ser feitas após a US1
- **Polimento (Fase 6)**: depende das histórias desejadas concluídas

### Dependências entre Histórias

- **US1 (P1)**: depende de T003 + T004 (Fundação). Núcleo da feature.
- **US2 (P2)**: depende da Fundação; valida que a US1 não quebrou o primeiro acesso.
- **US3 (P3)**: depende da Fundação; endurece o fallback. Independente da US2.

### Dentro de Cada História

- Testes escritos e falhando antes da implementação
- Correção de DAO/serviço (Fundação) antes dos ajustes na Activity

### Oportunidades de Paralelismo

- T001 e T002 (Setup) em paralelo
- T003 é [P] dentro da Fundação; T004 depende de T003
- Testes marcados [P] de cada história em paralelo (arquivos distintos ou métodos independentes)
- Após a Fundação, US2 e US3 podem ser tocadas em paralelo por pessoas diferentes

---

## Exemplo de Paralelismo: História de Usuário 1

```bash
# Escrever os testes da US1 juntos (devem falhar antes da correção):
Task: "Teste instrumentado do ciclo salvar→selecionar→restaurar em LocationSelectionDaoTest.java"
Task: "Teste instrumentado de sobrescrita de seleção em LocationSelectionDaoTest.java"
```

---

## Estratégia de Implementação

### MVP Primeiro (apenas US1)

1. Concluir Fase 1: Setup
2. Concluir Fase 2: Fundação (CRÍTICO — corrige a causa raiz)
3. Concluir Fase 3: US1
4. **PARAR e VALIDAR**: testar US1 isoladamente (selecionar cidade, reabrir)
5. Entregar/demonstrar — já resolve o pedido do usuário

### Entrega Incremental

1. Setup + Fundação → base pronta
2. US1 → testar → entregar (MVP: cidade persiste!)
3. US2 → garantir primeiro acesso intacto → entregar
4. US3 → endurecer fallback → entregar

---

## Notas

- [P] = arquivos diferentes, sem dependências
- A "Fundação" aqui é a correção da causa raiz (resolução do id real); sem ela, nenhuma história funciona
- **Não** subir `DATABASE_VERSION` em `LocationParamDao` — o `onUpgrade` atual dropa a tabela e apagaria as cidades do usuário
- Manter a gravação da seleção fora da UI thread (já roda em `Thread` própria no fluxo atual)
- Commit após cada tarefa ou grupo lógico
