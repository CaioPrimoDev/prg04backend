package br.com.ifba.api.service;

import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.pagamento.entity.Pedido;

import java.util.List;

public interface EmailIService {
    void sendTickets(Pedido pedido, List<Ingresso> ingressos);
}
