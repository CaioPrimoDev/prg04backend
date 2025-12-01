package br.com.ifba.usuario.service;

import br.com.ifba.usuario.entity.Usuario;

import java.util.List;

public interface UsuarioIService {
    Usuario save(Usuario usuario);
    Usuario findByEmail(String email);
    Usuario findByCpf(String cpf);
    void deleteById(Long id);
    List<Usuario> findAll();
}
