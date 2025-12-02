package br.com.ifba.poltrona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoltronaResumoDTO {
    private Long id;
    private String codigo; // Ex: A10
    private String letra;
    private Integer numero;
}
