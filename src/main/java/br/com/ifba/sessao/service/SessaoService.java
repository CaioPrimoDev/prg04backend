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
import java.time.LocalDateTime;
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

        LocalDateTime dataHoraSessao = LocalDateTime.of(dto.getData(), dto.getHorario());

        if (dataHoraSessao.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível agendar uma sessão em data/hora passada.");
        }

        Filme filme = filmeRepository.findById(dto.getFilmeId())
                .orElseThrow(() -> new BusinessException(
                        "Filme com o ID " + dto.getFilmeId() + " não encontrado."
                ));

        if (!filme.getAtivo()) {
            filme.setAtivo(true);
            filmeRepository.save(filme);
        }

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new BusinessException(
                        "Sala com o ID " + dto.getSalaId() + " não encontrada."
                ));

        Sessao sessao = Sessao.builder()
                .filme(filme)
                .sala(sala)
                .data(dto.getData())
                // todo: Futuramente, garantir que não há conflito de horário caso as sessões sejam na mesma sala
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

    @Override
    public List<Sessao> findAll() { return repository.findAll(); }

    @Override
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
        Sessao sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));

        Long filmeId = sessao.getFilme().getId();

        // Deleta a sessão
        repository.deleteById(id);

        // Verifica o status do filme após a exclusão
        atualizarStatusFilme(filmeId);
    }

    // MÉTODO AUXILIAR: Verifica se o filme ainda deve estar ativo
    private void atualizarStatusFilme(Long filmeId) {
        // Pergunta ao banco: "Ainda tem sessão de hoje para frente?"
        boolean temSessaoFutura = repository
                .existsByFilmeIdAndDataGreaterThanEqual(filmeId, LocalDate.now());

        Filme filme = filmeRepository.findById(filmeId).orElse(null);

        if (filme != null) {
            // Se tem sessão futura -> Ativo (true)
            // Se NÃO tem sessão futura -> Inativo (false)
            if (filme.getAtivo() != temSessaoFutura) {
                filme.setAtivo(temSessaoFutura);
                filmeRepository.save(filme);
            }
        }
    }
}