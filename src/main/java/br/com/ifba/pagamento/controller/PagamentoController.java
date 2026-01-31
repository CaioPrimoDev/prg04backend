package br.com.ifba.pagamento.controller;

import br.com.ifba.ingresso.repository.IngressoRepository;
import br.com.ifba.pagamento.dto.PedidoRequestDTO;
import br.com.ifba.pagamento.dto.PedidoResponseDTO;
import br.com.ifba.pagamento.service.PagamentoIService;
import br.com.ifba.pagamento.entity.Pedido;
import br.com.ifba.pagamento.entity.StatusPedido;
import br.com.ifba.pagamento.repository.PedidoRepository;
import br.com.ifba.pagamento.api.service.MercadoPagoIService;
import br.com.ifba.ingresso.service.IngressoIService;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final MercadoPagoIService mercadoPagoService;
    private final PedidoRepository pedidoRepository;
    private final IngressoIService ingressoService;
    private final UsuarioRepository usuarioRepository;
    private final PagamentoIService pagamentoService;

    // --- 1. CRIAR PEDIDO E GERAR PIX ---
    @PostMapping("/create")
    public ResponseEntity<PedidoResponseDTO> createPayment(@RequestBody PedidoRequestDTO requestDTO, Principal principal) {

        // 1. Validação de Usuário
        if (principal == null) return ResponseEntity.status(401).build();

        Usuario usuarioLogado = usuarioRepository.findByPessoa_Email(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            // 2. Processa o Cálculo e Cria o Pedido no Banco (Lógica Segura)
            Pedido pedido = pagamentoService.processOrder(
                    requestDTO.getIngressosIds(),
                    usuarioLogado
            );

            // 3. Gera o Link no Mercado Pago usando o valor calculado pelo Back
            String linkPagamento = mercadoPagoService.generatePaymentLink(pedido);

            // 4. Retorna para o Front
            PedidoResponseDTO response = PedidoResponseDTO.builder()
                    .pedidoId(pedido.getId())
                    .valorTotal(pedido.getValorTotal())
                    .status(pedido.getStatus())
                    .linkPagamento(linkPagamento)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build(); // Ou trate o erro melhor
        }
    }

    // --- 2. CONSULTAR STATUS (POLLING) ---
    @GetMapping("/status/{pedidoId}")
    public ResponseEntity<Map<String, StatusPedido>> getStatus(@PathVariable Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .map(pedido -> ResponseEntity.ok(Map.of("status", pedido.getStatus())))
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 3. WEBHOOK (Ajustado para receber JSON) ---
    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<Object> handleWebhook(@RequestBody Map<String, Object> payload) {

        System.out.println("🔔 Webhook recebido! Payload: " + payload);

        try {
            // O formato do JSON varia, mas geralmente vem "type" ou "topic"
            String tipo = (String) payload.get("type");

            // Às vezes o ID vem dentro de um objeto "data"
            String mpPaymentId = null;

            if (payload.get("data") instanceof Map) {
                Map<String, Object> dataPart = (Map<String, Object>) payload.get("data");
                mpPaymentId = (String) dataPart.get("id");
            } else if (payload.get("id") != null) {
                // Em alguns casos (IPN legado), o ID vem na raiz ou como query param,
                // mas vamos focar no formato padrão v1/v2
                mpPaymentId = String.valueOf(payload.get("id"));
            }

            // Se for notificação de pagamento e tivermos um ID
            if ("payment".equals(tipo) && mpPaymentId != null) {

                System.out.println("🔎 Consultando pagamento ID MP: " + mpPaymentId);

                // Consulta a API oficial para ter certeza do status
                Payment payment = mercadoPagoService.checkPayment(mpPaymentId);

                if (payment != null && payment.getExternalReference() != null) {
                    Long pedidoId = Long.valueOf(payment.getExternalReference());
                    Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

                    if (pedidoOpt.isPresent()) {
                        Pedido pedido = pedidoOpt.get();

                        if (pedido.getStatus() == StatusPedido.APROVADO) {
                            return ResponseEntity.ok().build();
                        }

                        // Lógica de atualização
                        if ("approved".equals(payment.getStatus())) {
                            pedido.setStatus(StatusPedido.APROVADO);
                            ingressoService.confirmOrderTickets(pedido.getId());
                            System.out.println("✅ SUCESSO! Pedido #" + pedidoId + " pago e confirmado.");
                        } else if ("rejected".equals(payment.getStatus())) {
                            pedido.setStatus(StatusPedido.REJEITADO);
                        }

                        pedidoRepository.save(pedido);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Retorna OK mesmo com erro nosso para o MP não ficar tentando infinitamente
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.ok().build();
    }
}
