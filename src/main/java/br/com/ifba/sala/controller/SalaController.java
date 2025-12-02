package br.com.ifba.sala.controller;

import br.com.ifba.infrastructure.mapper.DTOMapper;
import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.dto.SalaResponseDTO;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.service.SalaIService;
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
    private final DTOMapper mapper;

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SalaResponseDTO> salvar(@RequestBody SalaCadastroDTO dto) { // ✅ Recebe DTO

        // 1. O Service deve ser alterado para: Sala save(SalaCadastroDTO dto)
        Sala criado = service.save(dto);

        // 2. Mapeia a Entidade criada para o DTO de Saída
        SalaResponseDTO responseDto = mapper.map(criado, SalaResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping(path = "/findall",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaResponseDTO>> listar() { // ✅ Retorna Lista de DTOs

        List<Sala> salas = service.findAll();

        // Transforma a lista de Entidades em lista de DTOs usando Streams
        List<SalaResponseDTO> dtos = salas.stream()
                .map(sala -> mapper.map(sala, SalaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

