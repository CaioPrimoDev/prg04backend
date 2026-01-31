package br.com.ifba.ingresso.service;

import br.com.ifba.pagamento.entity.Pedido;
import br.com.ifba.ingresso.entity.Ingresso;

import java.util.List;
import java.util.Optional;

public interface IngressoIService {

    // Core da venda
    Ingresso toReserve(Ingresso ingresso);
    Ingresso confirmPayment(Long id);
    List<String> findPoltronasOcupadas(Long sessaoId);

    // Métodos de gerenciamento (que faltavam)
    Optional<Ingresso> findById(Long id);
    List<Ingresso> findByUsuarioId(Long usuarioId);
    void cancelReservation(Long id, String emailUsuario);

    void linkTicketsToOrder(List<Long> idsIngressos, Pedido pedido);
    void confirmOrderTickets(Long pedidoId);

    void clearUsedTickets();
    void clearCanceledTickets();
    void releaseLockedReservations();
}
