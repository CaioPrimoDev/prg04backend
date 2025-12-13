package br.com.ifba.usuario.dto;

import br.com.ifba.usuario.entity.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    // Todos os dados não-sensíveis para exibição completa
    private Long id;
    private String cpf;
    private String email;
    private Set<PerfilUsuario> perfis;
}
