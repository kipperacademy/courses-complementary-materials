package br.com.kipperdev.hexagonal.domain;

/** Vocabulário interno do curso; estados de fornecedor são traduzidos no adaptador. */
public enum PaymentStatus {
    CONFIRMED,
    PENDING,
    DECLINED,
    UNKNOWN
}
