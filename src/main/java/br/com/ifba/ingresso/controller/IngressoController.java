package br.com.ifba.ingresso.controller;

import br.com.ifba.api.service.EmailIService;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.ingresso.dto.IngressoRequestDTO;
import br.com.ifba.ingresso.dto.IngressoResponseDTO;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.repository.IngressoRepository;
import br.com.ifba.ingresso.service.IngressoIService;
import br.com.ifba.pagamento.entity.Pedido;
import br.com.ifba.pagamento.repository.PedidoRepository;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.service.SessaoIService;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import br.com.ifba.usuario.service.UsuarioIService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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
    private final IngressoRepository ingressoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailIService emailService;
    private final PedidoRepository pedidoRepository;

    /**
     * RESERVAR POLTRONA (Cria com status PENDENTE)
     * O Front envia { sessaoId: 1, codigoPoltrona: "A10" }
     */
    @PostMapping(
            path = "/reservar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> toReserve( // Mudei para <?> para retornar String de erro se precisar
                                        @RequestBody @Valid IngressoRequestDTO dto,
                                        Principal principal) {

        System.out.println("=============================================");
        System.out.println(">>> DEBUG: Iniciando /reservar");
        System.out.println(">>> Poltrona: " + dto.getCodigoPoltrona() + " | Sessão ID: " + dto.getSessaoId());

        try {
            // 1. Busca Usuário
            Usuario usuario;
            try {
                usuario = usuarioService.findByEmail(principal.getName());
                System.out.println(">>> Usuário encontrado: " + usuario.getId()); // Se der erro aqui, saberemos
            } catch (EntityNotFoundException e) {
                throw new BusinessException("Usuário não encontrado: " + principal.getName(), e);
            }

            // 2. Busca Sessão
            Sessao sessao = sessaoService.findById(dto.getSessaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada com ID: " + dto.getSessaoId()));
            System.out.println(">>> Sessão encontrada: " + sessao.getId());

            // 3. Monta a Entidade
            Ingresso ingresso = Ingresso.builder()
                    .usuario(usuario)
                    .sessao(sessao)
                    .codigoPoltrona(dto.getCodigoPoltrona())
                    .tipo(dto.getTipo())
                    .build();

            System.out.println(">>> Chamando service.toReserve()...");

            // 4. Salva (AQUI GERALMENTE DÁ O ERRO 500)
            Ingresso ingressoSalvo = service.toReserve(ingresso);

            System.out.println(">>> SUCESSO! Ingresso salvo com ID: " + ingressoSalvo.getId());
            System.out.println("=============================================");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mapper.map(ingressoSalvo, IngressoResponseDTO.class));

        } catch (Exception e) {
            // --- CAPTURA QUALQUER ERRO E MOSTRA NO CONSOLE ---
            System.err.println("##############################################");
            System.err.println(">>> ERRO FATAL DETECTADO NO CONTROLLER:");
            e.printStackTrace(); // <--- O MOTIVO REAL VAI APARECER AQUI
            System.err.println("##############################################");

            // Retorna o erro legível para o Postman/Frontend
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro Interno no Backend: " + e.getMessage() + " | Causa: " + e.getClass().getSimpleName());
        }
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

    @GetMapping("/my-tickets")
    public ResponseEntity<List<IngressoResponseDTO>> getMeusIngressos(Principal principal) {

        // 1. Pega o usuário logado pelo Token
        Usuario usuario = usuarioRepository.findByPessoa_Email(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 2. Busca TUDO que ele comprou (A mágica acontece aqui)
        List<Ingresso> ingressos = ingressoRepository.findByPedido_Usuario_Id(usuario.getId());

        // 3. Converte para DTO bonitinho
        List<IngressoResponseDTO> response = ingressos.stream().map(ingresso ->
                IngressoResponseDTO.builder()
                        .id(ingresso.getId())
                        .filmeTitulo(ingresso.getSessao().getFilme().getTitulo())
                        .dataHora(LocalDateTime.of(
                                ingresso.getSessao().getData(),
                                ingresso.getSessao().getHorario()
                        ))
                        .poltrona(ingresso.getCodigoPoltrona())
                        .status(ingresso.getPedido().getStatus().toString())
                        .pedidoId(ingresso.getPedido().getId())
                        .build()
        ).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Stub do envio de email
    @PostMapping("/send-email/{pedidoId}")
    public ResponseEntity<Void> reenviarEmail(@PathVariable Long pedidoId) {
        try {
            // 1. Busca o pedido para saber quem é o dono
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

            // 2. Busca os ingressos vinculados a esse pedido
            // Dica: Se não tiver esse método no repository, pode criar ou filtrar
            // Mas geralmente 'findByPedido_Id' resolve
            List<Ingresso> ingressos = ingressoRepository.findByPedido_Id(pedidoId);

            if (ingressos.isEmpty()) {
                throw new RuntimeException("Nenhum ingresso encontrado para este pedido.");
            }

            // 3. Chama o serviço de email
            emailService.sendTickets(pedido, ingressos);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
