package br.com.ifba.sala.service;

import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService implements SalaIService {

    private final SalaRepository repository;

    @Override
    public Sala save(Sala sala) {
        return repository.save(sala);
    }

    @Override
    public List<Sala> findAll() {
        return repository.findAll();
    }
}

