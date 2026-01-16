package br.com.ifba.usuario.repository;

import br.com.ifba.usuario.entity.PerfilUsuario;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Buscar pelo CPF da Pessoa associada ao Usuario
    // O Spring Data JPA entende: SELECT u FROM Usuario u WHERE u.pessoa.cpf = :cpf
    Optional<Usuario> findByPessoa_Cpf(String cpf);

    // 2. Buscar pelo Email da Pessoa associada ao Usuario
    // O Spring Data JPA entende: SELECT u FROM Usuario u WHERE u.pessoa.email = :email
    Optional<Usuario> findByPessoa_Email(String email);

    // 3. Buscar pelo CPF E Email da Pessoa (útil para validações mais estritas)
    Optional<Usuario> findByPessoa_CpfAndPessoa_Email(String cpf, String email);

    @Query("SELECT u FROM Usuario u JOIN u.perfis p WHERE p = :perfil")
    List<Usuario> findAllByPerfil(@Param("perfil") PerfilUsuario perfil);
}

/*

 */

