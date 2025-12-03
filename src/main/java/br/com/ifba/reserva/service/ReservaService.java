package br.com.ifba.reserva.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.repository.PoltronaRepository;
import br.com.ifba.reserva.dto.ReservaCadastroDTO;
import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.reserva.repository.ReservaRepository;
import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.repository.ReservaPoltronaRepository;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.repository.SessaoRepository;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaIService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;      // Necessário para buscar Usuario
    private final SessaoRepository sessaoRepository;        // Necessário para buscar Sessao
    private final PoltronaRepository poltronaRepository;    // Necessário para buscar Poltronas
    private final ReservaPoltronaRepository reservaPoltronaRepository; // Necessário para a relação N:N

    @Override
    @Transactional
    public Reserva criarReserva(ReservaCadastroDTO dto) {

        // 1. Validação de Existência (substituindo getReferenceById)
        // Usamos findById para garantir que a entidade existe antes de prosseguir.
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new BusinessException(
                        "Usuário com o ID " + dto.getUsuarioId() + " não encontrado."
                ));

        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(() -> new BusinessException(
                        "Sessão com o ID " + dto.getSessaoId() + " não encontrada."
                ));

        // Busca as Poltronas
        List<Poltrona> poltronas = poltronaRepository.findAllById(dto.getPoltronasIds());

        // 2. Validação de Poltronas (Regra de Negócio)
        // Garante que TODOS os IDs de poltronas enviados foram encontrados.
        if (poltronas.size() != dto.getPoltronasIds().size()) {
            // Lógica mais complexa poderia identificar qual ID falhou, mas essa é uma verificação mínima.
            throw new BusinessException(
                    "Um ou mais IDs de poltronas não são válidos ou não foram encontrados."
            );
        }

        // Valida se as poltronas já estão bloqueadas
        boolean anyBlocked = poltronas.stream().anyMatch(Poltrona::getBloqueada);
        if (anyBlocked) {
            throw new BusinessException(
                    "Uma ou mais poltronas selecionadas já estão bloqueadas para esta sessão."
            );
        }

        // 3. Cria a Entidade Reserva
        Reserva novaReserva = Reserva.builder()
                .usuario(usuario)
                .sessao(sessao)
                .status("TEMP")
                .token(UUID.randomUUID().toString())
                .expiracao(LocalDateTime.now().plusMinutes(15))
                .total(BigDecimal.ZERO)
                .build();

        Reserva savedReserva = reservaRepository.save(novaReserva);

        // 4. Cria as ReservaPoltrona e Bloqueia os Assentos
        for (Poltrona p : poltronas) {
            p.setBloqueada(true);
            poltronaRepository.save(p);

            ReservaPoltrona rp = ReservaPoltrona.builder()
                    .reserva(savedReserva)
                    .poltrona(p)
                    .criadoEm(LocalDateTime.now())
                    .build();

            reservaPoltronaRepository.save(rp);
        }

        return savedReserva;
    }


    @Override
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}

