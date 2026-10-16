"""Word2Vec demo — mostra que "banco" tem UM vetor fixo e colapsado (não context-aware).

Carrega um Word2Vec PT-BR pré-treinado (formato texto: VOCAB DIMS no cabeçalho,
uma palavra + vetor por linha) e inspeciona o vetor único de "banco".
"""
import sys
import time

import numpy as np
from gensim.models import KeyedVectors

# https://huggingface.co/collections/nilc-nlp/nilc-embeddings
MODEL_PATH = "skip_s300.txt"
DIM = 300


def load_keyed_vectors(path: str, dim: int = DIM) -> KeyedVectors:
    """Carrega word2vec em texto, pulando linhas com dimensão incorreta (corpus NILC é sujo)."""
    t = time.time()
    words, vecs, skipped = [], [], 0
    with open(path, "r", encoding="utf-8") as f:
        f.readline()  # cabeçalho "VOCAB DIMS"
        for line in f:
            parts = line.rstrip("\n").split(" ")
            if len(parts) != dim + 1:
                skipped += 1
                continue
            words.append(parts[0])
            vecs.append(np.array(parts[1:], dtype=np.float32))

    kv = KeyedVectors(vector_size=dim)
    kv.add_vectors(words, np.array(vecs, dtype=np.float32))
    print(f"carregado em {time.time()-t:.1f}s | vocabulario={len(kv.key_to_index)} | skipped={skipped}")
    return kv


def main() -> None:
    model_path = sys.argv[1] if len(sys.argv) > 1 else MODEL_PATH
    kv = load_keyed_vectors(model_path)

    print("\n--- most_similar('banco') top 15 ---")
    for w, s in kv.most_similar("banco", topn=15):
        print(f"  {w:<22} {s:.3f}")

    print("\n--- 'banco' e UM vetor: similaridade com cada sentido ---")
    for w in ["dinheiro", "assento", "praça"]:
        if w in kv:
            print(f"  cos(banco, {w:<8}) = {kv.similarity('banco', w):.3f}")

    print("\nConclusao: 'banco' tem um unico vetor, dominado pelo sentido financeiro.\n"
          "O sentido 'banco de sentar' quase desaparece (nao context-aware).")


if __name__ == "__main__":
    main()
