package br.com.ifba.pagamento.dto;

import br.com.ifba.pagamento.entity.StatusPedido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PedidoResponseDTO {
    private Long pedidoId;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private String linkPagamento; // A URL do Mercado Pago
}
