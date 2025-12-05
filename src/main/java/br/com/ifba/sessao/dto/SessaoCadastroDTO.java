package br.com.ifba.sessao.dto;

import br.com.ifba.infrastructure.validation.DataHorarioFuturo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DataHorarioFuturo // validação aplicado por classe para data e horario
public class SessaoCadastroDTO {
    // Relacionamentos representados por ID, eles usarão o metodo findById() no service

    @NotNull(message = "O Id do filme é obrigatório")
    private Long filmeId;

    @NotNull(message = "O Id do filme é obrigatório")
    private Long salaId;

    @NotNull(message = "A data é obrigatória.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    @NotNull(message = "O horário é obrigatório.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horario;
}
