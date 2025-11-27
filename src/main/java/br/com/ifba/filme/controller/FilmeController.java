package br.com.ifba.filme.controller;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.dto.ImagemUrlRequest;
import br.com.ifba.filme.service.FilmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService service;

    @PostMapping
    public ResponseEntity<Filme> criar(@RequestBody Filme filme) {
        Filme saved = service.salvar(filme);
        return ResponseEntity.created(URI.create("/api/filmes/" + saved.getId())).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Filme>> listarAtivos() {
        return ResponseEntity.ok(service.listarAtivos());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Filme>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filme> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // delete lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // hard delete
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/imagem")
    public ResponseEntity<Filme> atualizarImagem(@PathVariable Long id, @RequestBody ImagemUrlRequest body) {
        return service.buscarPorId(id)
                .map(f -> {
                    f.setImagemUrl(body.getImagemUrl());
                    return ResponseEntity.ok(service.salvar(f));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
