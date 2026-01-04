package br.com.ifba.auth.service;

import br.com.ifba.auth.dto.LoginDTO;
import br.com.ifba.auth.dto.LoginResponseDTO;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.security.TokenService;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioIService usuarioService; // Usa o seu service existente
    private final PasswordEncoder passwordEncoder; // Para validar a senha no login
    private final TokenService tokenService;

    /**
     * Lógica de Login Híbrido (CPF ou Email)
     */
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario;

        // Logica para descobrir se é Email ou CPF
        if (dto.getLogin().contains("@")) {
            usuario = usuarioService.findByEmail(dto.getLogin());
        } else {
            // Garante que só tem números para buscar no banco
            String cpfLimpo = dto.getLogin().replaceAll("\\D", "");
            usuario = usuarioService.findByCpf(cpfLimpo);
        }

        // Verifica a senha (Senha digitada vs Hash do banco, já que ela foi encripada antes de ser salva)
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Senha inválidas.");
        }

        // Gera o Token (Simulado com UUID por enquanto)
        String token = tokenService.generateToken(usuario);

        // Retorna o DTO com Token e talvez o Perfil principal
        return new LoginResponseDTO(token, usuario.getPessoa().getEmail());
    }

    /**
     * Registro público (Apenas delega para o UsuarioService)
     */
    @Transactional
    public void registrar(UsuarioCadastroDTO dto) {
        usuarioService.save(dto);
    }
}