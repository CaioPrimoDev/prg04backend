package br.com.ifba.sessao.service;

import br.com.ifba.sessao.dto.SessaoCadastroDTO;
import br.com.ifba.sessao.entity.Sessao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SessaoIService {

    Sessao save(SessaoCadastroDTO dto);
    List<Sessao> findByData(LocalDate data);
    List<Sessao> findByFilmeIdAndData(Long filmeId, LocalDate data);
    List<Sessao> findByFilmeId(Long filmeId);
    List<Sessao> findBySalaId(Long salaId);
    Optional<Sessao> findById(Long id);
    List<Sessao> findByAtivoTrue();
    List<Sessao> findByAtivoFalse();
    void desativar(Long id);
    void apagar(Long id);
}
