package br.com.ifba.poltrona.service;

import br.com.ifba.poltrona.dto.PoltronaCadastroDTO;
import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.repository.PoltronaRepository;
import br.com.ifba.sessao.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoltronaService implements  PoltronaIService {

    private final PoltronaRepository repository;
    private final SessaoRepository sessaoRepository;

    @Override
    public Poltrona save(PoltronaCadastroDTO dto) {
        Poltrona poltrona = Poltrona.builder()
                .letra(dto.getLetra())
                .numero(dto.getNumero())
                .codigo(dto.getCodigo())
                .sessao(sessaoRepository.getReferenceById(dto.getSessaoId()))
                .build();
        return repository.save(poltrona);
    }

    @Override
    public List<Poltrona> findBySessaoId(Long sessaoId) {
        return repository.findBySessaoId(sessaoId);
    }
}

