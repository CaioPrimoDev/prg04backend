package br.com.ifba.sala.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.dto.SalaResponseDTO;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.service.SalaIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<SalaResponseDTO>> listar(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        Page<Sala> salas = service.findAll(pageable);

        if(salas.isEmpty()) {
            // Retorna 200 OK com uma lista vazia, ou 204 No Content, que é melhor que 404.
            return ResponseEntity.ok(Page.empty());
        }

        Page<SalaResponseDTO> PageDtos = salas.map(sala -> mapper.map(sala, SalaResponseDTO.class));

        return ResponseEntity.ok(PageDtos);
    }

    @GetMapping(path = "/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SalaResponseDTO> findById(@PathVariable("id") Long id) {

        Sala sala = service.findById(id);

        SalaResponseDTO dto = mapper.map(sala, SalaResponseDTO.class);

        return ResponseEntity.ok(dto);
    }

}

