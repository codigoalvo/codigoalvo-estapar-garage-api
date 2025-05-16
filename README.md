
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

## Contato

Cassio Reinaldo Amaral 
Analista Desenvolvedor Backend Kotlin/Java  
Email: codigoalvo@gmail.com
