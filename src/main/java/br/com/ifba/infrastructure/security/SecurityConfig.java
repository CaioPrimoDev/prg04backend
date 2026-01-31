package br.com.ifba.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Libera configurações globais e Login/Registro
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("auth/register").permitAll()

                        // 2. TUDO que for GET (leitura) em filmes e sessões é público
                        .requestMatchers(HttpMethod.GET, "/filmes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/sessoes/**").permitAll()

                        // 3. Modificações (POST, PUT, DELETE) exigem ADMIN ou GESTOR
                        .requestMatchers(HttpMethod.POST, "/filmes/**", "/sessoes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_GESTOR")
                        .requestMatchers(HttpMethod.PUT, "/filmes/**", "/sessoes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_GESTOR")
                        .requestMatchers(HttpMethod.DELETE, "/filmes/**", "/sessoes/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_GESTOR")

                        // 4. Gestão de usuários: Só ADMIN
                        .requestMatchers("/usuarios/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/register-gestor").hasAuthority("ROLE_ADMIN")

                        // --- PONTO 4.1: LIBERAR O WEBHOOK DO MERCADO PAGO ---
                        .requestMatchers("/sucesso", "/pendente", "/falha").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pagamentos/webhook").permitAll()

                        // libera a rota de erro padrão
                        .requestMatchers("/error").permitAll()

                        // 5. O resto exige login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configuração para aceitar Vercel dinâmico e Localhost
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "https://prg04frontend-react.vercel.app",   // URL Principal (Produção)
                "https://prg04frontend-react-*.vercel.app"  // URL Dinâmica (Previews/Branches)
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Adicione "x-auth-token" caso utilize no futuro, mas os padrões estão ok
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

