package br.com.ifba.auth.service;

import br.com.ifba.auth.dto.LoginDTO;
import br.com.ifba.auth.dto.LoginResponseDTO;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioIService usuarioService; // Usa o seu service existente
    private final PasswordEncoder passwordEncoder; // Para validar a senha no login

    /**
     * Lógica de Login Híbrido (CPF ou Email)
     */
    public LoginResponseDTO login(LoginDTO dto) {
        Usuario usuario;

        // 1. Descobre se é Email ou CPF
        if (dto.getLogin().contains("@")) {
            usuario = usuarioService.findByEmail(dto.getLogin());
        } else {
            // Garante que só tem números para buscar no banco
            String cpfLimpo = dto.getLogin().replaceAll("\\D", "");
            usuario = usuarioService.findByCpf(cpfLimpo);
        }

        // 2. Verifica a senha (Senha digitada vs Hash do banco)
        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas."); // Não diga se foi senha ou email para segurança
        }

        // 3. Gera o Token (Simulado com UUID por enquanto)
        String token = UUID.randomUUID().toString();

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