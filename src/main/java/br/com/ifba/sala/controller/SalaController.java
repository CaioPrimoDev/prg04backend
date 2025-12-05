package br.com.ifba.sala.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.dto.SalaResponseDTO;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.service.SalaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SalaResponseDTO> salvar(@RequestBody @Valid SalaCadastroDTO dto) {

        Sala criado = service.save(dto);

        SalaResponseDTO responseDto = mapper.map(criado, SalaResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping(path = "/findall",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaResponseDTO>> listar() {

        List<Sala> salas = service.findAll();

        List<SalaResponseDTO> dtos = salas.stream()
                .map(sala -> mapper.map(sala, SalaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(path = "/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SalaResponseDTO> findById(@PathVariable("id") Long id) {

        Sala sala = service.findById(id);

        SalaResponseDTO dto = mapper.map(sala, SalaResponseDTO.class);

        return ResponseEntity.ok(dto);
    }

}

