# COBOL Error Tracker

## Descrição

O COBOL Error Tracker é um sistema de rastreamento de erros para aplicações COBOL, composto por múltiplos serviços microarquiteturais. Ele permite a produção, consumo, armazenamento e visualização de erros COBOL através de uma API REST e um dashboard web.

## Arquitetura

O projeto é dividido em quatro serviços principais:

- **error-producer**: Serviço responsável por receber e enviar erros COBOL para um tópico Kafka.
- **error-consumer**: Serviço que consome mensagens do Kafka e persiste os erros no banco de dados.
- **error-api**: API REST que fornece endpoints para consultar e gerenciar os erros armazenados.
- **error-dashboard**: Interface web Angular para visualização e análise dos erros.

Além disso, utiliza Docker Compose para orquestração dos serviços e dependências (Kafka, Zookeeper, PostgreSQL).

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 17 ou superior (para os serviços Spring Boot)
- Node.js 18+ e npm (para o dashboard Angular)

## Instalação e Configuração

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd cobol-error-tracker
   ```

2. Execute o script de população do banco (opcional, para dados de exemplo):
   ```bash
   .\populate.ps1
   ```

3. Construa e inicie os serviços com Docker Compose:
   ```bash
   docker-compose up --build
   ```

   Isso iniciará todos os serviços, incluindo Kafka, Zookeeper e PostgreSQL.

## Executando a Aplicação

Após iniciar com Docker Compose:

- **API**: Disponível em `http://localhost:8080`
- **Dashboard**: Disponível em `http://localhost:4200`

### Endpoints da API

- `GET /api/errors`: Lista todos os erros
- `GET /api/errors/{id}`: Detalhes de um erro específico
- `POST /api/errors`: Enviar um novo erro (usado pelo producer)
- `GET /api/dashboard/totals`: Totais para o dashboard
- `GET /api/dashboard/metrics`: Métricas agregadas

### Dashboard

O dashboard Angular permite visualizar:
- Lista de erros
- Detalhes dos erros
- Métricas agregadas
- Correlações entre erros

## Desenvolvimento

### Serviços Individuais

Para desenvolver um serviço específico:

1. Navegue para o diretório do serviço (ex: `cd error-api`)
2. Execute `mvn spring-boot:run` (para serviços Java)
3. Para o dashboard: `cd error-dashboard && npm install && ng serve`

Certifique-se de que as dependências externas (Kafka, DB) estejam rodando via Docker Compose.

## Testes

Execute testes para cada serviço:

- **error-api**: `cd error-api && mvn test`
- **error-consumer**: `cd error-consumer && mvn test`
- **error-producer**: `cd error-producer && mvn test`
- **error-dashboard**: `cd error-dashboard && npm test`

## Troubleshooting

- **Portas ocupadas**: Verifique se as portas 8080, 4200, 9092, 2181, 5432 estão livres.
- **Erros de build**: Certifique-se de que o Docker está rodando e as imagens base estão disponíveis.
- **Conexão com Kafka**: Aguarde alguns segundos após iniciar o Docker Compose para que o Kafka esteja pronto.
- **Banco de dados**: O PostgreSQL é iniciado automaticamente; dados são persistidos em volumes Docker.

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).