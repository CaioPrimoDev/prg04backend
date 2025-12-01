package br.com.ifba.sessao.service;

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

    @Override
    public Sessao save(Sessao s) {
        return repository.save(s);
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