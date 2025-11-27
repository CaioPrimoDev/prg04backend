package br.com.ifba.reservapoltrona.repository;

import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaPoltronaRepository extends JpaRepository<ReservaPoltrona, Long> {
    List<ReservaPoltrona> findByReservaId(Long reservaId);
    List<ReservaPoltrona> findByPoltronaId(Long poltronaId);
}

