# Especificação de Feature: Persistir Última Cidade Selecionada como Default

**Branch da Feature**: `2027-persist-last-city`
**Criada em**: 2026-06-12
**Status**: Rascunho
**Input**: Descrição do usuário: "Gostaria que a última cidade selecionada ficasse salva e ao reiniciar o aplicativo, ela possa ser utilizada como default. Atualmente a cidade Cabo Frio está sempre como default."

## Cenários de Usuário e Testes *(obrigatório)*

### História de Usuário 1 - Usuário recorrente vê sua última cidade ao abrir o app (Prioridade: P1)

Um usuário que mora em (ou acompanha as condições de) uma cidade diferente de Cabo Frio seleciona sua cidade de interesse. Na próxima vez que abrir o app — minutos ou dias depois — o app já abre exibindo clima, marés, fase da lua e condições do mar para aquela cidade, sem precisar buscar e selecionar novamente.

**Por que esta prioridade**: É o núcleo do pedido. Hoje todo lançamento do app começa em Cabo Frio, obrigando usuários de qualquer outra cidade a repetir o fluxo de busca e seleção a cada sessão. Eliminar essa fricção é todo o valor da feature.

**Teste Independente**: Pode ser totalmente testado selecionando uma cidade diferente de Cabo Frio, fechando completamente o app, reabrindo-o e confirmando que a cidade selecionada anteriormente é carregada como localização ativa.

**Cenários de Aceitação**:

1. **Dado** que o usuário selecionou "Fortaleza" como sua cidade, **Quando** ele fecha e reabre o app, **Então** o app abre exibindo dados de Fortaleza sem nenhuma ação do usuário.
2. **Dado** que o usuário selecionou "Fortaleza" e depois mudou para "Santos", **Quando** ele fecha e reabre o app, **Então** o app abre exibindo dados de Santos (a seleção mais recente prevalece).
3. **Dado** que o usuário selecionou uma cidade e o dispositivo foi reiniciado (não apenas o app), **Quando** ele abre o app, **Então** a última cidade selecionada continua sendo a localização ativa.

---

### História de Usuário 2 - Usuário de primeiro acesso continua com um default funcional (Prioridade: P2)

Um usuário que instala o app e o abre pela primeira vez, sem nunca ter selecionado uma cidade, vê uma cidade default válida (Cabo Frio, o comportamento atual) com todos os dados carregando normalmente.

**Por que esta prioridade**: A feature não pode quebrar a experiência de primeiro acesso. Ainda não existe "última cidade selecionada", então o app deve se comportar exatamente como hoje.

**Teste Independente**: Pode ser testado instalando o app do zero (ou limpando os dados do app) e abrindo-o — o app deve exibir Cabo Frio com todas as abas funcionando.

**Cenários de Aceitação**:

1. **Dado** uma instalação nova sem cidade previamente selecionada, **Quando** o usuário abre o app, **Então** o app exibe Cabo Frio como localização ativa com os dados carregando normalmente.
2. **Dado** que o usuário limpou os dados armazenados do app, **Quando** ele abre o app, **Então** o app retorna ao default Cabo Frio como se fosse um primeiro acesso.

---

### História de Usuário 3 - Cidade salva não está mais disponível (Prioridade: P3)

A cidade salva do usuário, por algum motivo, não existe mais no conjunto de cidades do app (ex.: o dataset embarcado mudou entre versões). O app não pode travar nem exibir uma tela vazia/quebrada — ele retorna à cidade default.

**Por que esta prioridade**: Cenário raro, mas protege o app de travar na abertura — o pior modo de falha possível para uma feature de persistência ligada à inicialização.

**Teste Independente**: Pode ser testado salvando uma referência de cidade que não corresponde a nenhuma entrada do dataset atual e abrindo o app — ele deve abrir em Cabo Frio sem erros.

**Cenários de Aceitação**:

1. **Dado** que a cidade salva não pode ser encontrada no dataset atual de cidades, **Quando** o usuário abre o app, **Então** o app abre em Cabo Frio e o usuário consegue selecionar uma nova cidade normalmente.
2. **Dado** que os dados da cidade salva estão corrompidos ou ilegíveis, **Quando** o usuário abre o app, **Então** o app abre em Cabo Frio sem travar.

---

### Casos de Borda

- O que acontece quando a cidade salva não existe mais no dataset após uma atualização do app? → Retornar à cidade default (Cabo Frio).
- O que acontece quando os dados salvos estão corrompidos ou parcialmente gravados? → Tratar como "nenhuma cidade salva" e retornar ao default.
- O que acontece quando o usuário seleciona uma cidade e o sistema mata o app logo em seguida? → A seleção já deve ter sido salva no momento da seleção, então é restaurada na próxima abertura.
- O que acontece quando o usuário limpa os dados do app pelas configurações do sistema? → O app se comporta como uma instalação nova (default Cabo Frio).
- O que acontece se o usuário selecionar a mesma cidade que já está ativa? → O default salvo permanece sendo essa cidade; nenhum efeito adverso.

## Requisitos *(obrigatório)*

### Requisitos Funcionais

- **RF-001**: O sistema DEVE salvar a cidade selecionada pelo usuário no momento da seleção (não apenas ao sair do app).
- **RF-002**: O sistema DEVE, na abertura do app, usar a última cidade salva como localização ativa.
- **RF-003**: O sistema DEVE retornar à cidade default atual (Cabo Frio) quando não existir cidade salva (primeiro acesso ou dados limpos).
- **RF-004**: O sistema DEVE retornar à cidade default quando a cidade salva não puder ser resolvida contra o dataset atual de cidades, sem travar nem bloquear o usuário.
- **RF-005**: O sistema DEVE sobrescrever a cidade salva anteriormente sempre que o usuário selecionar uma cidade diferente — apenas a seleção mais recente é mantida.
- **RF-006**: A seleção salva DEVE sobreviver a reinicializações do app e do dispositivo.
- **RF-007**: A cidade restaurada DEVE alimentar todos os painéis de dados (clima, marés, fase da lua, condições do mar) exatamente como se o usuário a tivesse selecionado manualmente.

### Entidades-Chave

- **Preferência de Cidade Salva**: A única cidade selecionada mais recentemente, contendo informação suficiente para restaurá-la como localização ativa na abertura (os mesmos atributos usados quando o usuário seleciona uma cidade interativamente — nome, coordenadas geográficas e quaisquer identificadores necessários aos provedores de dados).

## Critérios de Sucesso *(obrigatório)*

### Resultados Mensuráveis

- **CS-001**: Após selecionar qualquer cidade e reiniciar o app, 100% das aberturas iniciam naquela cidade sem nenhuma interação do usuário.
- **CS-002**: Usuários recorrentes precisam de zero toques adicionais para ver os dados da sua cidade de interesse (hoje: abrir busca, digitar, selecionar — pelo menos 3 interações por sessão para usuários fora de Cabo Frio).
- **CS-003**: A experiência de primeiro acesso permanece inalterada: uma instalação nova abre em Cabo Frio com todos os painéis de dados funcionais.
- **CS-004**: A abertura do app com cidade salva não é perceptivelmente mais lenta que a abertura atual (nenhum atraso visível ao usuário adicionado pela restauração da seleção).
- **CS-005**: Nenhum travamento na abertura ocorre por dados de cidade salva ausentes, desatualizados ou corrompidos.

## Premissas

- Cabo Frio permanece como cidade default de fallback; esta feature não introduz uma forma de configurar o fallback.
- Apenas a cidade mais recente é lembrada — histórico de seleções ou lista de "favoritos" está fora de escopo.
- A seleção salva é local ao dispositivo; sincronização entre dispositivos ou contas está fora de escopo.
- Limpar os dados do app pelas configurações do sistema reseta a cidade salva — comportamento aceitável e esperado.
- O app já possui um mecanismo local de persistência para preferências de localização do usuário no qual esta feature pode se apoiar conceitualmente (dados de localização do usuário já são armazenados no dispositivo).
