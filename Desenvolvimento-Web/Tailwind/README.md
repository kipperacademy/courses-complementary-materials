# Curso de Tailwind para Iniciantes

> ⚠️ Pré-requisito: este conteúdo assume que você **já sabe CSS**. O Tailwind é só uma forma diferente de aplicar propriedades CSS, então o conhecimento das propriedades (margin, padding, display, flex, grid, etc.) é estritamente necessário para entender a fundo como ele funciona.

Tailwind CSS é um **utility framework** (framework utilitário). Em vez de você escrever suas próprias classes CSS em arquivos separados, ele fornece **classes atômicas** que você aplica direto nas tags HTML para montar seus próprios estilos, sem sair do arquivo de marcação.

```html
<!-- CSS tradicional: você sai do HTML e cria um arquivo .css -->
<button class="btn-primary">Enviar</button>

<!-- Tailwind: o estilo vive na marcação, combinando classes atômicas -->
<button class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
  Enviar
</button>
```

> 📌 Diferente do **Bootstrap**, o Tailwind **não te entrega componentes prontos**. Ele te dá classes atômicas para você construir seus próprios estilos e reaproveitá-los ao longo de toda a aplicação. É basicamente uma abstração de classes CSS reutilizáveis.

O Tailwind deixou de ser uma biblioteca usada só por devs que gostam de testar coisas diferentes e virou a biblioteca principal de estilização em muitas empresas grandes. Depois que você entende como usá-lo, ele traz bastante **agilidade e escalabilidade** para as aplicações frontend.

> 🎥 No vídeo: [00:50](https://youtu.be/DL3IPyEXRKU?t=50s)

## Instalação

Existem duas maneiras de adicionar o Tailwind ao projeto:

**1. Via CDN**, serve para projetos só de HTML e é ótima para testar/aprender, mas **não é recomendada para produção**: ao importar via CDN você acaba importando **todas** as classes do Tailwind, mesmo que use só algumas. Isso fica pesado e pouco eficiente.

```html
<head>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
```

**2. Via npm/Yarn (recomendada)**, no momento do **build**, o Tailwind gera o CSS **somente com as classes que você usou** e descarta o resto. O bundle final fica muito menor e mais otimizado.

```bash
npm install tailwindcss @tailwindcss/cli
npx @tailwindcss/cli -i ./src/input.css -o ./dist/output.css --watch
```

No seu CSS de entrada:

```css
@import "tailwindcss";
```

> 💡 Neste guia começamos pela CDN para aprender as classes rápido. A instalação real via npm (com projeto React/Next) está na última seção.

> 🎥 No vídeo: [02:55](https://youtu.be/DL3IPyEXRKU?t=175s)

## Anatomia de uma classe: propriedade + valor

Esta é a ideia central do Tailwind: **toda propriedade CSS tem uma classe equivalente**, e a classe é sempre `propriedade-valor`.

No CSS você faria `margin: 16px`. No Tailwind, a classe da margem é só `m` (não precisa escrever "margin"), seguida do valor: `m-4`.

> ⚠️ O Tailwind **não trabalha com pixels por padrão**, ele usa uma **escala de `rem`**. E `m-4` **não** é `4rem`: cada unidade da escala vale `0.25rem`. Então `m-4` = `4 × 0.25rem` = `1rem` = **16px**.

| Classe | Valor | Em pixels |
| --- | --- | --- |
| `m-0` | 0 | 0px |
| `m-1` | 0.25rem | 4px |
| `m-2` | 0.5rem | 8px |
| `m-4` | 1rem | 16px |
| `m-8` | 2rem | 32px |

> 💡 Essa unidade confunde no começo (a própria instrutora demorou para pegar), mas vira natural com a prática. Em projetos Tailwind você vai encontrar `m-2`, `m-4`, `m-12`, `m-24`... em vez de pixels diretos.

Se você **realmente** precisar de um valor cru (hard coded), use colchetes: `m-[16px]`. Mas o ideal é seguir a escala padrão. (No fundo, tanto o `4` quanto o `red-500` são **variáveis** reutilizadas pelas classes do Tailwind.)

> 🎥 No vídeo: [06:40](https://youtu.be/DL3IPyEXRKU?t=400s)

## Espaçamentos: margin e padding

A margem é `m` e o padding é `p`. Para controlar lados e eixos específicos, você combina sufixos, sempre lembrando que **toda propriedade CSS tem uma classe correspondente**:

| Sufixo | Significado |
| --- | --- |
| `mt-` / `mb-` | margin-top / margin-bottom |
| `ml-` / `mr-` | margin-left / margin-right |
| `mx-` | eixo **X** (esquerda + direita) |
| `my-` | eixo **Y** (cima + baixo) |
| `m-` | todos os lados |

Os **eixos** funcionam como um plano cartesiano: `mx-4` aplica margem só nas laterais (eixo X); `my-4` aplica só em cima e embaixo (eixo Y). Assim você não precisa zerar lados manualmente.

```html
<div class="m-4">margem em todos os lados (1rem)</div>
<div class="mb-0">sem margem embaixo</div>
<div class="mx-4">margem só nas laterais</div>
```

O **padding** segue exatamente a mesma lógica, trocando `m` por `p`: `p-4`, `px-4`, `py-2`, `pl-4`, `pr-4`, `pt-4`, `pb-4`.

E como toda propriedade tem classe equivalente, o **`gap`** (espaçamento entre itens de um flex/grid) também: `gap-4`.

> 💡 Margem automática: igual ao CSS (`margin: auto`), use `m-auto` ou `mx-auto` para centralizar um elemento.

> Só com margem e padding você já resolve grande parte dos espaçamentos.

> 🎥 No vídeo: [06:40](https://youtu.be/DL3IPyEXRKU?t=400s)

## Cores

O sistema de cores é controlado por uma **escala de intensidade**, de `50` (mais clara) a `950` (mais escura). O valor da classe é **composto**: nome da cor + intensidade.

- Fundo: `bg-` → `bg-red-500`, `bg-blue-200`, `bg-blue-700`
- Texto: `text-` → `text-green-300`, `text-green-600`

```html
<div class="bg-red-500">fundo vermelho</div>
<h1 class="text-green-600">texto verde</h1>
```

> ⚠️ `white` e `black` **não têm intensidade**, são só `bg-white`, `text-black`. Já cores como `red` **exigem** a intensidade: `text-red` sozinho **não funciona**, tem que ser `text-red-500`.

Na grande maioria das vezes essa escala já é suficiente. Se precisar de uma cor personalizada pontual, use colchetes com o valor cru: `bg-[#ffffff]`. (Para registrar cores próprias como variáveis, dá para personalizar a configuração do Tailwind.)

> 🎥 No vídeo: [15:25](https://youtu.be/DL3IPyEXRKU?t=925s)

## Textos

### Tamanho da fonte (`text-`)

Diferente dos espaçamentos (que usam números), o tamanho da fonte usa uma escala em palavras: `xs`, `sm`, `base`, `lg`, `xl`, `2xl`... até `9xl`.

| Classe | Significado | Tamanho |
| --- | --- | --- |
| `text-xs` | extra small | 0.75rem (12px) |
| `text-base` | padrão | 1rem (16px) |
| `text-lg` | large | 1.125rem (18px) |
| `text-2xl` ... `text-9xl` | cada vez maior |, |

> 💡 As classes `text-xs`, `text-lg`, etc. já aplicam automaticamente um **`line-height` (leading)** correspondente ao tamanho da fonte, o Tailwind faz esse cálculo para você.

### Estilo e peso da fonte

Alguns estilos são aplicados direto pelo nome: `italic`, `underline`.

> ⚠️ `bold` **sozinho não funciona** como classe, ele é **peso de fonte** (`font-weight`), então é `font-bold`. Pesos vão de fino a grosso: `font-thin`, `font-medium`, `font-bold`.

### Espaçamento entre letras (`tracking-`)

Equivale ao `letter-spacing`. Útil para logos ou textos que pedem espaçamento específico: `tracking-thinner` (bem coladinho) até `tracking-widest` (bem espaçado).

### Altura da linha (`leading-`)

Equivale ao `line-height`. Segue a mesma lógica numérica de margin/padding: `leading-4`, `leading-10`, `leading-20`. Use quando quiser sobrescrever o leading automático do `text-`.

```html
<p class="text-lg font-bold italic tracking-wide leading-10">
  Texto estilizado
</p>
```

> 🎥 No vídeo: [20:25](https://youtu.be/DL3IPyEXRKU?t=1225s)

## Displays: Flexbox e Grid

Cada classe equivale a uma propriedade CSS. A classe `flex` é o equivalente a `display: flex`. **Nada é magia do Tailwind**, é o comportamento padrão do CSS, só com classes equivalentes.

### Flexbox

```html
<!-- só `flex` = display: flex (direção row por padrão, um ao lado do outro) -->
<div class="flex">
  <h1>Título</h1>
  <p>Parágrafo</p>
</div>

<!-- direção coluna -->
<div class="flex flex-col">...</div>

<!-- alinhar e justificar -->
<div class="flex items-center justify-center">...</div>
```

- `flex-row` / `flex-col` → direção
- `items-center` / `items-start` → `align-items`
- `justify-center` / `justify-between` → `justify-content`

> 💡 Lembre que `flex` puro já é `row` (CSS), então os itens ficam lado a lado. Para centralizar **texto** dentro da caixa, use `text-center`, é propriedade de texto, não da caixa.

### Grid

```html
<!-- grid de 3 colunas -->
<div class="grid grid-cols-3 gap-6">
  <div>1</div>
  <div>2</div>
  <div>3</div>
</div>
```

`grid-cols-3` aplica `grid-template-columns: repeat(3, 1fr)` por baixo dos panos. Com `grid-cols-2`, o terceiro elemento "pula" automaticamente para a próxima linha. Também há `grid-rows-*` e `justify-items-center`, entre outros.

> 🎥 No vídeo: [25:25](https://youtu.be/DL3IPyEXRKU?t=1525s)

## Altura e largura

`h-` é a altura (height) e `w-` é a largura (width). Seguem a **mesma escala** de margin/padding.

```html
<div class="h-12 w-24">altura 48px, largura 96px</div>
<div class="h-[200px]">valor cru em pixels</div>
```

Valores especiais:

| Classe | Equivale a |
| --- | --- |
| `h-full` / `w-full` | `100%` do espaço disponível |
| `h-screen` / `w-screen` | `100vh` / `100vw` (tamanho da tela) |

> ⚠️ `h-full` = 100% **do espaço disponível**; `h-screen` = 100% **da tela** (viewport). São coisas diferentes.

Mínimos e máximos seguem o mesmo padrão: `min-h-12`, `max-h-...`, `min-w-...`, `max-w-...`.

> 🎥 No vídeo: [30:25](https://youtu.be/DL3IPyEXRKU?t=1825s)

## Estados (hover, focus, disabled...)

Aqui a montagem da classe muda um pouquinho. Em vez de só `propriedade-valor`, você usa **`estado:propriedade-valor`**. A propriedade só é aplicada quando aquele estado do elemento for verdadeiro.

```html
<button class="bg-blue-600 hover:bg-red-500 hover:text-white hover:scale-105
               disabled:bg-gray-300 transition">
  Clique
</button>
```

- `hover:` → ao passar o mouse
- `disabled:` → quando o elemento está desabilitado
- `focus:`, `active:`, `group-hover:` → outros estados úteis

Você pode aplicar **qualquer** propriedade condicionada ao estado: cor, espaçamento, opacidade (`opacity`), sombra (`shadow`), escala (`scale`)... `transition` deixa a mudança suave.

> 🎥 No vídeo: [34:35](https://youtu.be/DL3IPyEXRKU?t=2075s)

## Responsividade (media queries): mobile-first

O Tailwind já vem com **breakpoints** padrão, pontos de separação que medem o tamanho da tela para decidir qual classe aplicar. Para aplicar uma classe só a partir de um breakpoint, prefixe com ele:

```html
<!-- fundo cinza só em telas grandes (>= 1024px) -->
<div class="lg:bg-gray-200">...</div>

<!-- texto LG no celular, 2xl no tablet, 4xl no desktop -->
<h1 class="text-lg md:text-2xl lg:text-4xl">Responsivo</h1>
```

| Prefixo | A partir de | Corresponde a |
| --- | --- | --- |
| `sm:` | 640px | (pequeno) |
| `md:` | 768px | tablet |
| `lg:` | 1024px | desktop |
| `xl:` | 1280px |, |
| `2xl:` | 1536px |, |

> 💡 **Mobile-first**: classes **sem prefixo** valem para todas as telas (são o seu layout de celular). Os prefixos aplicam **de um breakpoint para cima**. Assim você estiliza o mobile primeiro e só ajusta para telas maiores.

> ⚠️ A instrutora **não recomenda usar `sm:`** para estilos de mobile: o `sm:` cobre só uma faixa específica. Para mobile, deixe a classe como **padrão (sem prefixo)** e use `md:`/`lg:` para crescer. Isso é o verdadeiro mobile-first.

> 🎥 No vídeo: [36:15](https://youtu.be/DL3IPyEXRKU?t=2175s)

## Instalação real em projeto React / Next / Angular

Todas as classes que você viu continuam funcionando igual, o que muda é só a **instalação inicial**.

Num projeto Next/React, o `package.json` traz dois pacotes (como **devDependencies**, porque só são usados no build):

- **`tailwindcss`** → traz todas as classes utilitárias.
- **`@tailwindcss/postcss`** → faz o processamento dessas classes e garante que o build final saia certinho (só com as classes usadas).

No **arquivo global de CSS** do projeto, faça o import, sem isso as classes não funcionam:

```css
@import "tailwindcss";
```

### Mudança importante: `className` no React

No React você usa **`className`** em vez de `class`, porque `class` é palavra reservada do JavaScript:

```jsx
export function Botao() {
  return (
    <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
      Clique
    </button>
  );
}
```

Em Angular (ou HTML puro) continua sendo `class` normalmente.

### Imports por camadas (projetos antigos, antes do V4)

Você pode topar com projetos antigos importando o Tailwind **por camadas**:

```css
@tailwind base;       /* preflight: zera margens/paddings/tamanhos padrão do CSS */
@tailwind components;  /* componentes (de pacotes de componentes Tailwind) */
@tailwind utilities;   /* todas as classes utilitárias */
```

- **base** = *preflight*, que zera os comportamentos padrão do navegador.
- **utilities** = as classes atômicas.
- **components** = só relevante se o projeto usa pacotes de componentes baseados em Tailwind.

> 💡 A partir do **Tailwind v4**, o padrão é só `@import "tailwindcss"`, que já importa todas as camadas e faz a otimização internamente.

> 🎥 No vídeo: [40:50](https://youtu.be/DL3IPyEXRKU?t=2450s)

## Reaproveitando estilos

Quando um conjunto de classes se repete muito:

1. **Componentizar** (React/Angular/Vue): crie um `<Button>` e centralize as classes ali. É a forma recomendada.
2. **`@apply`** no CSS, para casos pontuais:

```css
.btn {
  @apply px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700;
}
```

> ⚠️ Evite usar `@apply` para tudo, isso desfaz a vantagem do utility-first. Prefira componentes.

## Resumo

- Tailwind = **utility framework**: classes atômicas compostas direto no HTML (diferente do Bootstrap, que entrega componentes prontos).
- Toda propriedade CSS tem uma classe equivalente, no formato **`propriedade-valor`**.
- Espaçamento usa **escala de `rem`** onde cada unidade = `0.25rem` (`m-4` = 1rem = 16px). Eixos: `mx-`/`my-`; lados: `mt-`, `mb-`, `ml-`, `mr-`.
- Cores usam **escala de intensidade** (`50`–`950`), valor composto: `bg-red-500`. `white`/`black` não têm intensidade.
- Textos: `text-lg` (size, já traz leading), `font-bold` (peso, não `bold`), `tracking-` (letras), `leading-` (linha).
- Displays: `flex`/`grid` são equivalentes diretos do CSS.
- Tamanho: `h-`/`w-`, mais `full` (100% do espaço) e `screen` (100% da tela).
- Estados via **`estado:propriedade-valor`**: `hover:`, `disabled:`, `focus:`.
- Responsivo **mobile-first**: classe padrão = mobile, prefixos `md:`/`lg:` aplicam de um breakpoint para cima (evite `sm:` para mobile).
- Em React use **`className`**; no v4 basta `@import "tailwindcss"`.
- Para reuso, prefira **componentes** a `@apply`.
