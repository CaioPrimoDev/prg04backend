package br.com.ifba.infrastructure.validation;

import br.com.ifba.sessao.dto.SessaoCadastroDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

// Esta classe precisa saber qual é o DTO que ela está validando
public class DataHorarioFuturoValidator implements ConstraintValidator<DataHorarioFuturo, SessaoCadastroDTO> {

    @Override
    public boolean isValid(SessaoCadastroDTO dto, ConstraintValidatorContext context) {

        // Se a data ou horário for nulo, a validação de @NotNull cuida disso.
        if (dto.getData() == null || dto.getHorario() == null) {
            return true;
        }

        // Combina os campos LocalDate e LocalTime em um único LocalDateTime
        LocalDateTime dataHoraAgendamento = LocalDateTime.of(dto.getData(), dto.getHorario());

        // Pega o momento atual
        LocalDateTime agora = LocalDateTime.now();

        // Verifica se a data e hora do agendamento é posterior (isAfter) ao momento atual
        return dataHoraAgendamento.isAfter(agora);
    }
}
