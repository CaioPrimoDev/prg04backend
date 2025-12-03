package br.com.ifba.poltrona.service;

import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.poltrona.dto.PoltronaCadastroDTO;
import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.poltrona.repository.PoltronaRepository;
import br.com.ifba.sessao.entity.Sessao;
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

        // 1. Validação de Existência da Sessão
        // Usamos findById() e orElseThrow para garantir que a Sessao realmente existe.
        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(() -> new BusinessException("Sessão com o ID " + dto.getSessaoId() + " não encontrada."));

        // TODO: Adicionar validação de unicidade da Poltrona (código + sessão) antes de salvar.

        Poltrona poltrona = Poltrona.builder()
                .letra(dto.getLetra())
                .numero(dto.getNumero())
                .codigo(dto.getCodigo())
                .sessao(sessao) // Usa a Entidade Sessao encontrada
                .build();

        return repository.save(poltrona);
    }

    @Override
    public List<Poltrona> findBySessaoId(Long sessaoId) {
        return repository.findBySessaoId(sessaoId);
    }
}

