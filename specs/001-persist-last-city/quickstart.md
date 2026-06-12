# Quickstart (Fase 1): Persistir Última Cidade Selecionada como Default

**Feature**: `001-persist-last-city` | **Branch**: `2027-persist-last-city`

## Arquivos centrais

| Arquivo | Papel |
|---------|-------|
| `app/src/main/java/com/novoideal/tabuademares/MainActivity.java` | Fluxo de seleção (`createCitySpinner` → `onCitySelected`, linhas ~237-258) e restauração (`createFragmentAdapter` + `getSelectedPosition`, linhas ~173-197) |
| `app/src/main/java/com/novoideal/tabuademares/service/LocationParamService.java` | Orquestra `saveIfNew` + `updateSelected` — ponto da correção |
| `app/src/main/java/com/novoideal/tabuademares/dao/LocationParamDao.java` | `updateSelected` (linhas 139-143), `geLocationParams(city)` (consulta por lat/lng) |
| `app/src/main/java/com/novoideal/tabuademares/model/LocationParam.java` | Flag `selected` e `defaultCity` (Cabo Frio) |

## O defeito em uma frase

Cidades do dataset têm `id = 0`; `updateSelected` faz `update ... where id=0` e não marca nada — então a inicialização sempre cai no fallback (posição 0 = Cabo Frio).

## Build e testes

```bash
# Compilar
./gradlew assembleDebug

# Testes unitários (JVM)
./gradlew test

# Testes instrumentados (exige device/emulador)
./gradlew connectedAndroidTest

# Instalar no device
./gradlew installDebug
```

## Verificação manual (espelha os cenários de aceitação)

1. **US1**: instalar, abrir, selecionar uma cidade ≠ Cabo Frio (ex.: "Fortaleza - CE"), fechar o app por completo (swipe nos recentes), reabrir → deve abrir na cidade escolhida.
2. **US1-AC2**: selecionar outra cidade, reabrir → a mais recente prevalece.
3. **US2**: `adb shell pm clear com.novoideal.tabuademares`, reabrir → Cabo Frio com todas as abas funcionando.
4. **Inspeção do banco** (opcional): `adb shell "run-as com.novoideal.tabuademares sqlite3 databases/tabuaMares_location.db 'select id, name, selected from locationParam;'"` → exatamente uma linha com `selected = 1` após uma seleção.

## Armadilhas conhecidas

- **Não subir `DATABASE_VERSION`**: o `onUpgrade` atual dropa e recria a tabela — apagaria as cidades do usuário. A correção não exige mudança de schema.
- **I/O fora da UI thread**: a gravação da seleção já roda em `Thread` própria; manter insert → resolver id → update na mesma thread para evitar corrida.
- **`refreshOnUserIteration` também grava seleção** (`MainActivity.java:203-206`) com o objeto do tag da view — esse caminho tem o mesmo defeito de id 0 e deve usar a mesma correção.
