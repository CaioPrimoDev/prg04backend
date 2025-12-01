package br.com.ifba.poltrona.repository;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.sessao.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PoltronaRepository extends JpaRepository<Poltrona, Long> {
    List<Poltrona> findBySessaoId(Long sessaoId);
}

