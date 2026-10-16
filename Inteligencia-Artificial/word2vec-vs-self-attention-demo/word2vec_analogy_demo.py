"""Word2Vec demo — analogia vetorial rei - homem + mulher = rainha.

Mostra os vetores de entrada, a operacao vetorial, o vizinho mais proximo do
resultado e a distancia entre o resultado e o vetor real de "rainha".
"""
import sys

import numpy as np
from gensim.models import KeyedVectors

from word2vec_demo import load_keyed_vectors, MODEL_PATH

WORDS = ["rei", "homem", "mulher", "rainha"]


def print_vector(label: str, vec: np.ndarray, n: int = 10) -> None:
    print(f"{label} (primeiras {n} dims de {vec.shape[0]}):")
    print(f"  {vec[:n]}")


def main() -> None:
    model_path = sys.argv[1] if len(sys.argv) > 1 else MODEL_PATH
    kv: KeyedVectors = load_keyed_vectors(model_path)

    for w in WORDS:
        if w not in kv:
            print(f"palavra '{w}' fora do vocabulario, abortando")
            return

    print("\n--- Vetores de entrada ---")
    vecs = {}
    for w in WORDS:
        vecs[w] = kv[w]
        print_vector(f"vetor('{w}')", vecs[w])

    print("\n--- Operacao: rei - homem + mulher ---")
    result = vecs["rei"] - vecs["homem"] + vecs["mulher"]
    print_vector("resultado", result)

    print("\n--- Vizinho mais proximo do resultado ---")
    # exclui as palavras usadas na operacao pra nao "trapacear" o most_similar
    closest = kv.most_similar(positive=[result], topn=10)
    closest = [(w, s) for w, s in closest if w not in ("rei", "homem", "mulher")]
    for w, s in closest[:5]:
        print(f"  {w:<15} similaridade={s:.3f}")

    top_word, top_score = closest[0]

    print("\n--- Comparacao com o vetor real de 'rainha' ---")
    print_vector("vetor('rainha')", vecs["rainha"])

    dist_euclid = float(np.linalg.norm(result - vecs["rainha"]))
    cos_sim = float(
        np.dot(result, vecs["rainha"])
        / (np.linalg.norm(result) * np.linalg.norm(vecs["rainha"]))
    )
    print(f"\ndistancia euclidiana(resultado, rainha) = {dist_euclid:.3f}")
    print(f"similaridade de cosseno(resultado, rainha) = {cos_sim:.3f}")

    print(
        f"\nConclusao: o vizinho mais proximo do resultado foi '{top_word}' "
        f"(sim={top_score:.3f})."
    )
    if top_word == "rainha":
        print("A analogia rei - homem + mulher = rainha se confirmou.")
    else:
        print("A analogia nao bateu exatamente com 'rainha' neste modelo/corpus.")


if __name__ == "__main__":
    main()
