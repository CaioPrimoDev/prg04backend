package br.com.ifba.poltrona.service;

import br.com.ifba.poltrona.dto.PoltronaCadastroDTO;
import br.com.ifba.poltrona.entity.Poltrona;

import java.util.List;

public interface PoltronaIService {
    Poltrona save(PoltronaCadastroDTO dto);
    List<Poltrona> findBySessaoId(Long sessaoId);
}
