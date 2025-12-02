package br.com.ifba.sala.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// SalaResponseDTO: Para listar ou retornar o detalhe da sala
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaResponseDTO {
    private Long id;
    private String nome;
}
