package br.com.kipperdev.clean.entities;

/** Vocabulário interno. Nunca espelha os estados brutos do provedor. */
public enum PaymentStatus {
    CONFIRMED,
    PENDING,
    DECLINED,
    UNKNOWN
}
