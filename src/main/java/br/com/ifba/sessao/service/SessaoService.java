package br.com.ifba.sessao.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import br.com.ifba.sessao.dto.SessaoCadastroDTO;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessaoService implements SessaoIService {

    private final SessaoRepository repository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;

    @Override
    public Sessao save(SessaoCadastroDTO dto) {

        // 1. Cria a referência (Proxy) do Filme/Sala, usando apenas o ID
        Filme filme = filmeRepository.getReferenceById(dto.getFilmeId());
        Sala sala = salaRepository.getReferenceById(dto.getSalaId());

        // 2. Mapeia DTO para a Entidade Sessao (omiti o ModelMapper aqui)
        Sessao sessao = new Sessao();
        sessao.setFilme(filme); // JPA sabe que filme é apenas o ID
        sessao.setSala(sala);   // JPA sabe que sala é apenas o ID
        sessao.setData(dto.getData());
        // ...

        return repository.save(sessao);
    }

    @Override
    public List<Sessao> findByData(LocalDate data) {
        return repository.findByData(data);
    }

    @Override
    public List<Sessao> findByFilmeIdAndData(Long filmeId, LocalDate data) {
        return repository.findByFilmeIdAndData(filmeId, data);
    }

    @Override
    public List<Sessao> findByFilmeId(Long filmeId) {
        return repository.findByFilmeId(filmeId);
    }

    @Override
    public List<Sessao> findBySalaId(Long salaId) {
        return repository.findBySalaId(salaId);
    }

    @Override
    public Optional<Sessao> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Sessao> findByAtivoTrue() {
        return repository.findByAtivoTrue();
    }

    @Override
    public List<Sessao> findByAtivoFalse() {
        return repository.findByAtivoFalse();
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setAtivo(false);
            repository.save(s);
        });
    }

    // hard delete
    @Override
    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}