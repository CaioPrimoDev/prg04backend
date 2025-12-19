package br.com.ifba.ingresso.entity;

public enum StatusIngresso {
    PENDENTE,   // Antiga "ReservaPoltrona"
    CONFIRMADO, // Ingresso vendido
    CANCELADO,   // Pagamento falhou ou tempo expirou
    USADO
}
