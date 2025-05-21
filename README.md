
# codigoalvo-estapar-garage-sim

Sistema de gestão de estacionamento inteligente, desenvolvido como parte de um desafio técnico da Estapar. Esta aplicação backend gerencia vagas, setores e eventos de estacionamento com base em dados geoespaciais e eventos de sensores simulados.

## Tecnologias Utilizadas

- Kotlin 1.9.23
- Spring Boot 3.2.5
- PostgreSQL 16 (via Docker)
- Liquibase 4.24.0
- Maven 3.9.9
- Java 21+

## Estrutura do Projeto

```
garage/
├── domain/
│   ├── model/        -> Entidades JPA
│   ├── repository/   -> Repositórios Spring Data
│   └── service/      -> Lógica de negócio (em breve)
├── web/
│   └── controller/   -> Endpoints REST
```

## Lógica de Funcionamento

A aplicação inicia realizando a carga inicial da estrutura da garagem (setores e vagas), que são armazenadas no banco de dados. Após a inicialização, começa a escutar eventos simulados de entrada, estacionamento e saída de veículos via webhook, atualizando o estado interno e registrando faturamento.

## Identificadores

Todas as entidades JPA utilizam `UUID` como tipo de identificador principal (`@Id`). Essa escolha garante unicidade global, facilita testes e elimina dependência de auto-incrementos.

## Controle de Versão do Banco de Dados

O controle do schema do banco de dados é feito com Liquibase utilizando scripts SQL. Os arquivos estão localizados em:

```
src/main/resources/db/changelog/sql/
```

Os arquivos `db.changelog-master.yaml` e `liquibase.properties` são responsáveis por compor os changesets de forma organizada e controlada.

## Lógica de Faturamento

O sistema registra eventos do tipo `ENTRY`, `PARKED` e `EXIT` na entidade `ParkingEvent`. A geração de receita ocorre apenas em eventos do tipo `EXIT`, sendo registrada na entidade `RevenueLog`, que possui um relacionamento `@OneToOne` com o evento correspondente.

Esta modelagem foi propositalmente simplificada para seguir o princípio **KISS** (Keep It Simple, Sr.), mantendo o sistema funcional e elegante sem criar sobrecarga desnecessária.

## Simulador de Eventos

A Estapar disponibiliza um simulador de garagem que envia eventos via webhook. Para utilizá-lo:

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

Após subir o container, ele irá enviar eventos para o endpoint:

```
POST http://localhost:8080/api/parking/...
```

Inicialmente, os eventos são apenas logados. Nas próximas etapas serão processados e persistidos.

## Como Rodar o Projeto

1. Suba o banco de dados com Docker:

```bash
docker-compose up -d
```

2. Execute o projeto via IntelliJ ou CLI:

```bash
./mvnw spring-boot:run
```

3. A aplicação aplicará os scripts do Liquibase e inicializará com sucesso.

## Próximos Passos

- Implementar lógica de precificação dinâmica com base na lotação do setor
- Persistir e consultar eventos e dados de faturamento
- Expor endpoints de consulta para status de placas e vagas
- Criar testes automatizados


## Integração com o Simulador ESTAPAR via Proxy NGINX

Para que sua aplicação receba corretamente os eventos simulados pelo container `garage-sim`, é necessário que ela esteja disponível no endereço `http://localhost:3003/webhook`. No entanto, como sua aplicação principal roda na porta `8080`, foi criado um **proxy reverso com NGINX** para redirecionar essas chamadas.

### Objetivo

Permitir que o simulador, rodando em container Docker, envie requisições para `localhost:3003/webhook` e estas sejam corretamente redirecionadas para `localhost:8080/webhook`, onde a aplicação Spring Boot escuta.

### Estrutura da Pasta `proxy/`

```bash
proxy/
├── nginx-config/
│   └── default.conf         # Configuração do NGINX para proxy reverso
├── run-services.cmd         # Script para Windows para subir os serviços
├── run-services.sh          # Script para Linux para subir os serviços
├── stop-services.cmd        # Script para Windows para parar os serviços
└── stop-services.sh         # Script para Linux para parar os serviços
```

### Liberação de Porta (Windows)

Certifique-se de que a porta `3003` está liberada no firewall. Execute o seguinte comando no PowerShell como Administrador:

```powershell
New-NetFirewallRule -DisplayName "Allow Port 3003" -Direction Inbound -LocalPort 3003 -Protocol TCP -Action Allow
```

### Subindo os Containers

#### Windows

```bash
cd proxy
run-services.cmd
```

#### Linux/Mac

```bash
cd proxy
chmod +x run-services.sh
./run-services.sh
```

Durante a execução, será solicitado o IP local da máquina (detecção automática oferecida no Windows). Esse IP é necessário para o container Docker conseguir resolver o nome `localhost` corretamente.

### Parando os Containers

#### Windows

```bash
cd proxy
stop-services.cmd
```

#### Linux/Mac

```bash
cd proxy
chmod +x stop-services.sh
./stop-services.sh
```

### Configuração da Aplicação

No arquivo `application.yml`, a aplicação está configurada para escutar em todas as interfaces (`0.0.0.0`) e na porta `8080`:

```yaml
server:
  port: 8080
  address: 0.0.0.0
```

Isso garante que o proxy NGINX possa alcançar a aplicação, mesmo quando esta estiver sendo executada dentro da IDE.

### Observações

- Essa abordagem **não exige que a aplicação Spring rode em container**, o que facilita o debug e desenvolvimento local.
- O NGINX atua como intermediário transparente, sendo uma solução robusta e multiplataforma para contornar limitações de rede entre containers Docker e serviços locais.

## Observações sobre a simulação de eventos

Durante a fase de testes, foi identificado que a aplicação de simulação da Estapar pode gerar eventos com intervalo inferior a 1 minuto entre `ENTRY` e `EXIT` para um mesmo veículo.

### Implicações:
- A lógica de cobrança implementada considera **1 minuto como o tempo mínimo de permanência**, conforme validado na `WebhookEventService`.
- Dessa forma, mesmo que o simulador envie uma saída quase imediata após a entrada, **a duração será ajustada para no mínimo 1 minuto** para fins de cálculo e persistência.

### Justificativa:
Essa medida evita o registro de durações nulas ou negativas e garante uma base mínima para cálculo da tarifa, especialmente importante quando regras de cobrança por faixa de ocupação estão em vigor.



## Contato

Cassio Reinaldo Amaral
Analista Desenvolvedor Backend Kotlin/Java  
Email: codigoalvo@gmail.com