package br.com.ifba.sala.service;

import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.entity.Sala;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalaIService {

    Sala save(SalaCadastroDTO dto);
    Page<Sala> findAll(Pageable pageable);

    Sala findById(Long id);
}
