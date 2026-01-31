package br.com.ifba.pagamento.api.service;


import br.com.ifba.pagamento.entity.Pedido;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
// Se precisar do objeto de resposta:
//import com.mercadopago.resources.preference.Preference;

import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService implements MercadoPagoIService {

    @Value("${MP_ACCESS_TOKEN}")
    private String accessToken;

    @Value("${MP_WEBHOOK_URL}") // Opcional, se tiver url publica
    private String webhookUrl;

    @Value("${APP_FRONTEND_URL}")
    private String frontendUrl;

    /**
     * Cria a PREFERÊNCIA (Link de Pagamento)
     * Recebe o Pedido já salvo e calculado pelo PagamentoService.
     */
    @Override
    public String generatePaymentLink(Pedido pedido) {
        // 1. LIMPEZA DA URL (O SEGREDO ESTÁ AQUI)
        // Se frontendUrl for null, usamos um valor padrão para não quebrar com NullPointerException
        String urlBase = (frontendUrl != null) ? frontendUrl.trim() : "http://localhost:5173";

        // Remove barra no final se tiver, para evitar "//sucesso"
        if (urlBase.endsWith("/")) {
            urlBase = urlBase.substring(0, urlBase.length() - 1);
        }

        System.out.println("DEBUG - URL LIMPA: " + urlBase);

        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(pedido.getId().toString())
                    .title("Cine The Golden - Pedido #" + pedido.getId())
                    .description("Compra de Ingressos")
                    .pictureUrl("https://cdn-icons-png.flaticon.com/512/2503/2503508.png")
                    .categoryId("entertainment")
                    .quantity(1)
                    .currencyId("BRL")
                    .unitPrice(pedido.getValorTotal())
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            System.out.println("\n\n\nURL BASE: " + frontendUrl + "\n\n\n");
            // 2. USE A VARIAVEL 'urlBase' LIMPA AQUI
            com.mercadopago.client.preference.PreferenceBackUrlsRequest backUrls =
                    com.mercadopago.client.preference.PreferenceBackUrlsRequest.builder()
                            .success("https://nan-trichoid-bunchily.ngrok-free.dev/sucesso")
                            .pending("https://nan-trichoid-bunchily.ngrok-free.dev/pendente")
                            .failure("https://nan-trichoid-bunchily.ngrok-free.dev/falha")
                            .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .externalReference(pedido.getId().toString())
                    .payer(PreferencePayerRequest.builder()
                            .email(pedido.getEmailComprador()) // Garanta que este email é válido!
                            .build())
                    .notificationUrl(webhookUrl + "/pagamentos/webhook")
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            if (accessToken.startsWith("TEST")) {
                return preference.getSandboxInitPoint();
            } else {
                return preference.getInitPoint();
            }

        } catch (MPApiException ex) {
            System.err.println("🚨 ERRO API MP: " + ex.getApiResponse().getContent());
            throw new RuntimeException(ex.getApiResponse().getContent());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Consulta o Status do Pagamento (Usado pelo Webhook)
     * Mantemos o PaymentClient aqui porque o Webhook retorna ID de Payment, não de Preference.
     */
    @Override
    public Payment checkPayment(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            PaymentClient client = new PaymentClient();
            return client.get(Long.parseLong(paymentId));
        } catch (Exception e) {
            System.err.println("Erro ao consultar pagamento ID " + paymentId + ": " + e.getMessage());
            return null;
        }
    }
}
