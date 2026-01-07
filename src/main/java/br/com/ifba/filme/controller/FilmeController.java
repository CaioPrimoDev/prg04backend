package br.com.ifba.filme.controller;

import br.com.ifba.filme.dto.FilmeCadastroDTO;
import br.com.ifba.filme.dto.FilmeResponseDTO;
import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.service.FilmeService;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService service;
    private final ObjectMapperUtill mapper;

    // Precisamos do ObjectMapper 'puro' do Jackson para ler o JSON que vem como String
    // Necessário por causa do MULTIPART_FORM_DATA
    private final ObjectMapper jsonMapper;

    @PostMapping(path = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FilmeResponseDTO> salvar(
            @RequestPart("filme") @Valid String filmeJson,  // O JSON vem como texto
            @RequestPart("imagem") MultipartFile imagem     // O Arquivo vem como binário
    ) throws JsonProcessingException {

        // DEBUG
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Usuário logado: " + auth.getName());
        System.out.println("Permissões carregadas: " + auth.getAuthorities());

        // Converte a String "dados" para o Objeto FilmeCadastroDTO
        FilmeCadastroDTO dto = jsonMapper.readValue(filmeJson, FilmeCadastroDTO.class);

        Filme saved = service.save(dto, imagem);

        FilmeResponseDTO responseDto = mapper.map(saved, FilmeResponseDTO.class);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
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
    public ResponseEntity<Page<FilmeResponseDTO>> listarTodos(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        Page<Filme> PageFilmes = service.findAll(pageable);

        if(PageFilmes.isEmpty()) {
            // Retorna 200 OK com uma lista vazia, ou 204 No Content, que é melhor que 404.
            return ResponseEntity.ok(Page.empty(pageable));
        }

        Page<FilmeResponseDTO> PageDtos = PageFilmes.map(filme ->
                mapper.map(filme, FilmeResponseDTO.class)
        );

        return ResponseEntity.ok(PageDtos);
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

    @PutMapping(path = "/disable/{id}")
    public ResponseEntity<Void> desativar(@PathVariable("id") Long id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/activate/{id}")
    public ResponseEntity<Void> ativar(@PathVariable("id") Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/delete/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable("id") Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FilmeResponseDTO> atualizar(
            @PathVariable("id") Long id,
            @RequestPart("filme") @Valid String filmeJson,            // JSON com título, preço, etc.
            @RequestPart(value = "imagem", required = false) MultipartFile imagem // Arquivo opcional
    ) throws JsonProcessingException {

        // 1. Converte o JSON String para DTO
        FilmeCadastroDTO dto = jsonMapper.readValue(filmeJson, FilmeCadastroDTO.class);

        // 2. Chama o serviço de atualização
        Filme filmeAtualizado = service.atualizar(id, dto, imagem);

        // 3. Retorna o DTO atualizado
        return ResponseEntity.ok(mapper.map(filmeAtualizado, FilmeResponseDTO.class));
    }
}
