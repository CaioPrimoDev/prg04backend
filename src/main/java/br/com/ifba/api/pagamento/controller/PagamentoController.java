package br.com.ifba.api.pagamento.controller;

import br.com.ifba.api.pagamento.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final MercadoPagoService mercadoPagoService;

    // endpoint ficticio de 'pedidos' repository
    // @Autowired private PedidoRepository pedidoRepository;

    // 1. FRONTEND CHAMA: GERA O PIX
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> dados) {
        // Exemplo simples recebendo JSON cru
        Double valor = Double.valueOf(dados.get("valor").toString());
        Long pedidoId = Long.valueOf(dados.get("pedidoId").toString());
        String email = dados.get("email").toString();

        Map<String, Object> pixData = mercadoPagoService.createPixPayment(
                BigDecimal.valueOf(valor),
                email,
                pedidoId
        );

        return ResponseEntity.ok(pixData);
    }

    // 2. MERCADO PAGO CHAMA: WEBHOOK
    // O MP envia um POST com query params: ?id=123&topic=payment
    @PostMapping("/webhook")
    public ResponseEntity<?> receiveNotification(@RequestParam Map<String, String> allParams) {
        System.out.println("WEBHOOK RECEBIDO: " + allParams);

        String tipo = allParams.get("type"); // ou 'topic' dependendo da versão
        String idData = allParams.get("data.id"); // Id do pagamento

        if ("payment".equals(tipo) || idData != null) {
            // AQUI VOCÊ CONSULTARIA O STATUS NO MP PARA CONFIRMAR
            // E ATUALIZARIA SEU BANCO DE DADOS
            System.out.println("Pagamento ID: " + idData + " foi atualizado. Indo verificar status...");

            // Lógica fictícia:
            // Payment pagamento = client.get(idData);
            // if (pagamento.getStatus().equals("approved")) {
            //    pedidoService.aprovarPedido(pagamento.getExternalReference());
            // }
        }

        return ResponseEntity.ok().build(); // Retorna 200 OK pro Mercado Pago não ficar tentando de novo
    }
}
