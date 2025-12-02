package br.com.ifba.poltrona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// PoltronaResponseDTO: DTO para listar ou retornar o detalhe da poltrona
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoltronaResponseDTO {
    private Long id;
    private Long sessaoId; // ID da Sessão à qual pertence
    private String codigo;
    private String letra;
    private Integer numero;
    private Boolean bloqueada;
}
