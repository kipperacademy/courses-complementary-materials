# Guia de Estudos Javascript

### Repositório que pode auxiliar nos estudos
[GitHub - leonardomso/33-js-concepts: 📜 33 JavaScript concepts every developer should know.](https://github.com/leonardomso/33-js-concepts?tab=readme-ov-file)

# Manipulação da DOM

No HTML podemos inserir tags scripts que vão permitir que o HTML e o JS interagem no navegador

```jsx
<script src="script.js"></script>
```

Basicamente existem 3 fluxos para o Javascript realizar a manipulação da árvore de elementos

html colocando event listener → acionando JS

html injetando script → script invocando uma função sua

html injetando script  → script adicionando um event listener para um evento da DOM, e então executando quando o evento acontecer

### Event listeners via HTML

https://www.w3schools.com/tags/ref_eventattributes.asp

- Uma tag html consegue ouvir um evento do usuário e responder a esse evento, acionando uma função javascript
- Quando o evento acontece, a função é acionada, e a tag html pode passar contexto para função através do objeto event

```jsx

function teste(event){
    console.log(event)
}
```

### Event listeners via Javascript

- O javascript fica escutando um evento e quando ele acontece, aciona uma função

```jsx
document.addEventListener("DOMContentLoaded", function(){
    alert("Tela carregou")
    console.log("eeeee")
    document.getElementById("mainBody").style.backgroundColor = "red"
})
```

### Invocação automática

É quando o próprio script de Javascript aciona a execução de uma função
Essa função será executada assim que esse script for chamado/injetado no HTML

```jsx
function mainFunction(){
    console.log("Main function")

    var h2 = document.createElement("h2");
    h2.innerText = "Oii Live"
    document.body.appendChild(h2);
    document.getElementById("mainBody").appendChild(h2)
}
mainFunction()
```

### Objeto “document”

Para cada página carregada no browser, existe um objeto **`Document`**. A interface `Document` serve como um ponto de entrada para o conteúdo da Página ( a árvore DOM, incluindo elementos como [`<body>`](https://developer.mozilla.org/pt-BR/docs/Web/HTML/Reference/Elements/body) e [`<table>`](https://developer.mozilla.org/pt-BR/docs/Web/HTML/Reference/Elements/table)) e provê funcionalidades globais ao documento (como obter a URL da página e criar novos elementos no documento).

O Javascript por padrão têm acesso á esse elemento e pode usa-lo para interagir com a árvore de elementos como acessar informações, alterar ou remover elementos, injetar elementos…

# Assincronismo

Código assíncrono é um código que não obtém resultado instantaneamente, para isso existe no Javascript uma forma de lidar com esses resultados assíncronos

Existe nomenclatura do `async`/`await`

E a nomemclatura das Promises

As promises são os objetos retornados por um código assíncrono, basicamente eles são a promessa de um resultado futuro, esse resultado pode ser tanto um erro ou um sucesso

```jsx
async function fetchPokedexAPI(){
    const response = await fetch("https://pokeapi.co/api/v2/pokemon/ditto")
    return response.json()
}
```

# Event Loop e Call Stack

O Event Loop é o comportamento do Javascript que permite que ele execute códigos assíncronos de forma não bloqueante.
Ou seja, quando eu tenho um código que não tem resultado instantâneo e outro código que poderia ja ser executado, o Javascript consegue continuar executando mesmo enquanto espera o resultado do código assíncrono ser resolvido.
Ele faz isso através do loop chamado Event Loop e da organização das pilhas de chamada.
Todos códigos síncronos prontos para serem executados são jogados na Call Stack, se esse código aciona outro código, ou seja chama uma outra função, essa função é adicionada na pilha e JS só começa a executa-los quando não há mais nenhuma chamada de função para ser adicionada na pilha e executa do mais recente adicionado a pilha ao mais velho
Depois ele passa para a próxima linha e faz o mesmo processo, se o código é assíncrono cai na task queue (segundo plano) e se for síncrono monta a call stack dele…
O Javascript só vai pegar os resultados da task queue que estiverem prontos para executar quando a pilha de chamadas síncronas estiver vazia

[JS Visualizer 9000](https://www.jsv9000.app/)

[latentflip.com](http://latentflip.com/loupe/)

## Pense no seu eu do futuro e aprenda a investir com a AUVP 🤝

[AUVP - A maior escola de investimentos do Brasil](https://sard.ink/AUVPKipper01)