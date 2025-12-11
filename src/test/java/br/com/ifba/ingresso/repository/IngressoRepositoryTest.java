package br.com.ifba.ingresso.repository;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.entity.StatusIngresso;
import br.com.ifba.sala.entity.Sala;
import br.com.ifba.sala.repository.SalaRepository;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.repository.SessaoRepository;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Teste para o repositório de Ingresso")
@ActiveProfiles("test")
public class IngressoRepositoryTest {

    @Autowired
    private IngressoRepository ingressoRepository;

    // Dependências necessárias para criar um Ingresso
    @Autowired private SessaoRepository sessaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FilmeRepository filmeRepository;
    @Autowired private SalaRepository salaRepository;

    // Entidades padrão para uso nos testes
    private Sessao sessaoPadrao;
    private Usuario usuarioPadrao;

    @BeforeEach
    public void setUp() {
        // 1. Criar e Salvar Dependências (Cadeia: Sala/Filme -> Sessao -> Usuario)

        Sala sala = salaRepository.save(Sala.builder().nome("Sala Teste").build());

        Filme filme = filmeRepository.save(Filme.builder()
                .titulo("Filme Teste")
                .descricao("Desc")
                .duracao("120m")
                .preco(BigDecimal.TEN)
                .imagemUrl("img.jpg")
                .ativo(true)
                .build());

        usuarioPadrao = usuarioRepository.save(Usuario.builder()
                .email("joao@email.com")
                .cpf("12345678900")
                .senha("123")
                .status(true)
                .build());

        sessaoPadrao = sessaoRepository.save(Sessao.builder()
                .sala(sala)
                .filme(filme)
                .data(LocalDate.now())
                .horario(LocalTime.of(20, 0))
                .ativo(true)
                .build());

        // 2. Salvar um Ingresso Padrão (Status CONFIRMADO, Assento "A1")
        Ingresso ingressoConfirmado = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("A1")
                .status(StatusIngresso.CONFIRMADO)
                .dataReserva(LocalDateTime.now())
                .precoPago(BigDecimal.TEN)
                .build();
        ingressoRepository.save(ingressoConfirmado);
    }

    // -------------------------------------------------------------------------
    // TESTES: findPoltronasOcupadas
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar códigos de poltronas PENDENTES e CONFIRMADAS")
    public void findPoltronasOcupadas_whenSuccesful() {
        // Arrange: Criar mais um ingresso PENDENTE na poltrona A2
        Ingresso ingressoPendente = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("A2")
                .status(StatusIngresso.PENDENTE)
                .dataReserva(LocalDateTime.now())
                .build();
        ingressoRepository.save(ingressoPendente);

        // Act
        List<String> poltronasOcupadas = ingressoRepository.findPoltronasOcupadas(sessaoPadrao.getId());

        // Assert
        assertThat(poltronasOcupadas).hasSize(2);
        assertThat(poltronasOcupadas).contains("A1", "A2"); // Deve ter a confirmada (setup) e a pendente
    }

    @Test
    @DisplayName("NÃO deve retornar poltronas de ingressos CANCELADOS")
    public void findPoltronasOcupadas_shouldIgnoreCancelled() {
        // Arrange: Criar um ingresso CANCELADO na poltrona A3
        Ingresso ingressoCancelado = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("A3")
                .status(StatusIngresso.CANCELADO)
                .dataReserva(LocalDateTime.now())
                .build();
        ingressoRepository.save(ingressoCancelado);

        // Act
        List<String> poltronasOcupadas = ingressoRepository.findPoltronasOcupadas(sessaoPadrao.getId());

        // Assert
        assertThat(poltronasOcupadas).hasSize(1); // Só deve ter o "A1" do setUp
        assertThat(poltronasOcupadas).contains("A1");
        assertThat(poltronasOcupadas).doesNotContain("A3");
    }

    // -------------------------------------------------------------------------
    // TESTES: isPoltronaOcupada
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar TRUE se a poltrona estiver ocupada (Confirmada/Pendente)")
    public void isPoltronaOcupada_whenTrue() {
        // Act & Assert
        // "A1" foi criada no setUp como CONFIRMADA
        boolean isOcupada = ingressoRepository.isPoltronaOcupada(sessaoPadrao.getId(), "A1");

        assertThat(isOcupada).isTrue();
    }

    @Test
    @DisplayName("Deve retornar FALSE se a poltrona estiver livre")
    public void isPoltronaOcupada_whenFalse_FreeSeat() {
        // Act & Assert
        // "B5" não existe no banco
        boolean isOcupada = ingressoRepository.isPoltronaOcupada(sessaoPadrao.getId(), "B5");

        assertThat(isOcupada).isFalse();
    }

    @Test
    @DisplayName("Deve retornar FALSE se a poltrona estiver CANCELADA")
    public void isPoltronaOcupada_whenFalse_CancelledSeat() {
        // Arrange
        Ingresso cancelado = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("C1")
                .status(StatusIngresso.CANCELADO)
                .dataReserva(LocalDateTime.now())
                .build();
        ingressoRepository.save(cancelado);

        // Act
        boolean isOcupada = ingressoRepository.isPoltronaOcupada(sessaoPadrao.getId(), "C1");

        // Assert
        assertThat(isOcupada).isFalse(); // Cancelado conta como livre
    }

    // -------------------------------------------------------------------------
    // TESTES: findAllByStatusAndDataReservaBefore (Scheduler)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar ingressos expirados (antes da data limite)")
    public void findAllByStatusAndDataReservaBefore_whenFound() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Ingresso ANTIGO (criado há 30 min atrás) -> DEVE SER ENCONTRADO
        Ingresso ingressoExpirado = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("X1")
                .status(StatusIngresso.PENDENTE)
                .dataReserva(agora.minusMinutes(30))
                .build();
        ingressoRepository.save(ingressoExpirado);

        // Ingresso NOVO (criado há 5 min atrás) -> NÃO DEVE SER ENCONTRADO
        Ingresso ingressoRecente = Ingresso.builder()
                .sessao(sessaoPadrao)
                .usuario(usuarioPadrao)
                .codigoPoltrona("X2")
                .status(StatusIngresso.PENDENTE)
                .dataReserva(agora.minusMinutes(5))
                .build();
        ingressoRepository.save(ingressoRecente);

        // Act: Buscar pendentes criados antes de 15 minutos atrás
        LocalDateTime tempoLimite = agora.minusMinutes(15);
        List<Ingresso> expirados = ingressoRepository
                .findAllByStatusAndDataReservaBefore(StatusIngresso.PENDENTE, tempoLimite);

        // Assert
        assertThat(expirados).hasSize(1);
        assertThat(expirados.getFirst().getCodigoPoltrona()).isEqualTo("X1");
    }

    // -------------------------------------------------------------------------
    // TESTES: findAllByUsuarioId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar histórico de ingressos do usuário")
    public void findAllByUsuarioId_whenSuccesful() {
        // Act
        List<Ingresso> ingressos = ingressoRepository.findAllByUsuarioId(usuarioPadrao.getId());

        // Assert
        assertThat(ingressos).isNotEmpty();
        assertThat(ingressos.getFirst().getUsuario().getId()).isEqualTo(usuarioPadrao.getId());
        assertThat(ingressos.getFirst().getCodigoPoltrona()).isEqualTo("A1");
    }
}