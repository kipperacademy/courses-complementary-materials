# Pré-requisitos

1.	**Node.js:** Angular requer Node.js para a execução de seu ambiente de desenvolvimento. O Node.js também inclui o npm (Node Package Manager), que é utilizado para instalar as dependências do projeto.

2.	**Angular CLI:**  Ferramenta de linha de comando que facilita a criação, desenvolvimento e manutenção de projetos Angular. 

**Instalando a CLI**

```bash
npm install -g @angular/cli
```

**Criando novo projeto**

```bash
ng new
```

# Estrutura do projeto

Após criar um novo projeto Angular usando o Angular CLI, a estrutura básica de diretórios e arquivos é a seguinte:

```markdown
**src/:** Contém todo o código fonte da aplicação.

**app/:** Diretório principal da aplicação onde os componentes, serviços e módulos são organizados.

**assets/:** Diretório para armazenar arquivos estáticos, como imagens e fontes.

**environments/:** Contém arquivos de configuração para diferentes ambientes (desenvolvimento, produção, etc.).

**main.ts:** Arquivo de entrada principal que inicializa o módulo principal da aplicação.

**index.html:** Página HTML principal, onde o aplicativo Angular é carregado.

**styles.css:** Arquivo de estilos globais da aplicação.

**angular.json:** Arquivo de configuração do Angular CLI, que define como o projeto é construído e servido.

**package.json:** Lista as dependências do projeto e scripts de build.
```

# NgModule vs Standalone

**O que é um NgModule?**

Um NgModule é uma classe fundamental no Angular, marcada com o decorador **`@NgModule`**. Esse decorador define um objeto de metadados que diz ao Angular como compilar os componentes e criar o injetor em tempo de execução.

**Como NgModule Funciona?**

•	**Agrupamento de Funcionalidades:** Um NgModule agrupa componentes, diretivas e pipes em uma unidade coesa. Isso permite organizar a aplicação em módulos que são fáceis de gerenciar e reutilizar.

•	**Publicação de Funcionalidades:** Ao utilizar a propriedade exports, um NgModule pode tornar componentes, diretivas e pipes disponíveis para outros módulos. Isso permite que funcionalidades específicas sejam reutilizadas em diferentes partes da aplicação.

•	**Injeção de Dependência:** Um NgModule pode registrar serviços que serão injetados em outros componentes ou serviços da aplicação. Por exemplo, você pode adicionar serviços globais que precisam estar disponíveis em toda a aplicação.

```jsx
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
```

**Standalone Components**

Por outro lado, componentes standalone, introduzidos em versões mais recentes do Angular, permitem que você crie componentes, diretivas e pipes que não dependem de um NgModule. Isso pode simplificar o desenvolvimento, especialmente em pequenas aplicações ou em componentes que são usados de forma isolada.

```jsx
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'teste';
}
```

# Criar novos componentes

```bash
ng generate component NOME_AQUI
```

Cada componente tem as seguintes propriedades principais:

- Um decorador**`@Component`** que contém a configuração principal do component
- Um modelo HTML que controla o que é renderizado no DOM
- Um seletor CSS que define como o componente é usado em HTML
- Uma classe TypeScript com comportamentos como gerenciamento de estado, manipulação de entrada do usuário ou busca de dados de um servidor.

# Templates e Data Binding

No Angular, a exibição de dados dinâmicos na view é feita através do **Data Binding**. Isso permite que você vincule dados entre a classe TypeScript (.ts) e o template HTML (.html). Quando os dados na classe TypeScript mudam, a view é automaticamente atualizada para refletir essas mudanças.

O **data binding em si** é o mecanismo que permite sincronizar dados entre o modelo e o template html.

**Interpolação ({{ }})**: Permite a exibição de dados de variáveis no template.

```jsx
<h1>{{ title }}</h1>
```

**Property Binding ([ ])**: Liga uma propriedade DOM a uma expressão.

```jsx
<img [src]="imageUrl">
```

**Event Binding (( ))**: Liga um evento DOM a um método no componente.

```jsx
<button (click)="handleClick()">Click me</button>
```

## **Por que usar Signals ou RxJS?**

**Signals** e **RxJS** são ferramentas poderosas no Angular que ajudam a lidar com a reatividade e o gerenciamento de estado de maneira mais eficiente.

### **RxJS (Reactive Extensions for JavaScript):**

RxJS é uma biblioteca para compor programas assíncronos e baseados em eventos usando observáveis. No contexto do Angular, RxJS é amplamente utilizado para:

•	**Gerenciamento de Fluxos de Dados Assíncronos:** RxJS permite lidar com dados assíncronos, como chamadas HTTP, WebSockets ou eventos do DOM.

•	**Reatividade:** RxJS promove a programação reativa, permitindo que você componha fluxos de dados de forma declarativa e reaja a mudanças de estado em tempo real.

•	**Manipulação de Fluxos Complexos:** Com operadores como map, filter, switchMap, você pode transformar, combinar e manipular fluxos de dados de maneira expressiva.

# Signals

Um signal é um wrapper em torno de um valor que notifica os consumidores interessados quando esse valor muda. Sinais podem conter qualquer valor, de primitivos a estruturas de dados complexas.

```jsx
const count = signal(0);

count.set(3);

count.update(value => value + 1);
```

Usando o valor de um signal

```html
<h3>Counter value {{counter()}}</h3>
```

## Effects

Os `signals` são úteis porque notificam os consumidores interessados quando eles mudam. Um efeito é uma operação que é executada sempre que um ou mais valores de sinal mudam. 

```jsx
effect(() => {
  console.log(`The current count is: ${count()}`);
});
```

Os efeitos sempre são executados pelo menos uma vez. Quando um efeito é executado, ele rastreia qualquer leitura de valor de sinal. Sempre que qualquer um desses valores de sinal muda, o efeito é executado novamente. 

*Semelhante aos sinais computados, os efeitos rastreiam suas dependências dinamicamente e rastreiam apenas sinais que foram lidos na execução mais recente.*

### Casos de uso para efeitos

Efeitos raramente são necessários na maioria dos códigos de aplicativos, mas podem ser úteis em circunstâncias específicas. Aqui estão alguns exemplos de situações em que um efeito pode ser uma boa solução:

- Registrar dados sendo exibidos e quando eles mudam, seja para análise ou como uma ferramenta de depuração.
- Manter dados sincronizados com `window.localStorage`
- Adicionar comportamento DOM personalizado que não pode ser expresso com sintaxe de modelo.
- Executar renderização personalizada para um <canvas>, biblioteca de gráficos ou outra biblioteca de IU de terceiros.

**Por que é importante usar Signals ou RxJS?**

1.	**Reatividade:** Ambos os mecanismos permitem que a aplicação reaja automaticamente a mudanças de dados sem a necessidade de manipulações manuais na DOM, tornando o código mais limpo e mais fácil de manter.

2.	**Manutenção e Escalabilidade:** Em aplicações complexas, o gerenciamento de estado e fluxos de dados pode se tornar complicado. RxJS e Signals ajudam a gerenciar essa complexidade de maneira mais estruturada.

3.	**Eficiência:** Signals e RxJS melhoram a eficiência da aplicação, garantindo que as atualizações na view sejam feitas de maneira otimizada, evitando renderizações desnecessárias e melhorando a experiência do usuário.

# Principais diretivas

As diretivas são uma das funcionalidades mais poderosas e essenciais do Angular. Elas permitem que você adicione comportamento a elementos HTML e manipule o DOM de maneira declarativa. No Angular, as diretivas são classificadas em três tipos principais: diretivas de componentes, diretivas estruturais e diretivas de atributos.

**Condicional if**

```html
@if (a > b) {
  {{a}} is greater than {{b}}
} @else if (b > a) {
  {{a}} is less than {{b}}
} @else {
  {{a}} is equal to {{b}}
}
```

**Iteração for**

```html
@for (item of items; track item.name) {
<li>{{ item.name }}</li>
} @empty {
<li>There are no items.</li>
}
```

# HTTP Fetch

Para realizar chamadas HTTP ao backend, vamos precisar usar um módulo especifico do Angular, o **`HttpClient`.**

O HttpClient é fornecido usando a função auxiliar `provideHttpClient`, que a maioria dos aplicativos inclui nos provedores de aplicativos em`app.config.ts.`

```jsx
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
  ]
};
```

Agora, para usar o HttpClient basta usarmos uma instância para realizar uma requisição.

```jsx
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CatFactsService {

  private apiUrl = 'https://cat-fact.herokuapp.com/facts';

  constructor(private http: HttpClient) { }

  getCatFacts(): Observable<any> {
    return this.http.get(this.apiUrl);
  }
}
```

Agora podemos consumir o valor dentro do componente, usando o `subscribe` para esperar o retorno da chamada e então consumi-lo

```jsx
import { Component, OnInit } from '@angular/core';
import { CatFactsService } from './cat-facts.service';

@Component({
  selector: 'app-cat-facts',
  templateUrl: './cat-facts.component.html',
  styleUrls: ['./cat-facts.component.css']
})
export class CatFactsComponent implements OnInit {

  catFacts: any[] = [];

  constructor(private catFactsService: CatFactsService) { }

  ngOnInit(): void {
    this.catFactsService.getCatFacts().subscribe(
      (data) => {
        this.catFacts = data;
      },
      (error) => {
        console.error('Erro ao obter fatos sobre gatos:', error);
      }
    );
  }
}
```