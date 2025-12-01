package br.com.ifba.sala.controller;

import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService service;

    @PostMapping(path = "/save")
    public Sala salvar(@RequestBody Sala sala) {
        return service.save(sala);
    }

    @GetMapping(path = "/findall")
    public java.util.List<Sala> listar() {
        return service.findAll();
    }
}

