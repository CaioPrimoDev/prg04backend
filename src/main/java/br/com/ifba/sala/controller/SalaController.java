package br.com.ifba.sala.controller;

import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.service.SalaIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaIService service;

    @PostMapping(path = "/save")
    public ResponseEntity<Sala> salvar(@RequestBody Sala sala) {
        Sala criado = service.save(sala);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(criado);
    }

    @GetMapping(path = "/findall")
    public ResponseEntity<List<Sala>> listar() {
        List<Sala> salas = service.findAll();
        return ResponseEntity.ok(salas);
    }
}

