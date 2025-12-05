package br.com.ifba.sala.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaCadastroDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;
}
