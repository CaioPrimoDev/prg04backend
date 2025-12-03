package br.com.ifba.sala.service;

import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.entity.Sala;

import java.util.List;

public interface SalaIService {

    Sala save(SalaCadastroDTO dto);
    List<Sala> findAll();

    Sala findById(Long id);
}
