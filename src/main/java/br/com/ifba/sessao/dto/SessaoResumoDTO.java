package br.com.ifba.sessao.dto;

import br.com.ifba.filme.dto.FilmeResumoDTO;
import br.com.ifba.sala.dto.SalaResumoDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

// SessaoResumoDTO
@Data
public class SessaoResumoDTO {
    private Long id;
    private LocalDate data;
    private LocalTime horario;

    // O resumo da Sessão precisa do resumo do Filme e da Sala.
    private FilmeResumoDTO filme;
    private SalaResumoDTO sala;
}