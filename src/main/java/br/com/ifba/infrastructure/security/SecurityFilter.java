package br.com.ifba.infrastructure.security;

import br.com.ifba.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
                            HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain filterChain
                                    ) throws ServletException, IOException {

        // LOG 1: Saber qual rota está sendo chamada
        System.out.println("--- INICIO FILTER ---");
        System.out.println("URL Chamada: " + request.getRequestURI());

        try {
            var token = this.recoverToken(request);

            if (token != null) {
                System.out.println("1. Token encontrado: " + token.substring(0, Math.min(token.length(), 10)) + "...");

                // Valida o token e extrai o login (email)
                var login = tokenService.validateToken(token);
                System.out.println("2. Login recuperado do Token: " + login);

                if (login != null && !login.isEmpty()) {
                    // Tenta achar o usuário pelo Email
                    var userOptional = usuarioRepository.findByPessoa_Email(login);

                    if (userOptional.isPresent()) {
                        UserDetails user = userOptional.get();
                        System.out.println("3. Usuário achado no DB: " + user.getUsername());

                        System.out.println("4. Authorities (Permissões): " + user.getAuthorities());

                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("5. Autenticação definida no Contexto com sucesso!");
                    } else {
                        System.out.println("ERRO: Usuário NÃO encontrado no banco para o email: " + login);
                    }
                }
            } else {
                System.out.println("AVISO: Requisição sem Token (normal para Login/Cadastro, erro para /save).");
            }
        } catch (Exception e) {
            // Log detalhado para entender por que falhou
            System.out.println("ERRO CRÍTICO no SecurityFilter: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- FIM FILTER (passando para o próximo passo) ---");
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        // O header vem como "Bearer eyJhbGciOiJIUzI1..." -> removemos o "Bearer "
        return authHeader.replace("Bearer ", "");
    }
}
