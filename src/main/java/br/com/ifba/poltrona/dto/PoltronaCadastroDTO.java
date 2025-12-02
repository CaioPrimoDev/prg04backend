package br.com.ifba.poltrona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// PoltronaCadastroDTO: DTO para criar uma nova poltrona para uma sessão
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoltronaCadastroDTO {
    // ⬅️ Relacionamento (Chave Estrangeira)
    private Long sessaoId;

    // ⬅️ Identificadores da Poltrona
    private String codigo;
    private String letra;
    private Integer numero;
}
