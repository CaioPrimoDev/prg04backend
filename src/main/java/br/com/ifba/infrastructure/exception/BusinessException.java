package br.com.ifba.infrastructure.exception;

public class BusinessException extends RuntimeException {

    // 1. Construtor básico: Aceita apenas a mensagem de erro.
    public BusinessException(final String message) {
        super(message);
    }

    // 2. Construtor para encadeamento: Aceita a mensagem e a causa (outra exceção).
    // Este é útil quando você captura uma exceção de nível inferior (ex: do banco de dados)
    // e a relança como uma exceção de negócio.
    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    // 3. Construtor que aceita apenas a causa.
    public BusinessException(final Throwable cause) {
        super(cause);
    }

    // 4. (Mais avançado) Permite controlar o comportamento da stack trace,
    // embora seja menos usado em APIs simples.
    public BusinessException(final String message, final Throwable cause,
                             final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
