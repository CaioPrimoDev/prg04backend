package br.com.ifba.sessao.repository;

import br.com.ifba.sessao.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByData(LocalDate data);
    List<Sessao> findByFilmeIdAndData(Long filmeId, LocalDate data);
    List<Sessao> findByFilmeId(Long filmeId);
    List<Sessao> findBySalaId(Long salaId);
    List<Sessao> findByAtivoTrue();
    List<Sessao> findByAtivoFalse();
}

