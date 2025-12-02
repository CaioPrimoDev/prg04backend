package br.com.ifba.filme.service;

import br.com.ifba.filme.dto.FilmeCadastroDTO;
import br.com.ifba.filme.entity.Filme;

import java.util.List;
import java.util.Optional;

public interface FilmeIService {

    Filme save(FilmeCadastroDTO dto);
    List<Filme> findByAtivoTrue();
    List<Filme> findByAtivoFalse();
    List<Filme> findAll();
    Optional<Filme> findById(Long id);
    void disable(Long id);
    void deleteById(Long id);

}
