package br.com.ifba.filme.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmeService {

    private final FilmeRepository repository;

    public Filme salvar(Filme filme) {
        return repository.save(filme);
    }

    public List<Filme> listarAtivos() {
        return repository.findByAtivoTrue();
    }

    public List<Filme> listarInativos() {
        return repository.findByAtivoFalse();
    }

    public List<Filme> listarTodos() {
        return repository.findAll();
    }

    public Optional<Filme> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // delete lógico (desativar)
    @Transactional
    public void desativar(Long id) {
        repository.findById(id).ifPresent(f -> {
            f.setAtivo(false);
            repository.save(f);
        });
    }

    // delete hard (apaga TUDO)
    @Transactional
    public void apagar(Long id) {
        repository.deleteById(id);
    }
}


