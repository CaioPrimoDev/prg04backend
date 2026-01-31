package br.com.ifba.pagamento.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;


@Data
public class PedidoRequestDTO {
    // O Front só manda os IDs dos ingressos que já foram reservados (PENDENTE)
    @NotEmpty(message = "A lista de ingressos não pode estar vazia")
    private List<Long> ingressosIds;
}
