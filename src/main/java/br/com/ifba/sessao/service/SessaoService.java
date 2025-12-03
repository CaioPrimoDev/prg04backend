package br.com.ifba.sessao.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import br.com.ifba.sessao.dto.SessaoCadastroDTO;
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
public class SessaoService implements SessaoIService {

    private final SessaoRepository repository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;

    @Override
    @Transactional
    public Sessao save(SessaoCadastroDTO dto) {

        Filme filme = filmeRepository.findById(dto.getFilmeId())
                .orElseThrow(() -> new BusinessException(
                        "Filme com o ID " + dto.getFilmeId() + " não encontrado."
                ));

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new BusinessException(
                        "Sala com o ID " + dto.getSalaId() + " não encontrada."
                ));

        Sessao sessao = Sessao.builder()
                .filme(filme)
                .sala(sala)
                .data(dto.getData())
                .horario(dto.getHorario())
                .ativo(true)
                .build();

        // TODO: Adicionar validação de conflito de horário/sala antes de salvar.

        return repository.save(sessao);
    }

    @Override
    public List<Sessao> findByData(LocalDate data) {
        return repository.findByData(data);
    }

    @Override
    public List<Sessao> findByFilmeIdAndData(Long filmeId, LocalDate data) {
        return repository.findByFilmeIdAndData(filmeId, data);
    }

    @Override
    public List<Sessao> findByFilmeId(Long filmeId) {
        return repository.findByFilmeId(filmeId);
    }

    @Override
    public List<Sessao> findBySalaId(Long salaId) {
        return repository.findBySalaId(salaId);
    }

    @Override
    public Optional<Sessao> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Sessao> findByAtivoTrue() {
        return repository.findByAtivoTrue();
    }

    @Override
    public List<Sessao> findByAtivoFalse() {
        return repository.findByAtivoFalse();
    }

    @Transactional
    public void disable(Long id) {
        // Busca a sessão e lança exceção se não existir
        Sessao sessao = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Sessão com o ID " + id + " não encontrada para desativação."
                ));

        sessao.setAtivo(false);
        repository.save(sessao);
    }

    // hard delete
    @Override
    @Transactional
    public void deleteById(Long id) {
        // Valida se a Sessão existe antes de tentar deletar
        if (!repository.existsById(id)) {
            throw new BusinessException(
                    "Não é possível deletar, Sessão com o ID " + id + " não encontrada."
            );
        }
        repository.deleteById(id);
    }
}