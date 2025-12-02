package br.com.ifba.sessao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoCadastroDTO {
    // Relacionamentos representados por ID, eles usarão o metodo findById() no service
    private Long filmeId;
    private Long salaId;

    // Dados simples
    private LocalDate data; // Ou String, dependendo do validador
    private LocalTime horario; // Ou String
}
