package br.com.ifba.reservapoltrona.controller;

import br.com.ifba.infrastructure.mapper.DTOMapper;
import br.com.ifba.reservapoltrona.dto.ReservaPoltronaResponseDTO;
import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.service.ReservaPoltronaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservapoltrona")
@RequiredArgsConstructor
public class ReservaPoltronaController {

    private final ReservaPoltronaService service;
    private final DTOMapper mapper; // Assumindo injeção

    // Listar por reserva (útil para coletar todas as poltronas daquela reserva)
    @GetMapping(path = "/reserva/{reservaId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservaPoltronaResponseDTO>> listarPorReserva(@PathVariable("reservaId") Long reservaId) {

        List<ReservaPoltrona> reservasPoltronas = service.findByReservaId(reservaId);

        // Mapeia Entidades para DTOs
        List<ReservaPoltronaResponseDTO> dtos = reservasPoltronas.stream()
                .map(rp -> mapper.map(rp, ReservaPoltronaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Deletar (cancelar) reservaPoltrona - Não precisa de DTO
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
