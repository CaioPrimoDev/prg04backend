package br.com.ifba.filme.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmeService implements FilmeIService {

    private final FilmeRepository repository;

    @Override
    public Filme save(Filme filme) {
        return repository.save(filme);
    }

    @Override
    public List<Filme> findByAtivoTrue() {
        return repository.findByAtivoTrue();
    }

    @Override
    public List<Filme> findByAtivoFalse() {
        return repository.findByAtivoFalse();
    }

    @Override
    public List<Filme> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Filme> findById(Long id) {
        return repository.findById(id);
    }

    // delete lógico (desativar)
    @Transactional
    public void disable(Long id) {
        repository.findById(id).ifPresent(f -> {
            f.setAtivo(false);
            repository.save(f);
        });
    }

    // delete hard (apaga TUDO)
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Filme atualizarImagem(Long id, String novaUrl) {
        return repository.findById(id)
                .map(filme -> {
                    filme.setImagemUrl(novaUrl);
                    return repository.save(filme);
                })
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com id: " + id));
    }

}


