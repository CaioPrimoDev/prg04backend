package br.com.ifba.reserva.service;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.reserva.entity.Reserva;
import br.com.ifba.reserva.repository.ReservaRepository;
import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.repository.ReservaPoltronaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaPoltronaRepository reservaPoltronaRepository;

    @Transactional
    public Reserva criarReserva(Reserva reserva, List<Poltrona> poltronas) {

        Reserva saved = reservaRepository.save(reserva);

        for (Poltrona p : poltronas) {
            ReservaPoltrona rp = ReservaPoltrona.builder()
                    .reserva(saved)
                    .poltrona(p)
                    .build();

            reservaPoltronaRepository.save(rp);
        }

        return saved;
    }
}

