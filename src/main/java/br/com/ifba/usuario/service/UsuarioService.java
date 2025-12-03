package br.com.ifba.usuario.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository repository;

    @Override
    public Usuario save(Usuario usuario) {
        // TODO: Adicionar validação de unicidade de CPF e Email antes de salvar.
        if (repository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado.");
        }
        return repository.save(usuario);
    }

    @Override
    public Usuario findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        "Usuário com o email " + email + " não encontrado."
                ));
    }

    @Override
    public Usuario findByCpf(String cpf) {
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new BusinessException(
                        "Usuário com o CPF " + cpf + " não encontrado."
                ));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Valida se o usuário existe antes de tentar deletar
        if (!repository.existsById(id)) {
            throw new BusinessException(
                    "Não é possível deletar, usuário com o ID " + id + " não encontrado."
            );
        }
        repository.deleteById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll();
    }
}

