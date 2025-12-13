package br.com.ifba.usuario.service;

import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioIService {
    Usuario save(UsuarioCadastroDTO dto);
    Usuario findByEmail(String email);
    Usuario findByCpf(String cpf);
    void deleteById(Long id);
    Page<Usuario> findAll(Pageable pageable);
}
