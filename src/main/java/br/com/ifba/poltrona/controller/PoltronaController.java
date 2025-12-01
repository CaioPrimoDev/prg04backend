package br.com.ifba.poltrona.controller;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.service.PoltronaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/poltronas")
@RequiredArgsConstructor
public class PoltronaController {

    private final PoltronaService service;

    @GetMapping(path = "/sala/{salaId}")
    public java.util.List<Poltrona> listarPorSessao(@PathVariable Long sessaoId) {
        return service.findBySessaoId(sessaoId);
    }

    @PostMapping(path = "/save")
    public Poltrona salvar(@RequestBody Poltrona poltrona) {
        return service.save(poltrona);
    }
}

