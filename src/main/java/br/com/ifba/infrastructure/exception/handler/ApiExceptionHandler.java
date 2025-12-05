package br.com.ifba.infrastructure.exception.handler;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.exception.dto.ErrorResponse;
import br.com.ifba.infrastructure.exception.dto.ValidationExceptionDetails;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.exception.ExceptionUtils; // Requer dependency commons-lang3
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 1. Injeta a configuração para mostrar ou não o stack trace
    @Value("${server.error.include-exception:false}")
    private boolean printStackTrace;

    // --- Tratamento para Exceções de Regra de Negócio (BusinessException) ---
    // Ideal para erros 400 que não são de validação de DTO, como "ID não encontrado"
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(
            final BusinessException ex)
    {
        return buildErrorMessage(
                ex,
                ex.getMessage()
        );
    }

    // --- Tratamento para Exceções de Validação de Input (MethodArgumentNotValidException) ---
    // Essencial para retornar detalhes dos campos com erro (ex: @NotNull, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDetails> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex)
    {
        List<String> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.toList());

        List<String> fieldsMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ValidationExceptionDetails details = ValidationExceptionDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Bad Request Exception, Campos Inválidos")
                .details("Um ou mais campos estão inválidos. Verifique a lista 'fields' e 'fieldsMessage'.")
                .developerMessage(ex.getClass().getName())
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .build();

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity<Object> buildErrorMessage(
            final Exception exception,
            final String mensagem)
    {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), mensagem);

        if (this.printStackTrace) {
            errorResponse.setStacktrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}