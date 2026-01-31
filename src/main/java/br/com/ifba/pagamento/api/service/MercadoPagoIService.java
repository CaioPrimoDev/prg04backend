package br.com.ifba.pagamento.api.service;

import br.com.ifba.pagamento.entity.Pedido;
import com.mercadopago.resources.payment.Payment;

public interface MercadoPagoIService {
    String generatePaymentLink(Pedido pedido);
    Payment checkPayment(String paymentId);
}
