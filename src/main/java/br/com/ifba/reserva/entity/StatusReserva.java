package br.com.ifba.reserva.entity;

public enum StatusReserva {

    // VOU APLICAR AINDA
    PENDENTE,   // Reserva criada, aguardando pagamento (QR Code de pagamento gerado)
    CONFIRMADA, // Pagamento realizado, Ingressos gerados
    CANCELADA,  // Tempo expirou ou pagamento falhou
    UTILIZADA   // (Opcional) Cliente já entrou na sala
}
