package br.com.ifba.pagamento.service;

import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.entity.TipoIngresso;
import br.com.ifba.ingresso.repository.IngressoRepository;
import br.com.ifba.pagamento.api.service.MercadoPagoService;
import br.com.ifba.pagamento.entity.Pedido;
import br.com.ifba.pagamento.entity.StatusPedido;
import br.com.ifba.pagamento.repository.PedidoRepository;
import br.com.ifba.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagamentoService implements PagamentoIService {

    private final IngressoRepository ingressoRepository;

    private final PedidoRepository pedidoRepository;

    @Transactional
    @Override
    public Pedido processOrder(List<Long> ingressosIds, Usuario usuarioLogado) {

        // 1. Busca os ingressos
        List<Ingresso> ingressos = ingressoRepository.findAllById(ingressosIds);

        if (ingressos.isEmpty()) {
            throw new RuntimeException("Nenhum ingresso encontrado para os IDs informados.");
        }

        BigDecimal valorTotalPedido = BigDecimal.ZERO;

        for (Ingresso ingresso : ingressos) {

            // SEGURANÇA EXTRA: Verifica se o ingresso pertence mesmo ao usuário logado
            // Isso impede que um usuário pague (ou roube) o ingresso de outro
            if (!ingresso.getUsuario().getId().equals(usuarioLogado.getId())) {
                throw new RuntimeException("O ingresso " + ingresso.getId() + " não pertence ao usuário logado.");
            }

            // Valida se já foi pago
            if (ingresso.getPedido() != null &&
                    ingresso.getPedido().getStatus() == StatusPedido.APROVADO) {
                throw new RuntimeException("O ingresso " + ingresso.getId() + " já foi pago.");
            }

            // Acesso seguro: Ingresso -> Sessao -> Filme -> Valor
            BigDecimal valorBaseFilme = ingresso.getSessao().getFilme().getPreco();

            BigDecimal valorItem;

            // Regra da Meia Entrada
            if (ingresso.getTipo() == TipoIngresso.MEIA) {
                valorItem = valorBaseFilme.divide(new BigDecimal("2"));
            } else {
                valorItem = valorBaseFilme;
            }

            // Regra da Taxa de 10%
            BigDecimal taxa = valorItem.multiply(new BigDecimal("0.10"));
            BigDecimal valorFinalIngresso = valorItem.add(taxa);

            // Atualiza histórico no ingresso
            ingresso.setPrecoPago(valorFinalIngresso);

            valorTotalPedido = valorTotalPedido.add(valorFinalIngresso);
        }

        // 3. Cria o Pedido vinculado ao Usuário da Sessão
        Pedido pedido = new Pedido();
        pedido.setValorTotal(valorTotalPedido);

        // MUDANÇA 2: Usa os dados do objeto passado pelo Controller
        pedido.setEmailComprador(usuarioLogado.getPessoa().getEmail());
        pedido.setUsuario(usuarioLogado); // <--- Muito mais seguro!

        pedido.setStatus(StatusPedido.PENDENTE);

        pedido = pedidoRepository.save(pedido);

        // 4. Vincula Ingressos ao Pedido
        for (Ingresso ingresso : ingressos) {
            ingresso.setPedido(pedido);
            ingressoRepository.save(ingresso);
        }

        return pedido;
    }
}
