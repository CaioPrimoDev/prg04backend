package br.com.ifba.reservapoltrona.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import br.com.ifba.reservapoltrona.repository.ReservaPoltronaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Essa classe irá garantir que duas pessoas comprem a mesma poltrona (na mesma sessão)
 */

@Service
@RequiredArgsConstructor
public class ReservaPoltronaService implements ReservaPoltronaIService {

    private final ReservaPoltronaRepository repository;

    @Override
    public ReservaPoltrona save(ReservaPoltrona rp) {
        rp.setCriadoEm(LocalDateTime.now());
        return repository.save(rp);
    }

    @Override
    public List<ReservaPoltrona> findByReservaId(Long reservaId) {
        return repository.findByReservaId(reservaId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(
                    "Não é possível cancelar, ReservaPoltrona com o ID " + id + " não encontrada."
            );
        }
        repository.deleteById(id);
    }
}
