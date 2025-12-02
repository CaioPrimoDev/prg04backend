package br.com.ifba.sala.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// SalaResumoDTO: DTO para uso como campo aninhado dentro de SessaoResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaResumoDTO {
    private Long id;
    private String nome; // O nome descritivo (ex: "SALA 1")
}
