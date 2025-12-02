package br.com.ifba.sessao.dto;

import br.com.ifba.filme.dto.FilmeResumoDTO;
import br.com.ifba.sala.dto.SalaResumoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// DTO para detalhes de uma sessão (GET /sessoes/{id})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoResponseDTO {
    // Campos básicos da Sessão
    private Long id;
    private LocalDate data;
    private LocalTime horario;
    private Boolean ativo;
    private LocalDateTime dataCadastro;

    // DTOs Aninhados para os relacionamentos
    private FilmeResumoDTO filme;
    private SalaResumoDTO sala;
}
