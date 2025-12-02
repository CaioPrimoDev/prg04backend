package br.com.ifba.reserva.repository;

import br.com.ifba.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findBySessaoId(Long sessaoId);
    List<Reserva> findByUsuarioId(Long usuarioId);
}

