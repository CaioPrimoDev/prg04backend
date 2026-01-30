package br.com.ifba.ingresso.controller;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.ingresso.dto.IngressoRequestDTO;
import br.com.ifba.ingresso.dto.IngressoResponseDTO;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.service.IngressoIService;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.service.SessaoIService;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/ingressos")
@RequiredArgsConstructor
public class IngressoController {

    private final IngressoIService service;
    private final SessaoIService sessaoService;
    private final UsuarioIService usuarioService;
    private final ObjectMapperUtill mapper; // Supondo que seja ModelMapper ou similar

    /**
     * RESERVAR POLTRONA (Cria com status PENDENTE)
     * O Front envia { sessaoId: 1, codigoPoltrona: "A10" }
     */
    @PostMapping
            (
                path = "/reservar",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE
            )
    public ResponseEntity<IngressoResponseDTO> toReserve(
            @RequestBody @Valid IngressoRequestDTO dto,
            Principal principal)  { // Pega usuário do token

        Usuario usuario;
        try {

            usuario = usuarioService.findByEmail(principal.getName());

        } catch (EntityNotFoundException e) {

            throw new BusinessException("Usuário não encontrado", e);

        }

        Sessao sessao = sessaoService.findById(dto.getSessaoId())
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

        // Monta a Entidade (Sem preço ainda)
        Ingresso ingresso = Ingresso.builder()
                .usuario(usuario)
                .sessao(sessao)
                .codigoPoltrona(dto.getCodigoPoltrona())
                .tipo(dto.getTipo()) // Passamos o tipo escolhido
                .build();

        Ingresso ingressoSalvo = service.toReserve(ingresso);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.map(ingressoSalvo, IngressoResponseDTO.class));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            Principal principal) {

        // Passamos o email do usuário para garantir que ele só apague o PRÓPRIO ingresso
        service.cancelReservation(id, principal.getName());

        return ResponseEntity.noContent().build(); // Retorna 204 (Sucesso sem corpo)
    }

    /**
     * CONFIRMAR PAGAMENTO
     * Transforma o status de PENDENTE para CONFIRMADO
     */
    @PatchMapping(path = "/{id}/confirmar",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngressoResponseDTO> confirmPayment(@PathVariable("id") Long id) {

        Ingresso ingressoConfirmado = service.confirmPayment(id);

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
    public ResponseEntity<List<String>> findPoltronasOcupadas(@PathVariable("sessaoId") Long sessaoId) {

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
    public ResponseEntity<List<IngressoResponseDTO>> findByUsuarioId(@PathVariable("usuarioId") Long usuarioId) {

        List<Ingresso> ingressos = service.findByUsuarioId(usuarioId);

        return ResponseEntity.ok(ingressos.stream()
                .map(ingresso -> mapper.map(ingresso, IngressoResponseDTO.class))
                .collect(Collectors.toList()));
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngressoResponseDTO> findById(@PathVariable("id") Long id) {

        return service.findById(id)
                .map(ingresso -> mapper.map(ingresso, IngressoResponseDTO.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
