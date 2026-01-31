package br.com.ifba.pagamento.service;

import br.com.ifba.pagamento.entity.Pedido;
import br.com.ifba.usuario.entity.Usuario;

import java.util.List;

public interface PagamentoIService {

    Pedido processOrder(List<Long> ingressosIds, Usuario usuarioLogado);
}
