# Weather Min Temperature

> **Status**: Done
> **Created**: 2026-05-25

## 1. Business Context

### Problem Statement

A descrição textual de tempo exibida no app (`weather_narrative`) mostra apenas a temperatura máxima do dia — "Máxima: X°C" — omitindo a mínima. O usuário não tem como saber a amplitude térmica do dia sem consultar outra fonte. A API Open-Meteo já retorna `temperature_2m_min`, e o campo só precisa ser solicitado e exibido.

### Goals

- Exibir a temperatura mínima diária ao lado da máxima na descrição textual de tempo.

### User Stories

#### US-1: Ver temperatura mínima na descrição de tempo

- **Story**: Como usuário, quero ver a temperatura mínima na descrição de tempo, para ter uma noção da amplitude térmica do dia sem sair do app.
- **Acceptance Criteria**:
  - **Given** o app carregou dados de tempo para a cidade selecionada, **when** o usuário visualiza o card de tempo, **then** a descrição exibe "Máxima: X°C. Mínima: Y°C." com ambas as temperaturas.
  - **Given** a temperatura mínima é diferente da máxima, **when** o card é exibido, **then** os dois valores são distintos e corretos.
  - **Given** dados de tempo em cache (sem conexão), **when** o card é exibido, **then** a mínima também aparece (narrativa foi salva no BD com o novo formato).

### Key Scenarios

| Cenário | Pré-condições | Passos | Resultado esperado |
|---|---|---|---|
| Happy path — dados frescos | App com conexão, cidade selecionada | Abrir o app | Narrativa exibe "Máxima: 32°C. Mínima: 22°C. Vento…" |
| Dados em cache | Sem conexão, BD com narrativa já gravada | Abrir o app | Mínima aparece normalmente (foi salva na narrativa) |
| Amplitude zero | Mín == Máx | Abrir o app | "Máxima: 25°C. Mínima: 25°C." — sem crash |
| Atualização forçada | Usuário pressiona refresh | Tap no botão refresh | Nova requisição inclui `temperature_2m_min`; narrativa atualizada |

### Functional Requirements

- FR-1: A URL de requisição à API Open-Meteo deve incluir `temperature_2m_min` no parâmetro `daily`.
- FR-2: O modelo `Weather` deve armazenar a temperatura mínima em um novo campo `minTemperature`.
- FR-3: `WeatherService` deve parsear `temperature_2m_min` da resposta e popular o novo campo.
- FR-4: A string `narrative` deve seguir o formato: `{condição} Máxima: {max}°C. Mínima: {min}°C. Vento {dir} a {speed} km/h.`
- FR-5: O schema do BD deve ser atualizado para incluir a nova coluna (`DATABASE_VERSION` incrementado).

### Non-Functional Requirements

- NFR-1: Zero impacto em performance — `temperature_2m_min` é retornado no mesmo payload da requisição existente.
- NFR-2: Nenhuma alteração em layout XML ou outros controllers.

### Out of Scope

- Exibição da mínima em um campo de UI separado (ex: TextView dedicado).
- Histórico de temperaturas ou gráfico de amplitude.
- Temperatura mínima nos dados de mar ou marés.

---

## 2. Arch Decisions

### Proposed Solution

Quatro alterações pontuais, todas dentro do pipeline existente:

1. **`WeatherController`** — adicionar `temperature_2m_min` à URL da API.
2. **`Weather` (model)** — adicionar campo `@DatabaseField int minTemperature` + getter/setter.
3. **`WeatherService`** — parsear o novo array, setar no modelo, e incluir na narrativa.
4. **`WeatherDao`** — incrementar `DATABASE_VERSION` de 1 para 2; `onUpgrade` já dropa e recria a tabela.

### Architecture Overview

```mermaid
flowchart TD
    A[WeatherController.getURL()] -->|inclui temperature_2m_min| B[Open-Meteo API]
    B -->|JSON com min e max| C[WeatherService.callback()]
    C -->|weather.setMinTemperature()| D[Weather model]
    D -->|narrativa com Mínima| E[WeatherDao.addNew()]
    E --> F[WeatherController.populateView()]
    F --> G[weather_narrative TextView]
```

### Alternatives Considered

| Alternativa | Prós | Contras | Veredicto |
|---|---|---|---|
| TextView separado para mínima | Mais visível | Exige alteração de layout e `populateView` | Rejeitado — fora de escopo |
| Buscar mínima em endpoint separado | Nenhum | Segunda requisição HTTP desnecessária; `temperature_2m_min` já está no mesmo payload | Rejeitado |
| Incluir na narrativa (escolhido) | Mínima alteração; consistente com formato atual | Mínima "escondida" no texto | **Aceito** |

### Risks & Mitigations

| Risco | Impacto | Probabilidade | Mitigação |
|---|---|---|---|
| BD existente sem coluna `minTemperature` | Alto — crash ao ler/inserir | Certo (schema antigo) | Incrementar `DATABASE_VERSION`; `onUpgrade` dropa e recria automaticamente |
| Dados em cache com narrativa antiga (sem mínima) | Baixo | Baixo | `onUpgrade` limpa o cache; na próxima abertura os dados são refrescados |

### Key Decisions

#### Decision 1: Incluir a mínima na `narrative` em vez de campo de UI separado

- **Status**: Aceito
- **Context**: O app exibe a temperatura apenas via `narrative` (string formatada). Criar um `TextView` separado exigiria mudança de layout e `populateView`, aumentando o escopo.
- **Decision**: Formatar a mínima diretamente na string de narrativa, mantendo o padrão já estabelecido.
- **Consequences**: A mínima fica no mesmo campo de texto da condição e do vento. Layout inalterado.

#### Decision 2: Bump de `DATABASE_VERSION` para migração

- **Status**: Aceito
- **Context**: OrmLite não adiciona colunas automaticamente. A adição de `minTemperature` em `Weather` requer que a tabela seja recriada.
- **Decision**: Incrementar `DATABASE_VERSION` de 1 para 2 em `WeatherDao`. O `onUpgrade` existente já faz drop + recreate.
- **Consequences**: Dados de tempo em cache são apagados na primeira abertura após o update. Sem impacto prático — o cache é por dia e é refrescado com frequência.

### Implementation Plan

1. `WeatherController.java:25` — adicionar `temperature_2m_min` à URL.
2. `Weather.java` — adicionar campo `minTemperature` com `@DatabaseField`, getter e setter.
3. `WeatherService.java` — parsear `temperature_2m_min`, popular modelo, atualizar narrativa.
4. `WeatherDao.java:32` — `DATABASE_VERSION = 2`.
5. Escrever/atualizar testes unitários que cobrem a narrativa gerada.

---

## 3. Technical Contract

### Data Models

**`Weather.java`** — novo campo:

```java
@DatabaseField
private int minTemperature;

public int getMinTemperature() { return minTemperature; }
public void setMinTemperature(int minTemperature) { this.minTemperature = minTemperature; }
```

### Interfaces

**`WeatherController.java`** — URL atualizada:

```java
private String baseUrl = "https://api.open-meteo.com/v1/forecast"
    + "?daily=weathercode,temperature_2m_max,temperature_2m_min,"
    + "windspeed_10m_max,winddirection_10m_dominant"
    + "&timezone=America/Sao_Paulo&forecast_days=3";
```

**`WeatherService.callback()`** — parsing e narrativa:

```java
JSONArray minTemperatures = daily.getJSONArray("temperature_2m_min");
// ...dentro do loop:
int minTemp = (int) minTemperatures.getDouble(i);
weather.setMinTemperature(minTemp);
weather.setNarrative(parts[1] + " Máxima: " + temp + "°C. Mínima: " + minTemp + "°C. Vento " + dir + " a " + speed + " km/h.");
```

### Integration Points

| Componente | Impacto |
|---|---|
| `WeatherController.java` | URL inclui `temperature_2m_min` |
| `WeatherService.java` | Parseia novo array; atualiza narrativa |
| `Weather.java` | Novo campo `minTemperature` persistido no BD |
| `WeatherDao.java` | `DATABASE_VERSION` 1 → 2; `onUpgrade` dropa e recria |
| `WeatherController.populateView()` | Nenhuma alteração — lê `narrative` como antes |
| Layout XML | Nenhuma alteração |

### Invariants & Constraints

- `minTemperature` deve ser sempre ≤ `temperature` (máxima).
- A narrativa deve conter as duas temperaturas em formato inteiro (sem casas decimais), consistente com o padrão atual.
- `DATABASE_VERSION` após a mudança: **2**.
