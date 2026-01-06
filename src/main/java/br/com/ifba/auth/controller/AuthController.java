package br.com.ifba.auth.controller;

import br.com.ifba.auth.dto.LoginDTO;
import br.com.ifba.auth.dto.LoginResponseDTO;
import br.com.ifba.auth.service.AuthService;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.entity.PerfilUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    // ROTA PÚBLICA (permitAll): Cadastro de clientes comuns
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UsuarioCadastroDTO dto) {
        // SEGURANÇA: Mesmo que o front envie outro perfil,
        // forçamos CLIENTE nesta rota pública. Já que alguém poderia forçar um ["ADMIN"] pelo JSON
        dto.setPerfis(Set.of(PerfilUsuario.ADMIN));
        authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ROTA PROTEGIDA (.hasRole("ADMIN")): Criação de Gestores/Admins
    @PostMapping("/register-gestor")
    public ResponseEntity<Void> registerGestor(@RequestBody @Valid UsuarioCadastroDTO dto) {
        // Nesta rota, confiamos nos perfis enviados pelo DTO
        // porque apenas um ADMIN autenticado pode acessá-la.
        dto.setPerfis(Set.of(PerfilUsuario.GESTOR));
        authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
