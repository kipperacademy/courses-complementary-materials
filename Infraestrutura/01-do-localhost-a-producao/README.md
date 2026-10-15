# API de cursos em Node.js — localhost à produção

API pequena para a aula **Do localhost à produção**. Usa apenas o módulo HTTP nativo do Node.js, sem framework, banco de dados ou dependências externas.

- Rota: `GET /cursos`
- Porta padrão: `8080`
- Resposta: lista JSON com cursos fictícios
- Outros caminhos retornam `404`; métodos diferentes de `GET` retornam `405`

## Rodar com Node.js

Requer Node.js 22 ou superior.

```sh
npm start
```

Em outro terminal:

```sh
curl -i http://localhost:8080/cursos
```

## Rodar com Docker localmente

Construa a imagem e inicie o container com a porta `8080` publicada no computador:

```sh
docker build -t catalogo-cursos-node .
docker run --rm -p 8080:8080 catalogo-cursos-node
```

Ou use a configuração Compose local:

```sh
docker compose -f docker-compose.local.yml up --build -d
curl -i http://localhost:8080/cursos
docker compose -f docker-compose.local.yml down
```

O mapeamento `8080:8080` encaminha a porta 8080 da máquina para a porta 8080 do container. A aplicação escuta em `0.0.0.0` dentro do container para aceitar conexões encaminhadas pelo Docker.

## Publicar na VPS Hostinger

O Dockerfile e o `docker-compose.yml` não dependem de domínio nem de uma rede externa do Traefik. O Compose publica a porta `8080` da VPS e encaminha as requisições para a aplicação:

```sh
docker compose up --build -d
curl http://IP_DA_VPS:8080/cursos
```

Para usar um domínio com HTTPS, configure o DNS e o proxy reverso da VPS separadamente para encaminhar o domínio à porta `8080`.

`PORT` e `HOST` podem ser substituídos por variáveis de ambiente. Os padrões são `8080` e `0.0.0.0`.
