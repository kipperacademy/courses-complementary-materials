# O que é o NGINX?

O NGINX é um servidor web, que lida com requisições HTTP

Receber requisições (em um porta) e:

- Redireciona essas requisições para as aplicações mapeadas (transfere a responsabilidade)
- retorna o recurso estático solicitado
- É como um gateway/portão de entrada para nossa arquitetura
    - Outro servidor muito famoso que faz isso é o Apache
- A grande sacada do NGINX é que ele não trabalha com um processo para cada requisição como o Apache faz
- O NGIX trabalha com um processo mãe com alguns workers (normalmente de acordo com a quantidade de núcleos da CPU no qual ele está rodando)
- Os workers executam de forma assíncrona e concorrente, o que traz uma performance muito interessante

## Detalhes sobre o Nginx

Além disso, o NGINX também tem suporte a cache de requisições. Dessa forma a gente consegue reaproveitar dados já retornados uma vez (como por exemplo recursos estáticos) e retornar a mesma resposta sempre que receber a mesma requisição.

Outro ponto positivo de utilizar o NGINX é que expomos para a internet apenas um ponto de entrada para nossa estrutura (todos nossos servidores/aplicações) ficaram "escondidos" atrás do NGINX (que irá receber as requisições e então encaminhar para a aplicação responsável) e dessa maneira mantemos nossas aplicações privadas dentro da nossa rede e menos pontos de contato que diminui chances de ataques.

Um caso onde o NGNIX é muito utilizado é para agregar em um único ponto exposto todos nossos microserviços (conceito de API Gateway)

- Então os clientes do nosso app sempre vão chamar fernandakipper.com/user /pay /checkout, mas no fundo dos panos isso estará sendo redirecionado para cada microserviço responsável por aquele endpoint

Além disso, conseguimos usar o NGINX para trabalhar com comunicação encriptografada. Então o frontend envia para o  servidor NGINX criptografada, e o único que consegue descriptograr é o NGINX.

## Proxy x Proxy Reverso

Sempre quando falamos sobre NGINX o termo surge é PROXY REVERSO

Isso significa que o NGINX responde em nome das nossas aplicações.

Mas para entender como isso funciona, primeiro precisamos entender o termo PROXY

### Proxy

Quando falamos sobre o termo PROXY, significa que ele atua como um middleman, que conecta o nosso computador ou servidor com a internet
Ele filtra e bloqueia essas requisições que saem e chegam antes delas realmente chegarem do outro lado, atuando como um escudo entre a rede privada e rede pública

É isso que as empresas utilizam para bloquear que a gente acesse alguns sites ou faça download de algumas coisas na internet, fazendo com que todo tráfego de internet seja roteado por um proxy.

Ele pode também logar as requisições que estão sendo feitas por cada usuário na rede, pra mostrar quais sites esses usuários estão acessando
Outra coisa que um proxy pode fazer é gerenciar caches, para evitar que requisições iguais gerem trafégo desnecessário, cacheando a resposta por um tempo e retornando ela sem nem mesmo bater no servidor final

### Proxy Reverso

Agora um proxy reverso é o PROXY do lado do servidor
Ou seja, protegendo uma rede de aplicações (um conjunto de servidores que formam uma aplicação, como por exemplo todos os servidores backend do Mercado Livre)

Ele vai ter todas as features de um PROXY, mas agora protegendo os servidores backend, fazendo com que todos eles estejam protegidos na rede privada da infraestrutura aonde foram hospedados e a porta de acesso para a internet seja somente através do PROXY REVERSO. Ou seja, todas requisições que forem enviadas para esses servidores, passaram pelo proxy reverso primeiro, que irá expor um entrypoint e então filtrar e redirecionar o trafego para o servidor responsável

# Como configurar

## Instalando

Existem várias opções para gente instalar e configurar o Nginx

1. Via docker
2. Diretamente no PC

Vamos seguir diretamente pelo PC para que não seja necessário você saber Docker nesse momento

**MAC:**

```bash
// instala
brew install nginx
sudo apt install nginx

// executa
brew services start nginx
```

**WINDOWS:**

1. [Instala o executável](https://nginx.org/en/download.html)
2. Extraia o zip para sua pasta de preferência (vou usar como exemplo a pasta downloads)
3. Executa o arquivo

```bash
cd C:/downloads/nginx
nginx
```

Agora ao abrir http://localhost:8080/ na porta 8080 no seu navegador, você deveria ver uma página como essa:

![Screenshot 2025-08-02 at 11.16.47.png](<Screenshot_2025-08-02_at_11.16.47.png>)

## Arquivo de configuração

Todo servidor de nginx é configurado através de um arquivo de configuração comumente chamado de `nginx.conf`

Quando usamos o comando `nginx -h` para verificar os detalhes do Nginx que acabamos de instalar na nossa máquina, vamos observar o local onde ele foi instalado e onde está o arquivo de configuração default que nosso servidorzinho  está usando

![Screenshot 2025-08-02 at 11.17.34.png](<Screenshot_2025-08-02_at_11.17.34.png>)

Se abrirmos esse arquivo de configuração em um editor de código, por exemplo com o vscode

```bash
code /opt/homebrew/etc/nginx/nginx.conf
```

Veremos que ele se parece com o seguinte:

```bash

worker_processes  1;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;

    server {
        listen       8080;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }

    include servers/*;
}

```


> 🚨 **ATENÇÃO:** Eu omiti as linhas comentadas que vem por padrão, para facilitar nossa visualização das configurações que estão sendo aplicadas. 
Os comentários que vem por padrão no arquivo nginx.conf servem para te guiar para fazer configurações extras ou novas.

Exemplo:

```yaml
#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;
```

## Entendendo as configurações

- `worker_processes`
    - Define o número de processos de trabalho (workers) que o Nginx vai iniciar. Geralmente é definido igual ao número de núcleos de CPU disponíveis, mas aqui está fixado em 1.
- `worker_connections`
    - Define configurações relacionadas aos eventos. O `worker_connections` determina o número máximo de conexões simultâneas que cada processo worker pode aceitar.
- `http` {
    - Início do bloco de configuração do módulo HTTP, que gerencia requisições web.
- `include       mime.types;`
    - Inclui o arquivo `mime.types`, que associa extensões de arquivos a tipos MIME, permitindo que o Nginx envie o `Content-Type` correto nas respostas.
- `default_type  application/octet-stream;`
    - Define o tipo MIME padrão para arquivos cuja extensão não foi reconhecida. `application/octet-stream` é usado para dados binários genéricos.
- `sendfile        on;`
    - Ativa o uso do `sendfile()`, uma chamada de sistema eficiente para envio de arquivos, melhorando a performance.
- `keepalive_timeout  65;`
    - Define o tempo (em segundos) que a conexão ficará aberta aguardando novas requisições antes de ser encerrada.
- `server {`
    - Início do bloco que define um servidor virtual (host).
- `listen       8080;`
    - O servidor irá escutar a porta 8080 por conexões HTTP.
- `server_name  localhost;`
    - Define o nome do servidor (hostname) para correspondência com o `Host` da requisição. Neste caso, aceita apenas `localhost`.
- `location /`
    - Define o comportamento para as requisições que chegarem na raiz do server_name (`/`).
    - `root   html;`
        - Os arquivos serão servidos a partir da pasta `html`
    - `index  index.html index.htm;`
        - e, caso não seja especificado um arquivo, tentará carregar `index.html` ou `index.htm`.
- error_page   500 502 503 504  /50x.html;
    - Define uma página de erro customizada para os códigos de erro 500, 502, 503 e 504. Redireciona para o caminho `/50x.html`.
- location = /50x.html {
    - Especifica onde está localizado o arquivo `/50x.html` no sistema de arquivos, que será servido a partir da pasta `html`
- include servers/*;
    - Inclui todos os arquivos de configuração dentro do diretório `servers/`. Útil para organizar múltiplos blocos `server` separados.

## Nosso primeiro servidor personalizado

Se observamos na última linha do nosso arquivo de configuração, ele indica que está sendo incluído uma pasta `servers`

```bash
    include servers/*;
```

Dentro dessa pasta existe outro arquivo de configuração que incluí servidores personalizados, para isso podemos navegar até a pasta raiz onde está nosso arquivo `nginx.conf`

```bash
 cd /opt/homebrew/etc/nginx/
```

> 🚨 Você vai obter essa pasta raiz executando o comando `nginx -h` e vendo onde ele foi instalado no seu computador

![Screenshot 2025-08-02 at 11.25.52.png](<Screenshot_2025-08-02_at_11.25.52.png>)

E dentro dessa pasta vamos navegar/criar a pasta server, e então criar um arquivo chamado `default.conf`

```bash
 cd /opt/homebrew/etc/nginx/
 mkdir servers
 code .
```

![Screenshot 2025-08-02 at 11.26.54.png](<Screenshot_2025-08-02_at_11.26.54.png>)

Dentro desse arquivo de configuração vamos apontar para uma pasta nova que contenha a nossa página personalizada

```bash
server {
    listen       3000;
    server_name  localhost;

    location / {
        root   /Users/fernandakipper/Desktop/server;
        index  index.html index.htm;
    }

}
```

Você pode fazer o download desse arquivo HTML e colocar na sua pasta de preferência, mas tome bastante cuidado para a sua configuração do nginx apontar para localização exata da pasta onde você colocou o arquivo `index.html`

[index.html](index.html)

[50x.html](50x.html)

No meu caso, eu criei uma pasta server na minha área de trabalho (Desktop) e coloquei o arquivo lá   `/Users/fernandakipper/Desktop/server;`

Agora para reiniciarmos nosso servidor do nginx, para que ele leia essa nova configuração que adicionamos, devemos executar no terminal

```bash
nginx -s reload
```

E acessar a porta `3000`  que foi onde colocamos esse nosso arquivo novo de configuração para ouvir

![Screenshot 2025-08-02 at 11.39.22.png](<Screenshot_2025-08-02_at_11.39.22.png>)

## Configurando um Proxy Reverso

O papel mais comum para servidores Nginx é servir como um proxy reverso, por isso vamos entender como fazer essa configuração.
Ao invés de ter 2 servers nginx rodando, vamos fazer o nosso nginx apontar a requisição, ou seja redirecionar ela, para um servidor que já está rodando na nossa máquina (podendo ser o servidorzinho que criamos antes ou uma nova aplicação).
Para fazer isso é bem simples, quando a requisição chegar na location que queremos ouvir, ao invés de apontar ela para uma pasta com os arquivos da aplicação, vamos direcionar ela para onde o nosso servidor da aplicação está rodando e ouvindo.

Fazemos isso através da config `proxy_pass`

```bash
http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;

    server {
        listen       8080;
        server_name  localhost;

        location / {
            proxy_pass http://localhost:3001;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }

    include servers/*;
}

```

# API Gateway

## O que é?

Podemos usar o Nginx como um API Gateway, onde ele vai atuar como um único ponto de entrada para todos nossos serviços/servidores, e então a partir do ponto de entrada ele redireciona para o servidor correspondente que deve lidar com essa requisição.
**Para o cliente, ele está sempre acessando a mesma URL, só mudando o endpoint
Mas para o servidor nginx, ele está por baixo dos panos roteando para aplicações diferentes**

```bash
http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;

    server {
        listen       8080;
        server_name  localhost;

        location / {
            proxy_pass http://localhost;
        }
        
	      location /api {
            proxy_pass http://localhost:8000/;
        }
        
	      location /auth {
            proxy_pass http://localhost:3200/;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }

    include servers/*;
}
```

> ATENÇÃO: Note que é importante manter na configuração do proxy_pass a barra `/` no final da url Pois dessa maneira o nginx não irá incluir o prefixo `/api` ou `/auth` quando for repassar a requisição para cada servidor correspondente. Ele vai apenas agregar o resto da rota

### Exemplo:

Configuração (com barra):

```bash
  location /api {
      proxy_pass http://localhost:8000/;
  }
```

Acesso:

```bash
http://localhost:8080/api/user ---> redirecionado para
  ---->  http://localhost:8000/user 
```

Configuração (sem barra):

```bash
  location /api {
      proxy_pass http://localhost:8000;
  }
```

Acesso:

```bash
http://localhost:8080/api/user ---> redirecionado para
  ---->  http://localhost:8000/api/user 
```

### Desvantagem

Ao usar API Gateways, nós temos um único ponto de falha para nossa arquitetura distríbuida
Pois ele acaba sendo o centralizador e redirecionador de todas requisições, e caso ele caia, toda minha aplicação cai (mesmo que esteja quebrada em microserviços), pois as requisições não serão enviadas ao microserviço responsável (mesmo que ele continue de pé)

Mas existem já diversas técnicas para monitorar e previnir esse problema. 
O próprio NGINX é um serviço super performático e todo pensado para lidar com essa desvantagem e oferecer o minino de risco possível

# Load Balancing

## O que é?

O NGINX também pode ser usado como um Load Balancer, distribuindo as requisições entre as diferentes instâncias do nosso app cadastradas no nosso NGINX.

- ele faz essa distribuição de requisições para evitar com que uma instância fique muito sobrecarregada enquanto outra está parada (esse é o conceito geral de load balancing)
- esse balanceamento de carga é feito com algoritmos de balancemaento como o round robbin e outras estratégias

## Como configurar

Vamos precisar um bloco de server que ouve uma location e então distribui as requisições dentre as instâncias disponíveis

```bash
upstream available_servers {
	server localhost:8001;
	server localhost:8002;
}

server {
    listen       3001;
    server_name  localhost;

    location / {
        proxy_pass http://available_servers;
    }
}
```

Perceba que nós declaramos um novo bloco, chamado de `upstream`  (que nada mais é que um grupo de servidores) e chamamos de `available_servers`

Agora quando esse nome `available_servers` for referenciado como um destino final, o nginx por padrão já sabe que deve aplicar um algoritmo de balanceamento de carga e enviar hora para o que está na porta 8001 ou 8002

Nesse caso simples, ele faz 50/50