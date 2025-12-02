package br.com.ifba.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioListagemDTO {
    private Long id;
    private String cpf;
    private String email;
    private Boolean status;
    //private LocalDateTime dataCadastro;
}
