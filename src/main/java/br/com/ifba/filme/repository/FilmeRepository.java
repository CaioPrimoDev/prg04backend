package br.com.ifba.filme.repository;

import br.com.ifba.filme.entity.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
    List<Filme> findByAtivoTrue();
    List<Filme> findByAtivoFalse();
}

