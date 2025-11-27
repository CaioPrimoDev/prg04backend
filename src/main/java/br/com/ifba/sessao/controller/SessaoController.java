package br.com.ifba.sessao.controller;

import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoService service;

    @PostMapping
    public ResponseEntity<Sessao> criar(@RequestBody Sessao sessao) {
        Sessao saved = service.salvar(sessao);
        return ResponseEntity.ok(saved);
    }

    // listar por data
    @GetMapping("/data/{data}")
    public ResponseEntity<List<Sessao>> listarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(service.listarPorData(data));
    }

    // listar por filme e data
    @GetMapping("/filme/{filmeId}/data/{data}")
    public ResponseEntity<List<Sessao>> listarPorFilmeEData(
            @PathVariable Long filmeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(service.listarPorFilmeEData(filmeId, data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sessao> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Sessao>> listarAtivas() {
        return ResponseEntity.ok(service.listarAtivas());
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
}

