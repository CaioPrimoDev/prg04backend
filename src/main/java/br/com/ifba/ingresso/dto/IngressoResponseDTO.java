package br.com.ifba.ingresso.dto;

import br.com.ifba.ingresso.entity.StatusIngresso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngressoResponseDTO {

    private Long id;
    private String codigoPoltrona;
    private StatusIngresso status;
    private LocalDateTime dataReserva;
    private BigDecimal precoPago;

    // Dados achatados da Sessão/Filme para exibição fácil
    private String nomeFilme;       // Mapeado de ingresso.getSessao().getFilme().getTitulo()
    private String nomeSala;        // Mapeado de ingresso.getSessao().getSala().getNome()
    private LocalDateTime dataSessao;

    // Dados do Usuário
    private String nomeUsuario;
}
