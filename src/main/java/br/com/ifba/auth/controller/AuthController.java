package br.com.ifba.auth.controller;

import br.com.ifba.auth.dto.LoginDTO;
import br.com.ifba.auth.dto.LoginResponseDTO;
import br.com.ifba.auth.service.AuthService;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UsuarioCadastroDTO dto) {
        authService.registrar(dto);
        // Retorna 201 Created sem corpo
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
