package br.com.ifba.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// UsuarioResumoDTO: DTO para uso aninhado dentro da Reserva
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResumoDTO {
    private Long id;
    private String email;
}
