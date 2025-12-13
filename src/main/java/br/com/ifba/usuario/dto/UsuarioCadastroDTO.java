package br.com.ifba.usuario.dto;

import br.com.ifba.usuario.entity.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Set;

// DTO para a criação de um novo usuário
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroDTO {
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "O CPF informado é inválido.")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @NotNull(message = "O perfil é obrigatório")
    private Set<PerfilUsuario> perfis;
}
