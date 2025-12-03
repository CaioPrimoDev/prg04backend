package br.com.ifba.infrastructure.exception.handler;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.exception.dto.ErrorResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @Value(value = "${server.error.include-exception}")
    private boolean printStackTrace;

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBusinessException(
            final BusinessException ex,
            final WebRequest request)
    {
        // Chama o método auxiliar para construir a resposta padronizada
        return buildErrorMessage(
                ex,
                ex.getMessage(), // Mensagem da exceção
                HttpStatus.BAD_REQUEST, // Status HTTP desejado (400)
                request
        );
    }

    private ResponseEntity<Object> buildErrorMessage(
            final Exception exception,
            final String mensagem,
            final HttpStatus httpStatus,
            final WebRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), mensagem);
        if (this.printStackTrace) {
            errorResponse.setStacktrace(ExceptionUtils.getStackTrace(exception));
        }

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
