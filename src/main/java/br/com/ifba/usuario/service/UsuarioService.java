package br.com.ifba.usuario.service;

import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario salvar(Usuario usuario) {
        return repository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public Usuario buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf).orElse(null);
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }

    public java.util.List<Usuario> listarTodos() {
        return repository.findAll();
    }
}

