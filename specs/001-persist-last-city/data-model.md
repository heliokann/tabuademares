# Modelo de Dados (Fase 1): Persistir Última Cidade Selecionada como Default

**Feature**: `001-persist-last-city` | **Data**: 2026-06-12

## Entidade: LocationParam (existente — sem mudança de schema)

Tabela `locationParam` no banco `tabuaMares_location.db` (SQLite via OrmLite, `DATABASE_VERSION = 5`). Esta entidade já implementa a "Preferência de Cidade Salva" da spec — nenhum campo novo é necessário.

| Campo | Tipo | Papel nesta feature |
|-------|------|---------------------|
| `id` | int, `generatedId` | Identificador da linha. **Ponto crítico**: objetos vindos do dataset embarcado têm `id = 0` até serem resolvidos contra o banco. |
| `name` | String | Nome exibido (formato `"Cidade - UF"` para cidades do dataset). |
| `selected` | boolean | **Campo central da feature**: marca a cidade default a restaurar na inicialização. |
| `latitude`, `longetude` | Double | Chave natural usada para localizar a linha persistida (`geLocationParams(city)` / `contains()`). |
| `codeSeaCondition`, `woeId` | Integer | Identificadores para provedores de dados; restaurados junto com a linha. |
| `latExtreme`, `longExtreme`, `latWeather`, `longWeather` | Double | Coordenadas refinadas por provedor; preservadas ao restaurar. |
| `tabuademaresPath` | String | Caminho para o scraper de marés; restaurado junto com a linha. |
| `updated` | Date | Timestamp de atualização de dados; usado pelo fluxo de refresh, não pela seleção. |

### Constante relevante

- `LocationParam.defaultCity` — Cabo Frio (`model/LocationParam.java:46-51`). Permanece como semente da tabela vazia e fallback de posição 0. Não é alterada por esta feature.

## Invariantes

1. **No máximo uma linha com `selected = 1`** em qualquer momento (garantida por `updateSelected`, que zera todas antes de marcar uma). Zero linhas selecionadas é estado válido → fallback.
2. **Unicidade lógica de cidade** por par (`latitude`, `longetude`) — garantida por `saveIfNew`/`contains`. A resolução de id usa essa mesma chave natural.
3. **A linha selecionada é autossuficiente** para restauração: todos os dados necessários aos painéis estão na própria linha; não há dependência do dataset embarcado na inicialização.

## Transições de Estado

```text
[Tabela vazia]
    └─ inicialização → semeia defaultCity (Cabo Frio), nenhuma selecionada
[Nenhuma linha selected=1]                                  ← estado atual em produção (defeito)
    └─ inicialização → restaura posição 0 (Cabo Frio)       ← fallback RF-003/RF-004
[Usuário seleciona cidade C]
    ├─ C não existe no banco → INSERT (banco gera id real)
    ├─ resolver id real da linha de C (por latitude+longetude)
    └─ UPDATE: zera selected de todas; marca selected=1 no id resolvido
[Linha C com selected=1]
    └─ inicialização → getSelectedPosition() encontra C → C é a localização ativa (RF-002)
[Usuário seleciona cidade D ≠ C]
    └─ mesmo fluxo → C perde selected, D ganha (RF-005)
```

## Regras de Validação (derivadas dos requisitos)

- **RF-001**: a transição INSERT→resolver→UPDATE ocorre no momento da seleção, em thread de background, na ordem indicada (sem corrida).
- **RF-004**: falha em qualquer passo da restauração (linha ilegível, lista vazia) degrada para o fallback de posição 0 sem lançar exceção para a UI.
- **RF-005**: `updateSelected` permanece a única via de escrita da flag, preservando a invariante 1.
