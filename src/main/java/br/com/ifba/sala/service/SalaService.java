package br.com.ifba.sala.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.sala.dto.SalaCadastroDTO;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalaService implements SalaIService {

    private final SalaRepository repository;

    @Override
    public Sala save(SalaCadastroDTO dto) {
        // TODO: Adicionar validação de unicidade do nome da Sala aqui, se necessário.
        Sala sala = new Sala();
        sala.setNome(dto.getNome());
        return repository.save(sala);
    }

    @Override
    public Page<Sala> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Sala findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Sala com o ID " + id + " não encontrada."));
    }
}

