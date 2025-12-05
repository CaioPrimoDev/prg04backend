package br.com.ifba.poltrona.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// PoltronaCadastroDTO: DTO para criar uma nova poltrona para uma sessão
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoltronaCadastroDTO {

    @NotNull(message = "O Id da sessão é obrigatório")
    private Long sessaoId;

    @NotBlank(message = "O código é obrigatório.")
    @Size(min = 2, max = 2, message = "O código deve ter exatamente 2 caracteres.")
    // Regex que exige [Letra][Dígito] (Ex: A5, b7)
    @Pattern(regexp = "^[a-zA-Z]\\d$",
            message = "O código deve ser exatamente UMA letra seguida por UM número.")
    private String codigo;

    @NotBlank(message = "A letra é obrigatória.")
    @Size(min = 1, max = 1, message = "O campo 'letra' deve ter exatamente 1 caractere.")
    @Pattern(regexp = "^[a-zA-Z]$", // Exige APENAS UMA letra
            message = "O campo 'letra' deve conter apenas uma letra.")
    private String letra;

    @NotNull(message = "O número é obrigatório.")
    @Min(value = 1, message = "O número mínimo é 1.")
    private Integer numero;
}
