package br.com.ifba.sessao.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.sessao.dto.SessaoCadastroDTO;
import br.com.ifba.sessao.dto.SessaoResponseDTO;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.service.SessaoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoIService service;
    private final ObjectMapperUtill mapper;

    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessaoResponseDTO> criar(@RequestBody @Valid SessaoCadastroDTO dto) { // ✅ Recebe DTO

        // OBS: Assumimos que service.save() foi alterado para: Sessao save(SessaoCadastroDTO dto)
        Sessao savedEntity = service.save(dto); // ⬅️ O Service lida com a conversão DTO -> Entidade

        // Converte a Entidade salva para o DTO de Saída
        SessaoResponseDTO responseDto = mapper.map(savedEntity, SessaoResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED) // Status 201 CREATED é melhor para POST
                .body(responseDto);
    }

    @GetMapping(path = "/data/{data}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> listarPorData(
            @PathVariable("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<Sessao> sessoes = service.findByData(data);

        List<SessaoResponseDTO> dtos = sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping(path = "/filme/{filmeId}/data/{data}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> listarPorFilmeEData(
            @PathVariable("filmeId") Long filmeId,
            @PathVariable("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<Sessao> sessoes = service.findByFilmeIdAndData(filmeId, data);

        return ResponseEntity.ok(sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/filme/{filmeId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> listarPorFilme(@PathVariable("filmeId") Long filmeId) {

        List<Sessao> sessoes = service.findByFilmeId(filmeId);

        return ResponseEntity.ok(sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/sala/{salaId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> listarPorSala(@PathVariable("salaId") Long salaId) {

        List<Sessao> sessoes = service.findBySalaId(salaId);

        return ResponseEntity.ok(sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable("id") Long id) { // ✅ Retorna DTO

        return service.findById(id)
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // caso seja vazio
    }

    @GetMapping(path = "/ativas",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> findByAtivoTrue() {

        List<Sessao> sessoes = service.findByAtivoTrue();

        return ResponseEntity.ok(sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/desativadas",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SessaoResponseDTO>> findByAtivoFalse() {

        List<Sessao> sessoes = service.findByAtivoFalse();

        return ResponseEntity.ok(sessoes.stream()
                .map(sessao -> mapper.map(sessao, SessaoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @PatchMapping(path = "/{id}/desativar",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> desativar(@PathVariable("id") @Valid Long id) {
        service.disable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}/hard")
    public ResponseEntity<Void> apagar(@PathVariable("id") Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
