package br.com.ifba.ingresso.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.ingresso.dto.IngressoRequestDTO;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.entity.StatusIngresso;
import br.com.ifba.ingresso.repository.IngressoRepository;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.repository.SessaoRepository;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngressoService implements IngressoIService {

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository; // Para validar se a sessão existe
    private final UsuarioRepository usuarioRepository; // Caso precise validar usuário

    @Override
    @Transactional // IMPORTANTE: Se der erro em 1 poltrona, desfaz todas as outras salvas neste loop.
    public List<Ingresso> reservar(IngressoRequestDTO dto) {

        // 1. Buscas otimizadas (Busca Sessão e Usuário uma única vez)
        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        List<Ingresso> novosIngressos = new ArrayList<>();

        // 2. Loop de validação e criação
        for (String codigoPoltrona : dto.getPoltronas()) {

            // Verifica Disponibilidade
            if (ingressoRepository.isPoltronaOcupada(sessao.getId(), codigoPoltrona)) {
                throw new BusinessException(
                        "A poltrona " + codigoPoltrona + " não está mais disponível. Por favor, refaça sua seleção."
                );
            }

            // Cria o objeto
            Ingresso ingresso = Ingresso.builder()
                    .sessao(sessao)
                    .usuario(usuario)
                    .codigoPoltrona(codigoPoltrona)
                    .status(StatusIngresso.PENDENTE)
                    .dataReserva(LocalDateTime.now())
                    // .precoPago(sessao.getFilme().getPreco()) // Opcional: já salvar o preço aqui
                    .build();

            novosIngressos.add(ingresso);
        }

        // 3. Salva todos de uma vez (Batch Insert)
        return ingressoRepository.saveAll(novosIngressos);
    }

    @Override
    @Transactional
    public Ingresso confirmarPagamento(Long id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingresso não encontrado"));

        if (ingresso.getStatus() == StatusIngresso.CANCELADO) {
            throw new BusinessException("Não é possível confirmar um ingresso cancelado.");
        }

        ingresso.setStatus(StatusIngresso.CONFIRMADO);
        // Aqui você setaria o valor pago, data do pagamento, etc.

        return ingressoRepository.save(ingresso);
    }

    @Override
    public List<String> findPoltronasOcupadas(Long sessaoId) {
        // Retorna apenas as Strings (ex: "A1", "B5") para o Front bloquear
        return ingressoRepository.findPoltronasOcupadas(sessaoId);
    }

    // --- MÉTODOS QUE FALTAVAM ---

    @Override
    public Optional<Ingresso> findById(Long id) {
        return ingressoRepository.findById(id);
    }

    @Override
    public List<Ingresso> findByUsuarioId(Long usuarioId) {
        return ingressoRepository.findAllByUsuarioId(usuarioId);
    }

    /**
     * Lógica de Cancelamento:
     * Ao mudar o status para CANCELADO, a query do Repository (findPoltronasOcupadas)
     * para de trazer essa poltrona. Ou seja, ela fica "verde" (livre) no mapa
     * instantaneamente para outros usuários.
     */
    @Override
    @Transactional
    public void cancelar(Long id) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingresso não encontrado"));

        // Talvez eu implemente isso um dia
        /*
         if (ingresso.getSessao().getDataHora().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível cancelar ingresso de sessão já iniciada.");
        }
         */

        ingresso.setStatus(StatusIngresso.CANCELADO);
        ingressoRepository.save(ingresso);

        /*
        if (ingresso.getStatus() == StatusIngresso.PENDENTE) {
            ingressoRepository.delete(ingresso);
        } else {
            ingresso.setStatus(StatusIngresso.CANCELADO);
            ingressoRepository.save(ingresso);
        }
        */
    }

    @Override
    @Transactional
    // Todos os domingos ás 22h - Horario de São Paulo
    @Scheduled(cron = "0 0 22 * * SUN", zone = "America/Sao_Paulo")
    public void limparIngressosUsados() {

        System.out.println("Iniciando limpeza de ingressos usados...");

        // Executa o delete e retorna quantos foram apagados
        long quantidadeDeletada = ingressoRepository.deleteByStatus(StatusIngresso.USADO);

        System.out.println("Limpeza concluída! Total removido: " + quantidadeDeletada);
    }

    @Override
    @Transactional
    // Todos os domingos ás 20h - Horario de São Paulo
    @Scheduled(cron = "0 0 20 * * SUN", zone = "America/Sao_Paulo")
    public void limparIngressosCancelados() {

        System.out.println("Iniciando limpeza de ingressos cancelados...");

        // Executa o delete e retorna quantos foram apagados
        long quantidadeDeletada = ingressoRepository.deleteByStatus(StatusIngresso.CANCELADO);

        System.out.println("Limpeza concluída! Total removido: " + quantidadeDeletada);
    }
}
