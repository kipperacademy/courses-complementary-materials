# Word2Vec vs Self-Attention — Demo

Demonstração prática do vídeo **"Por que o Word2Vec não entende contexto? (E como Self-Attention resolveu)"**.

📺 Vídeo: https://youtu.be/xlV_VwItkKA

A demo mostra, com resultados reais, as **duas falhas exatas do Word2Vec** (tabela do Stanford CME 295, slide 64):
1. **Embeddings não são context-aware** — a palavra "banco" tem um único vetor fixo, independente do contexto.
2. **A ordem das palavras não conta** — CBOW trata a frase como um "saco de palavras".

E compara com o **Self-Attention** (Transformer), que gera embeddings contextuais: a mesma palavra, em contextos diferentes, vira vetores diferentes.

## Estrutura

- `word2vec_demo.py` — carrega um Word2Vec PT-BR (NILC, 300 dims) e mostra o vetor **único e colapsado** de "banco".
- `transformer_demo.py` — usa um Transformer multilingue (self-attention) e mostra que cada "banco" tem vetor próprio.
- `requirements.txt` — dependências.

## Como rodar

```bash
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python3 word2vec_demo.py
python3 transformer_demo.py
```

### Modelo Word2Vec PT-BR

O `word2vec_demo.py` espera um modelo no formato `word2vec` texto (uma palavra + vetor por linha, cabeçalho `VOCAB DIMS`).

Baixe o skip-gram 300d do **NILC** (projeto brasileiro) e salve como `skip_s300.txt` na pasta:

```
curl -L -o skip_s300.txt "http://143.107.183.175:22980/download.php?file=embeddings/word2vec/skip_s300.txt"
```

> O arquivo é grande (~1.3 GB) — não versionar no git (ver `.gitignore`).

### Modelo Transformer

O `transformer_demo.py` baixa o modelo multilingue `paraphrase-multilingual-MiniLM-L12-v2` automaticamente na primeira execução (via HuggingFace).

## Resultados reais obtidos

### Word2Vec — "banco" é UM vetor (colapsado pro sentido financeiro)

```
most_similar("banco"): bc, bnu, bch, ex-banco, bamerindus, efisa, ibercorp, bbv, bdp, bndes...
cos(banco, dinheiro) = 0.210
cos(banco, assento)  = 0.131
cos(banco, praça)    = 0.076
```

O sentido "banco de sentar" praticamente **desaparece** — o vetor é dominado pelo sentido financeiro (o mais frequente no corpus).

### Transformer — cada "banco" tem vetor próprio

| Par de frases | cosseno |
|---|---|
| banco(dinheiro) × banco(juros) | 0.311 |
| banco(praça) × banco(jardim) | 0.426 |
| banco(praça) × banco(juros) | **-0.045** |

Os sentidos financeiro e "sentar" se **separam** (quase ortogonais), e cada um fica perto do seu próprio contexto.

## Créditos

- Falhas do Word2Vec: **Stanford CME 295 — Transformers & LLMs** (slide 64).
- Modelo Word2Vec PT-BR: **NILC** (núcleo interinstitucional de linguística computacional, USP).
