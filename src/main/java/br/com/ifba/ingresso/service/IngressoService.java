package br.com.ifba.ingresso.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.entity.StatusIngresso;
import br.com.ifba.ingresso.entity.TipoIngresso;
import br.com.ifba.ingresso.repository.IngressoRepository;
import br.com.ifba.sessao.repository.SessaoRepository;
import br.com.ifba.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngressoService implements IngressoIService {

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository; // Para validar se a sessão existe
    private final UsuarioRepository usuarioRepository; // Caso precise validar usuário

    @Override
    @Transactional
    public Ingresso toReserve(Ingresso ingresso) {
        if (ingressoRepository.isPoltronaOcupada(
                ingresso.getSessao().getId(),
                ingresso.getCodigoPoltrona())) {
            throw new BusinessException("A poltrona " + ingresso.getCodigoPoltrona() + " já está ocupada.");
        }

        // Dados para cálculo
        Filme filme = ingresso.getSessao().getFilme();
        BigDecimal valorBase = filme.getPreco();

        // Lógica do Preço (Segurança no Backend)
        if (ingresso.getTipo() == TipoIngresso.MEIA) {
            // Verifica se o filme permite meia
            if (Boolean.FALSE.equals(filme.getMeiaEntrada())) { // supondo Boolean Wrapper
                throw new BusinessException("Este filme não aceita meia entrada.");
            }
            // Preço = Metade
            BigDecimal valorMeia = valorBase.divide(new BigDecimal("2"), RoundingMode.HALF_UP);
            ingresso.setPrecoPago(valorMeia);


            ingresso.setPrecoPago(ingresso.getPrecoPago().add(valorMeia.divide(new BigDecimal("10"), RoundingMode.HALF_UP)));
        } else {
            // Preço = Cheio
            ingresso.setPrecoPago(valorBase);

            ingresso.setPrecoPago(ingresso.getPrecoPago().add(valorBase.divide(new BigDecimal("10"), RoundingMode.HALF_UP)));
        }


        ingresso.setStatus(StatusIngresso.PENDENTE);
        ingresso.setDataReserva(LocalDateTime.now());

        return ingressoRepository.save(ingresso);
    }

    @Override
    @Transactional
    public void cancelReservation(Long id, String emailUsuario) {
        Ingresso ingresso = ingressoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingresso não encontrado"));

        // 1. Segurança: Verifica se o ingresso pertence ao usuário logado
        if (!ingresso.getUsuario().getPessoa().getEmail().equals(emailUsuario)) {
            try {
                throw new AccessDeniedException("Você não tem permissão para cancelar este ingresso.");
            } catch (AccessDeniedException e) {
                throw new BusinessException(e.getMessage(), e.getCause());
            }
        }

        // 2. Regra de Negócio: Só pode cancelar se estiver PENDENTE
        // Se já estiver PAGO, o fluxo é outro (estorno, etc)
        if (ingresso.getStatus() != StatusIngresso.PENDENTE) {
            throw new BusinessException("Não é possível cancelar um ingresso que já foi pago ou processado.");
        }

        // 3. EXCLUSÃO FÍSICA
        // Deletamos para liberar a constraint (sessao_id + poltrona) no banco
        ingressoRepository.delete(ingresso);
    }

    @Override
    @Transactional
    public Ingresso confirmPayment(Long id) {
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
    // Todos os domingos ás 22h - Horario de São Paulo
    @Scheduled(cron = "0 0 22 * * SUN", zone = "America/Sao_Paulo")
    public void clearUsedTickets() {

        System.out.println("Iniciando limpeza de ingressos usados...");

        // Executa o delete e retorna quantos foram apagados
        long quantidadeDeletada = ingressoRepository.deleteByStatus(StatusIngresso.USADO);

        System.out.println("Limpeza concluída! Total removido: " + quantidadeDeletada);
    }

    @Override
    @Transactional
    // Todos os domingos ás 20h - Horario de São Paulo
    @Scheduled(cron = "0 0 20 * * SUN", zone = "America/Sao_Paulo")
    public void clearCanceledTickets() {

        System.out.println("Iniciando limpeza de ingressos cancelados...");

        // Executa o delete e retorna quantos foram apagados
        long quantidadeDeletada = ingressoRepository.deleteByStatus(StatusIngresso.CANCELADO);

        System.out.println("Limpeza concluída! Total removido: " + quantidadeDeletada);
    }

    @Override
    @Transactional
    // Roda a cada 60.000 milissegundos (1 minuto)
    @Scheduled(fixedRate = 60000)
    public void releaseLockedReservations() {

        // Define o tempo limite (Ex: 15 minutos atrás)
        LocalDateTime tempoLimite = LocalDateTime.now().minusMinutes(15);

        // Deleta SÓ o que está PENDENTE e é velho
        int quantidade = ingressoRepository.deleteByStatusAndDataReservaBefore(
                StatusIngresso.PENDENTE,
                tempoLimite
        );

        if (quantidade > 0) {
            System.out.println("SISTEMA: " + quantidade + " poltronas travadas foram liberadas para venda.");
        }
    }

}
