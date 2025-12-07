package br.com.ifba.filme.service;

import br.com.ifba.filme.dto.FilmeCadastroDTO;
import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmeService implements FilmeIService {

    private final FilmeRepository repository;

    @Override
    public Filme save(FilmeCadastroDTO dto) {
        // Mapeia DTO para a Entidade Filme
        Filme filme = Filme.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .imagemUrl(dto.getImagemUrl())
                .trailerYoutube(dto.getTrailerYoutube())
                .preco(dto.getPreco())
                .meiaEntrada(dto.getMeiaEntrada())
                .ativo(true) // Ativo por padrão
                .build();
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
    public Page<Filme> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Filme> findById(Long id) {
        return repository.findById(id);
    }

    // delete lógico (desativar)
    @Transactional
    public void disable(Long id) {
        Filme filme = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Filme com o ID " + id + " não encontrado para desativação."
                ));

        filme.setAtivo(false);
        repository.save(filme);
    }

    // delete hard (apaga TUDO)
    @Transactional
    public void deleteById(Long id) {
        // Valida se o filme existe antes de tentar deletar
        if (!repository.existsById(id)) {
            throw new BusinessException(
                    "Não é possível apagar, Filme com o ID " + id + " não encontrado."
            );
        }
        repository.deleteById(id);
    }

    @Transactional
    public Filme atualizarImagem(Long id, String novaUrl) {
        Filme filme = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Filme com o ID " + id + " não encontrado para atualização de imagem."
                ));

        filme.setImagemUrl(novaUrl);
        return repository.save(filme);
    }

}


