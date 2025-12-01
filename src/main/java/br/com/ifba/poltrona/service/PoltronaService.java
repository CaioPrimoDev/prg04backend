package br.com.ifba.poltrona.service;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.repository.PoltronaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoltronaService implements  PoltronaIService {

    private final PoltronaRepository repository;

    @Override
    public Poltrona save(Poltrona poltrona) {
        return repository.save(poltrona);
    }

    @Override
    public List<Poltrona> findBySessaoId(Long sessaoId) {
        return repository.findBySessaoId(sessaoId);
    }
}

