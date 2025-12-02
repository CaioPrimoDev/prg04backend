package br.com.ifba.filme.controller;

import br.com.ifba.filme.dto.FilmeCadastroDTO;
import br.com.ifba.filme.dto.FilmeResponseDTO;
import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.image_dto.ImagemUrlRequest;
import br.com.ifba.filme.service.FilmeService;
import br.com.ifba.infrastructure.mapper.DTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService service;
    private final DTOMapper mapper; // Assumindo injeção

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilmeResponseDTO> salvar(@RequestBody FilmeCadastroDTO dto) {

        Filme saved = service.save(dto);

        FilmeResponseDTO responseDto = mapper.map(saved, FilmeResponseDTO.class);

        // Constrói a URI para o status 201 Created
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri() // Uso fromCurrentRequestUri para evitar duplicidade de /save
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDto);
    }

    @GetMapping(path = "/ativos")
    public ResponseEntity<List<FilmeResponseDTO>> listarAtivos() {
        return mapListToResponse(service.findByAtivoTrue());
    }

    @GetMapping(path = "/desativados")
    public ResponseEntity<List<FilmeResponseDTO>> listarDesativados() {
        return mapListToResponse(service.findByAtivoFalse());
    }

    @GetMapping(path = "/findall")
    public ResponseEntity<List<FilmeResponseDTO>> listarTodos() {
        return mapListToResponse(service.findAll());
    }

    private ResponseEntity<List<FilmeResponseDTO>> mapListToResponse(List<Filme> filmes) {
        List<FilmeResponseDTO> dtos = filmes.stream()
                .map(filme -> mapper.map(filme, FilmeResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable("id") Long id) {
        return service.findById(id)
                .map(filme -> mapper.map(filme, FilmeResponseDTO.class)) // Mapeia para DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> desativar(@PathVariable("id") Long id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable("id") Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{id}/imagem")
    public ResponseEntity<FilmeResponseDTO> atualizarImagem(
            @PathVariable("id") Long id,
            @RequestBody ImagemUrlRequest request) { // Mantém a classe ImagemUrlRequest

        Filme atualizado = service.atualizarImagem(id, request.getImagemUrl());

        FilmeResponseDTO responseDto = mapper.map(atualizado, FilmeResponseDTO.class);

        return ResponseEntity.ok(responseDto);
    }
}
