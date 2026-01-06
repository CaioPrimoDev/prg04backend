package br.com.ifba.filme.service;

import br.com.ifba.filme.dto.FilmeCadastroDTO;
import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.infrastructure.exception.BusinessException;
import br.com.ifba.infrastructure.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmeService implements FilmeIService {

    private final FilmeRepository repository;
    private final FileStorageService storageService;

    @Override
    @Transactional
    public Filme save(FilmeCadastroDTO dto, MultipartFile imagem) {
        String urlDaImagem = null;

        // Se veio um arquivo, faz o upload no Supabase e pega a URL
        if (imagem != null && !imagem.isEmpty()) {
            urlDaImagem = storageService.uploadImagem(imagem);
        }

        // Constrói o objeto Filme
        Filme filme = Filme.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .imagemUrl(urlDaImagem)
                .trailerYoutube(dto.getTrailerYoutube())
                .preco(dto.getPreco())
                .meiaEntrada(dto.getMeiaEntrada())
                .duracao(dto.getDuracao())
                .genero(dto.getGenero())
                .classificacao(dto.getClassificacao())
                .ativo(true) // Ativo por padrão
                .build();

        return repository.save(filme);
    }

    @Override
    @Transactional
    public Filme atualizar(Long id, FilmeCadastroDTO dto, MultipartFile imagem) {
        Filme filmeExistente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Filme não encontrado"));

        filmeExistente.setTitulo(dto.getTitulo());
        filmeExistente.setDescricao(dto.getDescricao());
        filmeExistente.setTrailerYoutube(dto.getTrailerYoutube());
        filmeExistente.setPreco(dto.getPreco());
        filmeExistente.setMeiaEntrada(dto.getMeiaEntrada());
        filmeExistente.setDuracao(dto.getDuracao());
        filmeExistente.setGenero(dto.getGenero());
        filmeExistente.setClassificacao(dto.getClassificacao());

        if (imagem != null && !imagem.isEmpty()) {
            String novaUrl = storageService.uploadImagem(imagem);
            filmeExistente.setImagemUrl(novaUrl);
        }
        // Se 'imagem' for nula, ele simplesmente mantém o que já estava no 'filmeExistente'

        return repository.save(filmeExistente);
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
    @Transactional(readOnly = true)
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

}


