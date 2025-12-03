package br.com.ifba.infrastructure.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    // Status HTTP numérico (e.g., 400, 404, 500)
    private Integer status;

    private String error;

    private String stacktrace;

    public ErrorResponse(Integer status, String error) {
        this.status = status;
        this.error = error;
    }
}
