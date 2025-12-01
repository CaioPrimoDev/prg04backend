package br.com.ifba.reserva.controller;

import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.dto.ReservaRequest;
import br.com.ifba.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService service;

    @PostMapping(path = "/save")
    public ResponseEntity<Reserva> criarReserva(@RequestBody ReservaRequest request) {
        Reserva novaReserva = service.criarReserva(
                request.getReserva(),
                request.getPoltronas()
        );
        return ResponseEntity.ok(novaReserva);
    }
}

