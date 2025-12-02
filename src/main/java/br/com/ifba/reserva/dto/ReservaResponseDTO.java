package br.com.ifba.reserva.dto;

import br.com.ifba.sessao.dto.SessaoResumoDTO;
import br.com.ifba.usuario.dto.UsuarioResumoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    // Campos gerenciados pelo sistema (ID, Status, Token, Total)
    private Long id;
    private String status;
    private String token;
    private LocalDateTime expiracao;
    private BigDecimal total;

    // DTOs Aninhados (Relacionamentos)
    private UsuarioResumoDTO usuario;
    private SessaoResumoDTO sessao;
}