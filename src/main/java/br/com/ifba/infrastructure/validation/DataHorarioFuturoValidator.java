package br.com.ifba.infrastructure.validation;

import br.com.ifba.sessao.dto.SessaoCadastroDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;


// Esta classe precisa saber qual é o DTO que ela está validando
public class DataHorarioFuturoValidator implements ConstraintValidator<DataHorarioFuturo, SessaoCadastroDTO> {

    @Override
    public boolean isValid(SessaoCadastroDTO dto, ConstraintValidatorContext context) {

        // 1. Se estiver nulo, deixa o @NotNull tratar
        if (dto.getData() == null || dto.getHorario() == null) {
            return true;
        }

        LocalDateTime dataHoraAgendamento = LocalDateTime.of(dto.getData(), dto.getHorario());
        LocalDateTime agora = LocalDateTime.now();

        // Se for futuro, está válido.
        if (dataHoraAgendamento.isAfter(agora)) {
            return true;
        }

        // Desabilita o erro genérico da classe
        // Se não usar isso, o front não capta o erro
        context.disableDefaultConstraintViolation();

        boolean isHoje = dto.getData().isEqual(agora.toLocalDate());

        if (isHoje) {
            // CENÁRIO: É hoje, então o problema é especificamente o HORÁRIO (ex: agora 15:51, tentou 14:00)
            context.buildConstraintViolationWithTemplate("O horário selecionado já passou.")
                    .addPropertyNode("horario") // <--- Aponta para o campo 'horario'
                    .addConstraintViolation();
        } else {
            // CENÁRIO: A data é anterior a hoje (ex: ontem)
            context.buildConstraintViolationWithTemplate("A data deve ser no futuro.")
                    .addPropertyNode("data") // <--- Aponta para o campo 'data'
                    .addConstraintViolation();
        }

        return false;
    }
}
