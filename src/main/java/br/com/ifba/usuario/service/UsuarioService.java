package br.com.ifba.usuario.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.pessoa.entity.Pessoa;
import br.com.ifba.pessoa.repository.PessoaRepository;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioIService {

    private final UsuarioRepository usuarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario save(UsuarioCadastroDTO dto) {
        if (usuarioRepository.findByPessoa_Cpf(dto.getCpf()).isPresent()) {
            throw new BusinessException("CPF já cadastrado.");
        }
        if (usuarioRepository.findByPessoa_Email(dto.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado.");
        }

        Pessoa pessoa = Pessoa.builder()
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .build();
        pessoa = pessoaRepository.save(pessoa);

        Usuario usuario = Usuario.builder()
                .pessoa(pessoa)
                // Criptografa a senha em um Hash seguro
                // Sim, fui eu MESMO que escrevi esse comentário :P
                .senha(passwordEncoder.encode(dto.getSenha()))
                .perfis(dto.getPerfis())
                .status(true)
                .build();
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByPessoa_Email(email)
                .orElseThrow(() -> new BusinessException(
                        "Usuário com o email " + email + " não encontrado."
                ));
    }

    @Override
    public Usuario findByCpf(String cpf) {
        return usuarioRepository.findByPessoa_Cpf(cpf)
                .orElseThrow(() -> new BusinessException(
                        "Usuário com o CPF " + cpf + " não encontrado."
                ));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Valida se o usuário existe antes de tentar deletar
        if (!usuarioRepository.existsById(id)) {
            throw new BusinessException(
                    "Não é possível deletar, usuário com o ID " + id + " não encontrado."
            );
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }
}

