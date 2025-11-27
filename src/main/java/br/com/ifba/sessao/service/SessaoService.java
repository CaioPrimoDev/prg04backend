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
public class SessaoService {

    private final SessaoRepository repository;

    public Sessao salvar(Sessao s) {
        return repository.save(s);
    }

    public List<Sessao> listarPorData(LocalDate data) {
        return repository.findByData(data);
    }

    public List<Sessao> listarPorFilmeEData(Long filmeId, LocalDate data) {
        return repository.findByFilmeIdAndData(filmeId, data);
    }

    public Optional<Sessao> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Sessao> listarAtivas() {
        return repository.findByAtivoTrue();
    }

    @Transactional
    public void desativar(Long id) {
        repository.findById(id).ifPresent(s -> {
            s.setAtivo(false);
            repository.save(s);
        });
    }

    // hard delete
    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}