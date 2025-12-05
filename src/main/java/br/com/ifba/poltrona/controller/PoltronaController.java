package br.com.ifba.poltrona.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.poltrona.dto.PoltronaCadastroDTO;
import br.com.ifba.poltrona.dto.PoltronaResponseDTO;
import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.service.PoltronaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/poltronas")
@RequiredArgsConstructor
public class PoltronaController {

    private final PoltronaService service;
    private final ObjectMapperUtill mapper;

    @GetMapping(path = "/find-by-sessao-id/{sessaoId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PoltronaResponseDTO>> findBySessaoId(@PathVariable("sessaoId") Long sessaoId) {

        List<Poltrona> poltronas = service.findBySessaoId(sessaoId);

        // Aplica o padrão Streams para mapear a lista
        List<PoltronaResponseDTO> dtos = poltronas.stream()
                .map(poltrona -> mapper.map(poltrona, PoltronaResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PoltronaResponseDTO> save(@RequestBody @Valid PoltronaCadastroDTO dto) { // ✅ Recebe DTO

        // O service deve ser alterado para: Poltrona save(PoltronaCadastroDTO dto)
        Poltrona novaPoltrona = service.save(dto);

        // Mapeia a Entidade criada para o DTO de Saída
        PoltronaResponseDTO responseDto = mapper.map(novaPoltrona, PoltronaResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED) // Status 201
                .body(responseDto);
    }
}

