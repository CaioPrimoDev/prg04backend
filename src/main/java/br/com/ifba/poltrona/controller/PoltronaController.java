package br.com.ifba.poltrona.controller;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.service.PoltronaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poltronas")
@RequiredArgsConstructor
public class PoltronaController {

    private final PoltronaService service;

    @GetMapping("/sala/{salaId}")
    public java.util.List<Poltrona> listarPorSala(@PathVariable Long salaId) {
        return service.listarPorSala(salaId);
    }

    @PostMapping
    public Poltrona salvar(@RequestBody Poltrona poltrona) {
        return service.salvar(poltrona);
    }
}

