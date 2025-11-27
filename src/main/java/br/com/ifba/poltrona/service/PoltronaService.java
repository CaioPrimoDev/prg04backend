package br.com.ifba.poltrona.service;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.repository.PoltronaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PoltronaService {

    private final PoltronaRepository repository;

    public Poltrona salvar(Poltrona poltrona) {
        return repository.save(poltrona);
    }

    public java.util.List<Poltrona> listarPorSala(Long salaId) {
        return repository.findBySalaId(salaId);
    }
}

