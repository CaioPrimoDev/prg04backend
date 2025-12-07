package br.com.ifba.ingresso.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.ingresso.dto.IngressoRequestDTO;
import br.com.ifba.ingresso.dto.IngressoResponseDTO;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.service.IngressoIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoIService service;
    private final ObjectMapperUtill mapper; // Supondo que seja ModelMapper ou similar

    /**
     * RESERVAR POLTRONA (Cria com status PENDENTE)
     * O Front envia { sessaoId: 1, codigoPoltrona: "A10" }
     */
    @PostMapping(path = "/reservar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<IngressoResponseDTO>> reservar(@RequestBody @Valid IngressoRequestDTO dto) {

        // O Service processa a lista e retorna os ingressos criados
        List<Ingresso> ingressosSalvos = service.reservar(dto);

        // Convertemos a lista de Entidades para lista de DTOs
        List<IngressoResponseDTO> responseList = ingressosSalvos.stream()
                .map(ing -> mapper.map(ing, IngressoResponseDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseList);
    }

    /**
     * CONFIRMAR PAGAMENTO
     * Transforma o status de PENDENTE para CONFIRMADO
     */
    @PatchMapping(path = "/{id}/confirmar",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngressoResponseDTO> confirmarPagamento(@PathVariable("id") Long id) {

        Ingresso ingressoConfirmado = service.confirmarPagamento(id);

        IngressoResponseDTO responseDto = mapper.map(ingressoConfirmado, IngressoResponseDTO.class);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * MAPA DE ASSENTOS
     * Retorna a lista de Strings ["A1", "A2"] das poltronas ocupadas/reservadas.
     * O Front usa isso para pintar as cadeiras de vermelho.
     */
    @GetMapping(path = "/sessao/{sessaoId}/ocupadas",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> listarPoltronasOcupadas(@PathVariable("sessaoId") Long sessaoId) {

        // Retorna direto a lista de Strings do Repository, sem necessidade de DTO complexo
        List<String> poltronasOcupadas = service.findPoltronasOcupadas(sessaoId);

        return ResponseEntity.ok(poltronasOcupadas);
    }

    /**
     * HISTÓRICO DO USUÁRIO
     * Lista todos os ingressos de um cliente específico
     */
    @GetMapping(path = "/usuario/{usuarioId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<IngressoResponseDTO>> listarPorUsuario(@PathVariable("usuarioId") Long usuarioId) {

        List<Ingresso> ingressos = service.findByUsuarioId(usuarioId);

        return ResponseEntity.ok(ingressos.stream()
                .map(ingresso -> mapper.map(ingresso, IngressoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngressoResponseDTO> buscarPorId(@PathVariable("id") Long id) {

        return service.findById(id)
                .map(ingresso -> mapper.map(ingresso, IngressoResponseDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * CANCELAR/ESTORNAR
     * Remove o ingresso ou muda status para CANCELADO
     */
    @PatchMapping(path = "/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable("id") Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
