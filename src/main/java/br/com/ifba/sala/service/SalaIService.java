package br.com.ifba.sala.service;

import br.com.ifba.sala.entity.Sala;

import java.util.List;

public interface SalaIService {

    Sala save(Sala sala);
    List<Sala> findAll();
}
