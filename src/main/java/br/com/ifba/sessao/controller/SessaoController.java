package br.com.ifba.sessao.controller;

import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.service.SessaoIService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoIService service;

    @PostMapping(path = "/save")
    public ResponseEntity<Sessao> criar(@RequestBody Sessao sessao) {
        Sessao saved = service.save(sessao);
        return ResponseEntity.ok(saved);
    }

    @GetMapping(path = "/data/{data}")
    public ResponseEntity<List<Sessao>> listarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(service.findByData(data));
    }

    @GetMapping(path = "/filme/{filmeId}/data/{data}")
    public ResponseEntity<List<Sessao>> listarPorFilmeEData(
            @PathVariable Long filmeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(service.findByFilmeIdAndData(filmeId, data));
    }

    @GetMapping(path = "/filme/{filmeId}")
    public ResponseEntity<List<Sessao>> listarPorFilme(@PathVariable Long filmeId) {
        return ResponseEntity.ok(service.findByFilmeId(filmeId));
    }

    @GetMapping(path = "/sala/{salaId}")
    public ResponseEntity<List<Sessao>> listarPorSala(@PathVariable Long salaId) {
        return ResponseEntity.ok(service.findBySalaId(salaId));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Sessao> buscarPorId(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/ativas")
    public ResponseEntity<List<Sessao>> listarAtivas() {
        return ResponseEntity.ok(service.findByAtivoTrue());
    }

    @GetMapping(path = "/desativadas")
    public ResponseEntity<List<Sessao>> listarDesativadas() {
        return ResponseEntity.ok(service.findByAtivoFalse());
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
