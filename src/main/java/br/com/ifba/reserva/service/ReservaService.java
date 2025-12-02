package br.com.ifba.reserva.service;

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

        Usuario usuario = usuarioRepository.getReferenceById(dto.getUsuarioId());
        Sessao sessao = sessaoRepository.getReferenceById(dto.getSessaoId());

        List<Poltrona> poltronas = poltronaRepository.findAllById(dto.getPoltronasIds());

        // TODO: Adicionar lógica de validação de poltronas (se estão bloqueadas/disponíveis)

        // 2. Cria a Entidade Reserva com a lógica de negócio
        Reserva novaReserva = Reserva.builder()
                .usuario(usuario)
                .sessao(sessao)
                .status("TEMP") // Status inicial (pode ser CONFIRMADA se o pagamento for instantâneo)
                .token(UUID.randomUUID().toString()) // Gera um token de rastreamento
                .expiracao(LocalDateTime.now().plusMinutes(15)) // Define um tempo de expiração
                // TODO: Adicionar lógica real de cálculo (baseada no preço da Sessao e meia/inteira)
                .total(BigDecimal.ZERO)
                .build();

        // 3. Salva a Reserva principal
        Reserva savedReserva = reservaRepository.save(novaReserva);

        for (Poltrona p : poltronas) {
            // Lógica do Bloqueio:
            p.setBloqueada(true); // Trava a poltrona
            poltronaRepository.save(p); // Persiste o novo status de bloqueio

            // Lógica da Ligação (ReservaPoltrona):
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

