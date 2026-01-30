package br.com.ifba.api.pagamento.service;


import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class MercadoPagoService {

    @Value("${MP_ACCESS_TOKEN}")
    private String accessToken;

    @Value("${MP_WEBHOOK_URL}")
    private String webhookUrl;

    public Map<String, Object> createPixPayment(BigDecimal valorTotal, String emailComprador, Long pedidoId) {
        try {
            // 1. Configura o Token
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();

            // 2. Cria a requisição de pagamento
            PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                    .transactionAmount(valorTotal)
                    .description("Ingressos Cinema - Pedido " + pedidoId)
                    .paymentMethodId("pix") // Fundamental para gerar PIX
                    .notificationUrl(webhookUrl) // Onde o MP vai avisar que pagou
                    .payer(PaymentPayerRequest.builder()
                            .email(emailComprador) // O MP exige um email, mesmo que fake em testes
                            .build())
                    .externalReference(pedidoId.toString()) // Para sabermos qual pedido é esse no Webhook
                    .build();

            // 3. Envia para o Mercado Pago
            Payment payment = client.create(createRequest);

            // 4. Extrai os dados do PIX da resposta
            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("status", payment.getStatus());

            // O "Point of Interaction" contém o QR Code
            if (payment.getPointOfInteraction() != null &&
                    payment.getPointOfInteraction().getTransactionData() != null) {

                response.put("qrCodeBase64", payment.getPointOfInteraction().getTransactionData().getQrCodeBase64());
                response.put("qrCodeCopiaCola", payment.getPointOfInteraction().getTransactionData().getQrCode());
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar pagamento PIX: " + e.getMessage());
        }
    }
}
