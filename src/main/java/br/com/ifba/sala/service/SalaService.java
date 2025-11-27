package br.com.ifba.sala.service;

import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository repository;

    public Sala salvar(Sala sala) {
        return repository.save(sala);
    }

    public java.util.List<Sala> listar() {
        return repository.findAll();
    }
}

