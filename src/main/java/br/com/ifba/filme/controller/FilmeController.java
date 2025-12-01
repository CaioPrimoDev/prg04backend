package br.com.ifba.filme.controller;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.dto.ImagemUrlRequest;
import br.com.ifba.filme.service.FilmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService service;

    @PostMapping(path = "/save")
    public ResponseEntity<Filme> salvar(@RequestBody Filme filme) {
        Filme saved = service.save(filme);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    @GetMapping(path = "/ativos")
    public ResponseEntity<List<Filme>> listarAtivos() {
        return ResponseEntity.ok(service.findByAtivoTrue());
    }

    @GetMapping(path = "/desativados")
    public ResponseEntity<List<Filme>> listarDesativados() {
        return ResponseEntity.ok(service.findByAtivoFalse());
    }

    @GetMapping(path = "")
    public ResponseEntity<List<Filme>> listarTodos() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Filme> buscarPorId(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/imagem")
    public ResponseEntity<Filme> atualizarImagem(
            @PathVariable Long id,
            @RequestBody ImagemUrlRequest request) {
        Filme atualizado = service.atualizarImagem(id, request.getImagemUrl());
        return ResponseEntity.ok(atualizado);
    }
}
