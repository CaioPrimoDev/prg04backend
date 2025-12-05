package br.com.ifba.infrastructure.validation.dto;

import br.com.ifba.infrastructure.validation.DataHorarioFuturo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@DataHorarioFuturo
public class AgendamentoDTO {

    @NotNull(message = "A data é obrigatória.")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;

    @NotNull(message = "O horário é obrigatório.")
    @JsonFormat(pattern = "HH:mm") // Ex: 14:30
    private LocalTime horario;
}
