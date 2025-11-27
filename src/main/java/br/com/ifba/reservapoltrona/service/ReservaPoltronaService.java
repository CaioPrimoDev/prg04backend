package br.com.ifba.reservapoltrona.service;

import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.repository.ReservaPoltronaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Essa classe irá garantir que duas pessoas comprem a mesma poltrona (na mesma sessão)
 */

@Service
@RequiredArgsConstructor
public class ReservaPoltronaService {

    private final ReservaPoltronaRepository repository;

    public ReservaPoltrona salvar(ReservaPoltrona rp) {
        rp.setCriadoEm(LocalDateTime.now());
        return repository.save(rp);
    }

    public List<ReservaPoltrona> listarPorReserva(Long reservaId) {
        return repository.findByReservaId(reservaId);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
