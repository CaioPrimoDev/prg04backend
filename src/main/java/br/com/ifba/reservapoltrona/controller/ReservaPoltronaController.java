package br.com.ifba.reservapoltrona.controller;

import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.service.ReservaPoltronaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservapoltrona")
@RequiredArgsConstructor
public class ReservaPoltronaController {

    private final ReservaPoltronaService service;

    // "Criar" nova reservaPoltrona
    @PostMapping(path = "/save")
    public ResponseEntity<ReservaPoltrona> criar(@RequestBody ReservaPoltrona rp) {
        ReservaPoltrona saved = service.save(rp);
        return ResponseEntity.ok(saved);
    }

    // Listar por reserva (útil para coletar todas as poltronas daquela reserva)
    @GetMapping(path = "/reserva/{reservaId}")
    public ResponseEntity<List<ReservaPoltrona>> listarPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(service.findByReservaId(reservaId));
    }

    // Deletar (cancelar) reservaPoltrona
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
