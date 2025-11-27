package br.com.ifba.sala.repository;

import br.com.ifba.sala.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {
}

