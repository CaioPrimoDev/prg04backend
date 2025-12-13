package br.com.ifba.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt é o algoritmo padrão e mais recomendado
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (necessário para POSTs funcionarem sem token)
                .csrf(AbstractHttpConfigurer::disable)

                // MODO DESENVOLVIMENTO: Libera TUDO
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /*
      @Bean
     * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     *     http
     *         // 1. Desabilita CSRF (Comum em APIs REST stateless)
     *         .csrf(csrf -> csrf.disable())
     *
     *         // 2. Define as regras de autorização
     *         .authorizeHttpRequests(auth -> auth
     *             // Permite APENAS o método POST para o endpoint de cadastro
     *             .requestMatchers(POST, "/usuarios/save").permitAll()
     *             // Adicione aqui também seu endpoint de login, quando criá-lo:
     *             // .requestMatchers(POST, "/auth/login").permitAll()
     *
     *             // Exige autenticação para todos os outros endpoints
     *             .anyRequest().authenticated()
     *         )
     *
     *         // 3. Adicione a configuração de sessão Stateless (necessária para JWT)
     *         // Isso é fundamental para APIs REST que usam tokens
     *         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
     *
     *     return http.build();
     * }
     */
}
