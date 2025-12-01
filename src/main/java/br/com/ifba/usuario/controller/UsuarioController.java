package br.com.ifba.usuario.controller;

import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioIService usuarioService;

    @PostMapping(path = "/salvar")
    public Usuario salvar(@RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @GetMapping("/email/{email}")
    public Usuario buscarPorEmail(@PathVariable String email) {
        return usuarioService.findByEmail(email);
    }

    @GetMapping("/cpf/{cpf}")
    public Usuario buscarPorCpf(@PathVariable String cpf) {
        return usuarioService.findByCpf(cpf);
    }

    @GetMapping(path = "/findall")
    public List<Usuario> listarTodos() {
        return usuarioService.findAll();
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}



