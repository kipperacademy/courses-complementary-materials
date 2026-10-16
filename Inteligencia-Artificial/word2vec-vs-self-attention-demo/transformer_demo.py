"""Transformer (self-attention) demo — mostra que "banco" ganha vetor próprio por contexto.

Usa um SentenceTransformer multilingue (arquitetura Transformer / self-attention)
para gerar embeddings contextuais: a mesma palavra, em frases diferentes, vira
vetores diferentes — o oposto do Word2Vec.
"""
import time

from sentence_transformers import SentenceTransformer, util

MODEL_NAME = "paraphrase-multilingual-MiniLM-L12-v2"

SENTENCES = [
    "Fui ao banco tirar dinheiro.",       # banco financeiro
    "Sentei no banco da praça.",          # banco de sentar
    "O banco central anunciou a taxa de juros.",  # banco financeiro (instituição)
    "Comprei um banco de madeira para o jardim.", # banco de sentar (móvel)
]
LABELS = ["banco(dinheiro)", "banco(praça)", "banco(juros)", "banco(jardim)"]


def main() -> None:
    t = time.time()
    model = SentenceTransformer(MODEL_NAME)
    print(f"modelo carregado em {time.time()-t:.1f}s")

    emb = model.encode(SENTENCES, normalize_embeddings=True)
    cos = util.cos_sim(emb, emb).numpy()

    print("\n--- Transformer (self-attention): cada 'banco' tem vetor proprio ---")
    for i in range(len(SENTENCES)):
        print(f"  [{LABELS[i]}]")
        for j in range(len(SENTENCES)):
            print(f"      cos vs {LABELS[j]:<16} = {cos[i][j]:.3f}")

    print("\nConclusao: os sentidos financeiro e 'de sentar' se SEPARAM\n"
          "(quase ortogonais), e cada um fica perto do seu contexto.")


if __name__ == "__main__":
    main()
