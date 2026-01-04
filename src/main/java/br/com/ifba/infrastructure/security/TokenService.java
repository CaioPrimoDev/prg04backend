package br.com.ifba.infrastructure.security;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.usuario.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // O SpringBoot não reconhece bem o JWT, por ser fora do padrão, então tem que usar isso
    @Value("${api.security.token.secret}")
    private String secret;

    // GERA O TOKEN  toda vez que faz login
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api-cine") // Quem emitiu
                    .withSubject(usuario.getPessoa().getEmail()) // Quem é o dono (Email)
                    .withClaim("id", usuario.getId()) // Guardamos o ID pra facilitar
                    .withExpiresAt(genExpirationDate()) // Expira em 2 horas
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new BusinessException("Erro ao gerar token JWT", exception);
        }
    }

    // VALIDA O TOKEN em toda requisição
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api-cine")
                    .build()
                    .verify(token)
                    .getSubject(); // Retorna o Email que estava dentro do token
        } catch (JWTVerificationException exception) {
            return ""; // Token inválido ou expirado
        }
    }

    private Instant genExpirationDate() {
        // Token dura 2 horas  segundo o fuso horario de Brasilia(-03:00)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
