# Contrato Interno (Fase 1): Persistência da Seleção de Cidade

**Feature**: `001-persist-last-city` | **Data**: 2026-06-12

O app não expõe API externa — este contrato define o comportamento interno entre as camadas (Activity → Service → DAO) que os testes devem verificar.

## C1. `LocationParamService` — gravar seleção

**Operação**: persistir a cidade escolhida e marcá-la como selecionada.

- **Entrada**: `LocationParam` vindo de qualquer origem (dataset embarcado com `id = 0`, ou linha já persistida com id real).
- **Comportamento**:
  1. Se não existir linha com mesmo (`latitude`, `longetude`), inserir.
  2. Resolver a linha persistida correspondente (id real do banco).
  3. Zerar `selected` de todas as linhas e marcar `selected = 1` apenas na linha resolvida.
- **Pós-condições**:
  - Exatamente 1 linha com `selected = 1`, correspondendo à cidade de entrada.
  - Operação idempotente: repetir com a mesma cidade mantém o mesmo estado final.
  - Executada fora da UI thread (responsabilidade do chamador, como hoje).
- **Erros**: falha de SQL não derruba o app; seleção anterior permanece intacta (gravação atômica por linha).

## C2. `MainActivity` — restaurar na inicialização

**Operação**: determinar a localização ativa ao criar a Activity.

- **Comportamento**:
  1. Carregar todas as linhas de `locationParam`.
  2. Lista vazia → semear `LocationParam.defaultCity` (Cabo Frio) e recarregar.
  3. Localização ativa = primeira linha com `selected = true`; se nenhuma, posição 0.
- **Pós-condições**:
  - `currentLocation` aponta para uma linha do banco (nunca nula, nunca objeto sintético sem id).
  - Todos os painéis (clima, marés, lua, mar) recebem essa localização (RF-007).
- **Erros**: qualquer falha de leitura degrada para o fallback (Cabo Frio) sem crash (RF-004 / CS-005).

## C3. Cenários de verificação (mapeiam para a spec)

| Cenário | Contrato | Requisito |
|---------|----------|-----------|
| Selecionar cidade do dataset (id 0) → reabrir → cidade restaurada | C1 + C2 | RF-001, RF-002 / US1 |
| Selecionar C depois D → reabrir → D restaurada, C desmarcada | C1 | RF-005 / US1-AC2 |
| Banco vazio (instalação nova) → Cabo Frio ativo | C2 | RF-003 / US2 |
| Nenhuma linha selecionada → Cabo Frio (posição 0) | C2 | RF-004 / US3 |
| Selecionar a mesma cidade já ativa → estado inalterado, sem erro | C1 (idempotência) | Caso de borda |
