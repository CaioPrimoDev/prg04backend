package br.com.ifba.infrastructure.security;

import br.com.ifba.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal
            (
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
            )
            throws ServletException, IOException {

        String url = request.getRequestURI();

        // Debug para vermos EXATAMENTE o que está chegando
        System.out.println("--- FILTER CHECK ---");
        System.out.println("URL: " + url);

        // 🚨 1. A REGRA DE OURO TEM QUE SER A PRIMEIRA COISA
        // Verificamos se a URL contém "webhook" (independente de maiúscula/minúscula ou prefixos)
        if (url.toLowerCase().contains("webhook")) {
            System.out.println("⏩ WEBHOOK DETECTADO! Pulando verificação de token.");

            // Passa a bola para frente e...
            filterChain.doFilter(request, response);

            // 🚨 2. O RETURN É OBRIGATÓRIO!
            // Isso impede que o Java continue descendo para a linha "recoverToken"
            return;
        }

        // --- A partir daqui, só executa se NÃO for webhook ---

        try {
            // Se o código chegou aqui, não é webhook. Então EXIGIMOS token.
            var token = this.recoverToken(request);

            if (token != null) {
                var login = tokenService.validateToken(token);
                if (login != null) {
                    var userOptional = usuarioRepository.findByPessoa_Email(login);
                    if (userOptional.isPresent()) {
                        var user = userOptional.get();
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } else {
                // Só imprime isso se NÃO for webhook
                System.out.println("⚠️ AVISO: Acesso sem token na rota: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        // O header vem como "Bearer eyJhbGciOiJIUzI1..." -> removemos o "Bearer "
        return authHeader.replace("Bearer ", "");
    }
}
