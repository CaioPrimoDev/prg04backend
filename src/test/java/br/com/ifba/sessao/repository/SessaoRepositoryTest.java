package br.com.ifba.sessao.repository;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import br.com.ifba.sessao.entity.Sessao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Teste para o repositorio de Sessao")
@ActiveProfiles("test")
public class SessaoRepositoryTest {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    // Variáveis de instância para uso nos testes (com IDs gerados)
    private Sessao sessaoPadrao;
    private Filme filmePadrao;
    private Sala salaPadrao;

    @BeforeEach
    public void setUp() {
        // 1. Salva a Sala
        this.salaPadrao = Sala.builder()
                .nome("Sala 1")
                .build();
        this.salaPadrao = salaRepository.save(salaPadrao);

        // 2. Salva o Filme
        this.filmePadrao = Filme.builder()
                .titulo("Filme 1")
                .descricao("Filme 1")
                .trailerYoutube("Filme 1")
                .duracao("120 min")
                .preco(new BigDecimal("25.00"))
                .meiaEntrada(true)
                .imagemUrl("http://imagem.com")
                .ativo(true)
                .build();
        this.filmePadrao = filmeRepository.save(filmePadrao);

        // 3. Salva a Sessão vinculada
        this.sessaoPadrao = Sessao.builder()
                .filme(filmePadrao)
                .sala(salaPadrao)
                .data(LocalDate.now())
                .horario(LocalTime.of(20, 30))
                .ativo(true)
                .build();
        this.sessaoPadrao = sessaoRepository.save(sessaoPadrao);
    }

    @Test
    @DisplayName("Deve retornar lista de sessões quando buscar por data existente")
    public void findByData_whenSuccesful() {
        // Act
        List<Sessao> sessoes = sessaoRepository.findByData(LocalDate.now());

        // Assert
        assertThat(sessoes).isNotEmpty();
        assertThat(sessoes.getFirst().getData()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Deve retornar lista de sessões quando buscar por Filme ID e Data")
    public void findByFilmeIdAndData_whenSuccesful() {
        // Act
        List<Sessao> sessoes = sessaoRepository.findByFilmeIdAndData(
                filmePadrao.getId(),
                LocalDate.now()
        );

        // Assert
        assertThat(sessoes).isNotEmpty();
        assertThat(sessoes.getFirst().getFilme().getId()).isEqualTo(filmePadrao.getId());
    }

    @Test
    @DisplayName("Deve retornar lista de sessões quando buscar por Filme ID")
    public void findByFilmeId_whenSuccesful() {
        // Act
        List<Sessao> sessoes = sessaoRepository.findByFilmeId(filmePadrao.getId());

        // Assert
        assertThat(sessoes).isNotEmpty();
        assertThat(sessoes.getFirst().getFilme().getTitulo()).isEqualTo(filmePadrao.getTitulo());
    }

    @Test
    @DisplayName("Deve retornar lista de sessões quando buscar por Sala ID")
    public void findBySalaId_whenSuccesful() {
        // Act
        List<Sessao> sessoes = sessaoRepository.findBySalaId(salaPadrao.getId());

        // Assert
        assertThat(sessoes).isNotEmpty();
        assertThat(sessoes.getFirst().getSala().getNome()).isEqualTo(salaPadrao.getNome());
    }

    @Test
    @DisplayName("Deve retornar sessão quando buscar por ID existente")
    public void findById_whenSuccesful() {
        // Act
        Optional<Sessao> sessaoFound = sessaoRepository.findById(sessaoPadrao.getId());

        // Assert
        assertThat(sessaoFound)
                .isPresent()
                .hasValueSatisfying(sessao -> {
                    assertThat(sessao.getId()).isEqualTo(sessaoPadrao.getId());
                    assertThat(sessao.getHorario()).isEqualTo(LocalTime.of(20, 30));
                });
    }

    @Test
    @DisplayName("Deve retornar lista de sessões ativas")
    public void findByAtivoTrue_whenSuccesful() {
        // Act
        List<Sessao> sessoes = sessaoRepository.findByAtivoTrue();

        // Assert
        assertThat(sessoes).isNotEmpty();
        // Garante que todas as retornadas são true
        assertThat(sessoes).allMatch(Sessao::getAtivo);
    }

    @Test
    @DisplayName("Deve retornar lista de sessões inativas")
    public void findByAtivoFalse_whenSuccesful() {
        // Arrange: Precisamos criar uma sessão inativa, pois o setUp só cria ativa
        Sessao sessaoInativa = Sessao.builder()
                .filme(filmePadrao)
                .sala(salaPadrao)
                .data(LocalDate.now().plusDays(1))
                .horario(LocalTime.of(10, 0))
                .ativo(false) // <--- O Pulo do gato
                .build();
        sessaoRepository.save(sessaoInativa);

        // Act
        List<Sessao> sessoes = sessaoRepository.findByAtivoFalse();

        // Assert
        assertThat(sessoes).isNotEmpty();
        assertThat(sessoes).allMatch(sessao -> !sessao.getAtivo()); // Garante que são false
    }


    // -------------------------------------------------------------------------
    // TESTES DE FALHA (NOT FOUND)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por Data sem sessões")
    public void findByData_whenNotFound() {
        // Act: Busca por uma data futura onde não criamos sessão
        List<Sessao> sessoes = sessaoRepository.findByData(LocalDate.now().plusYears(1));

        // Assert
        assertThat(sessoes).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por Filme e Data incorretos")
    public void findByFilmeIdAndData_whenNotFound() {
        // Act: Filme existe, mas a data não tem sessão
        List<Sessao> sessoes = sessaoRepository.findByFilmeIdAndData(
                filmePadrao.getId(),
                LocalDate.now().plusYears(1)
        );

        // Assert
        assertThat(sessoes).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por Filme ID inexistente")
    public void findByFilmeId_whenNotFound() {
        Long idInexistente = 9999L;

        // Act
        List<Sessao> sessoes = sessaoRepository.findByFilmeId(idInexistente);

        // Assert
        assertThat(sessoes).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando buscar por Sala ID inexistente")
    public void findBySalaId_whenNotFound() {
        Long idInexistente = 9999L;

        // Act
        List<Sessao> sessoes = sessaoRepository.findBySalaId(idInexistente);

        // Assert
        assertThat(sessoes).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando buscar por ID inexistente")
    public void findById_whenNotFound() {
        Long idInexistente = 9999L;

        // Act
        Optional<Sessao> sessaoFound = sessaoRepository.findById(idInexistente);

        // Assert
        assertThat(sessaoFound).isNotPresent();
    }
}