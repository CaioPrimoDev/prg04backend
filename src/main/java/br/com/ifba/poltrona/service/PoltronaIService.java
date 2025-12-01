package br.com.ifba.poltrona.service;

import br.com.ifba.poltrona.entity.Poltrona;

import java.util.List;

public interface PoltronaIService {
    Poltrona save(Poltrona poltrona);
    List<Poltrona> findBySessaoId(Long sessaoId);
}
