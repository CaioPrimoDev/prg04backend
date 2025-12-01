package br.com.ifba.poltrona.controller;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.service.PoltronaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poltronas")
@RequiredArgsConstructor
public class PoltronaController {

    private final PoltronaService service;

    @GetMapping(path = "/find-by-sessao-id/{sessaoId}")
    public ResponseEntity<List<Poltrona>> findBySessaoId(@PathVariable Long sessaoId) {
        List<Poltrona> poltronas = service.findBySessaoId(sessaoId);
        return ResponseEntity.ok(poltronas);
    }

    @PostMapping(path = "/save")
    public ResponseEntity<Poltrona> save(@RequestBody Poltrona poltrona) {
        Poltrona novaPoltrona = service.save(poltrona);
        return ResponseEntity.ok(novaPoltrona);
    }
}

