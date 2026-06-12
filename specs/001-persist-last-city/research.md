# Pesquisa (Fase 0): Persistir Última Cidade Selecionada como Default

**Feature**: `001-persist-last-city` | **Data**: 2026-06-12

Não havia marcadores NEEDS CLARIFICATION na spec nem no Contexto Técnico. Esta pesquisa consolida a análise do código existente e as decisões de abordagem.

## R1. Diagnóstico do comportamento atual (por que Cabo Frio é sempre o default)

**Fatos verificados no código** (leitura direta, não hipótese):

1. `LocationParam` possui o campo `@DatabaseField private boolean selected` (`model/LocationParam.java:41`).
2. `LocationParamDao.updateSelected()` (`dao/LocationParamDao.java:139-143`) zera `selected` de todas as linhas e marca `selected=1` **onde `id = city.getId()`**.
3. `MainActivity.getSelectedPosition()` (`MainActivity.java:173-182`) já procura a linha com `selected=true` na inicialização e usa a posição 0 como fallback.
4. As cidades exibidas no diálogo de busca vêm de `CityDatasetLoader.load()` (`MainActivity.java:232`), que cria objetos `LocationParam` **sem id** (campo `generatedId` fica 0, pois o objeto nunca passou pelo banco).
5. No fluxo de seleção (`MainActivity.java:248-254`), `saveIfNew(selected.clone(0))` insere uma nova linha (o banco gera um id real), mas em seguida `updateSelected(cityWithDay)` usa o clone em memória com `id = 0`.

**Hipótese de causa raiz (alta confiança, a confirmar com teste)**: o `update locationParam set selected=1 where id=0` não atinge nenhuma linha (ids gerados pelo OrmLite começam em 1). Nenhuma linha fica marcada, `getSelectedPosition()` retorna 0, e a posição 0 é Cabo Frio (primeira linha, semeada por `LocationParamDao.onCreate`). O mesmo defeito existe em `refreshOnUserIteration()` (`MainActivity.java:203-206`), que também chama `updateSelected` com o objeto do tag da view (id 0 quando a cidade veio do dataset).

**Implicação**: a feature pedida é, na prática, a correção deste defeito + garantias de fallback. Não é necessário criar mecanismo novo de persistência.

## R2. Mecanismo de persistência

- **Decisão**: Reutilizar e corrigir o mecanismo existente — flag `selected` na tabela `locationParam` (SQLite/OrmLite). Após `saveIfNew`, resolver a linha real do banco (consulta por `latitude` + `longetude`, já disponível em `LocationParamDao.geLocationParams(city)`) e marcar a seleção usando o **id do banco**, não o id do objeto em memória.
- **Justificativa**:
  - Toda a infraestrutura (coluna, DAO, restauração na inicialização) já existe; a correção é pontual e de baixo risco.
  - A restauração precisa do `LocationParam` completo (coordenadas, `tabuademaresPath`, `codeSeaCondition`, `updated`), que já mora nessa tabela — qualquer outro mecanismo ainda dependeria dela.
  - Zero mudança de schema: não é preciso subir `DATABASE_VERSION` (o `onUpgrade` atual dropa e recria a tabela, o que apagaria as cidades salvas do usuário — evitá-lo é um benefício direto).
- **Alternativas consideradas**:
  - **SharedPreferences** guardando identificador/coordenadas da última cidade: rejeitada — cria segunda fonte de verdade para o mesmo dado, exige lógica extra de reconciliação com a tabela na inicialização e não elimina a necessidade de corrigir `updateSelected` (usado também no refresh).
  - **Nova tabela/registro de preferência**: rejeitada — complexidade desproporcional para guardar um único valor que já tem coluna dedicada.
  - **Corrigir apenas o `where` do update para usar lat/lng em vez de id**: viável, mas resolver o id real e mantê-lo no objeto em memória também corrige consultas subsequentes (`getById`, `touch`, `updateExtremeParams`) que sofrem do mesmo problema de id 0 — escolhida a resolução de id por ser correção mais completa com o mesmo custo.

## R3. Estratégia de fallback (RF-003 / RF-004)

- **Decisão**: Manter o fallback existente, que já cobre os requisitos: tabela vazia → `saveIfNew(LocationParam.defaultCity)` semeia Cabo Frio; nenhuma linha com `selected=1` → `getSelectedPosition()` retorna posição 0 (Cabo Frio). Garantir por teste que esses caminhos não regridem.
- **Justificativa**: comportamento atual já é o desejado para primeiro acesso e dados limpos; a feature só precisa não quebrá-lo.
- **Alternativas consideradas**: validação ativa da cidade salva contra o dataset na inicialização — rejeitada como gate obrigatório, pois a linha do banco é autossuficiente (não depende do dataset para renderizar); a cidade "inexistente no dataset" continua funcional ou, no pior caso (linha corrompida/ausente), cai no fallback de posição 0.

## R4. Momento da gravação (RF-001)

- **Decisão**: Manter a gravação no momento da seleção (`onCitySelected`), em thread de background, como já é feito — apenas corrigindo a ordem: inserir → resolver id → marcar selecionado, dentro da mesma thread para evitar corrida entre insert e update.
- **Justificativa**: atende "salvar no momento da seleção, não ao sair do app" e mantém o trabalho de I/O fora da UI thread (restrição do Android).
- **Alternativas consideradas**: gravar em `onPause`/`onStop` — rejeitada; o sistema pode matar o processo antes, violando RF-001.

## R5. Estratégia de testes

- **Decisão**:
  - **JUnit (src/test)**: extrair a lógica de "resolver linha persistida e decidir seleção" para método testável; testar seleção, sobrescrita de seleção anterior (RF-005) e fallback de posição (RF-003).
  - **Instrumentado (src/androidTest)**: teste de ciclo completo no DAO real — inserir cidade do dataset (id 0), marcar selecionada, reabrir consulta e verificar que `getSelected()` é true na linha certa e única (invariante: no máximo uma linha selecionada).
- **Justificativa**: o defeito está exatamente na fronteira objeto-em-memória ↔ banco; o teste instrumentado cobre essa fronteira, e os testes JVM cobrem a lógica de decisão.
