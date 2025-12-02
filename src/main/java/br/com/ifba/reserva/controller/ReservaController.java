package br.com.ifba.reserva.controller;

import br.com.ifba.infrastructure.mapper.DTOMapper;
import br.com.ifba.reserva.dto.ReservaCadastroDTO;
import br.com.ifba.reserva.dto.ReservaResponseDTO;
import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService service;
    private final DTOMapper mapper;

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservaResponseDTO> criarReserva(@RequestBody ReservaCadastroDTO dto) {

        // O Service agora recebe apenas o DTO, não entidades separadas
        Reserva novaReserva = service.criarReserva(dto);

        // Mapeia a Entidade para o DTO de Saída (ReservaResponseDTO)
        ReservaResponseDTO responseDto = mapper.map(novaReserva, ReservaResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping(path = "/usuario/{usuarioId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReservaResponseDTO>> findByUsuarioId(@PathVariable("usuarioId") Long usuarioId) {

        // busca a lista de Entidades Reserva
        List<Reserva> reservas = service.findByUsuarioId(usuarioId); // Assumindo este método no Service

        // Transforma a lista de Entidades em lista de DTOs usando Streams
        List<ReservaResponseDTO> dtos = reservas.stream()
                .map(reserva -> mapper.map(reserva, ReservaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

