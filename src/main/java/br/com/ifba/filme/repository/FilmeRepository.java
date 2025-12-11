package br.com.ifba.filme.repository;

import br.com.ifba.filme.entity.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
    List<Filme> findByAtivoTrue();
    List<Filme> findByAtivoFalse();

    // Adicionado para o teste
    Optional<Filme> findByTitulo(String titulo);
}

