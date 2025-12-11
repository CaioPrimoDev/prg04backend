package br.com.ifba.filme.repository;

import br.com.ifba.filme.entity.Filme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Teste para o repositório de Filme")
@ActiveProfiles("test")
public class FilmeRepositoryTest {

    @Autowired
    private FilmeRepository filmeRepository;

    // Constantes para garantir consistência nos testes
    private static final String TITULO_PADRAO = "O Senhor dos Anéis";
    private static final String TITULO_INEXISTENTE = "Filme que não existe";

    // Instância padrão salva antes de cada teste
    private Filme filmePadrao;

    @BeforeEach
    public void setUp() {
        this.filmePadrao = Filme.builder()
                .titulo(TITULO_PADRAO)
                .descricao("Um filme de aventura épico.")
                .trailerYoutube("https://youtube.com/trailer")
                .duracao("178 min")
                .preco(new BigDecimal("25.50"))
                .meiaEntrada(true)
                .imagemUrl("http://supabase.com/img.jpg")
                .ativo(true) // O padrão é ATIVO
                .build();

        this.filmeRepository.save(filmePadrao);
    }

    // -------------------------------------------------------------------------
    // TESTES: Buscas por Atributos (Titulo)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar filme quando buscar por Título existente")
    public void findByTitulo_whenSuccesful() {
        // Act
        Optional<Filme> filmeFound = filmeRepository.findByTitulo(TITULO_PADRAO);

        // Assert
        assertThat(filmeFound)
                .isPresent()
                .hasValueSatisfying(filme -> {
                    assertThat(filme.getTitulo()).isEqualTo(TITULO_PADRAO);
                    assertThat(filme.getPreco()).isEqualByComparingTo(new BigDecimal("25.50"));
                });
    }

    @Test
    @DisplayName("Não deve retornar filme quando buscar por Título inexistente")
    public void findByTitulo_whenNotFound() {
        // Act
        Optional<Filme> filmeFound = filmeRepository.findByTitulo(TITULO_INEXISTENTE);

        // Assert
        assertThat(filmeFound).isNotPresent();
    }

    // -------------------------------------------------------------------------
    // TESTES: Listagem por Status (Ativo/Inativo)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar lista de filmes ativos")
    public void findByAtivoTrue_whenSuccesful() {
        // Act
        List<Filme> filmesAtivos = filmeRepository.findByAtivoTrue();

        // Assert
        assertThat(filmesAtivos).isNotEmpty();
        assertThat(filmesAtivos).allMatch(Filme::getAtivo); // Garante que todos são true
        assertThat(filmesAtivos.getFirst().getTitulo()).isEqualTo(TITULO_PADRAO);
    }

    @Test
    @DisplayName("Deve retornar lista de filmes desativados")
    public void findByAtivoFalse_whenSuccesful() {
        // Arrange: Precisamos criar um filme desativado, pois o setUp só cria um ativo
        Filme filmeDesativado = Filme.builder()
                .titulo("Filme Antigo")
                .descricao("Filme fora de cartaz")
                .preco(BigDecimal.TEN)
                .ativo(false) // <--- O Pulo do Gato
                .build();
        filmeRepository.save(filmeDesativado);

        // Act
        List<Filme> filmesDesativados = filmeRepository.findByAtivoFalse();

        // Assert
        assertThat(filmesDesativados).isNotEmpty();
        assertThat(filmesDesativados).allMatch(filme -> !filme.getAtivo()); // Garante que todos são false
        assertThat(filmesDesativados.getFirst().getTitulo()).isEqualTo("Filme Antigo");
    }

    // -------------------------------------------------------------------------
    // TESTES: Padrão JPA (FindById, Save, Delete)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar filme quando buscar por ID existente")
    public void findById_whenSuccesful() {
        // Act
        Optional<Filme> filmeFound = filmeRepository.findById(filmePadrao.getId());

        // Assert
        assertThat(filmeFound)
                .isPresent()
                .hasValueSatisfying(filme -> {
                    assertThat(filme.getId()).isEqualTo(filmePadrao.getId());
                });
    }

    @Test
    @DisplayName("Não deve retornar filme quando buscar por ID inexistente")
    public void findById_whenNotFound() {
        // Act
        Optional<Filme> filmeFound = filmeRepository.findById(9999L);

        // Assert
        assertThat(filmeFound).isNotPresent();
    }

    @Test
    @DisplayName("Deve salvar um filme com sucesso")
    public void save_whenSuccesful() {
        // Arrange
        Filme novoFilme = Filme.builder()
                .titulo("Novo Filme")
                .preco(BigDecimal.TEN)
                .ativo(true)
                .build();

        // Act
        Filme filmeSalvo = filmeRepository.save(novoFilme);

        // Assert
        assertThat(filmeSalvo).isNotNull();
        assertThat(filmeSalvo.getId()).isNotNull();
        assertThat(filmeSalvo.getTitulo()).isEqualTo("Novo Filme");
    }

    @Test
    @DisplayName("Deve deletar um filme com sucesso")
    public void delete_whenSuccesful() {
        // Act
        filmeRepository.delete(filmePadrao);

        // Assert
        Optional<Filme> filmeDeletado = filmeRepository.findById(filmePadrao.getId());
        assertThat(filmeDeletado).isNotPresent();
    }
}
