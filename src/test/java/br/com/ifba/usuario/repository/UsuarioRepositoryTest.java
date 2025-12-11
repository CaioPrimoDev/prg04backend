package br.com.ifba.usuario.repository;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import br.com.ifba.usuario.entity.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

// Import Padronizado do AssertJ
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Teste para o repositorio de Usuario")
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Define os dados padrão que serão usados em todos os testes
    private static final String CPF_PADRAO = "36932378086";
    private static final String EMAIL_PADRAO = "email@email.com";
    private static final String CPF_INEXISTENTE = "00000000000";
    private static final String EMAIL_INEXISTENTE = "naoexiste@email.com";


    @BeforeEach // Garante que o usuário seja salvo ANTES de cada teste
    public void setUp() {
        // Garantindo que o banco esteja limpo antes de salvar (feito por @DataJpaTest)
        // E salvando o usuário padrão para os testes de sucesso
        Usuario usuario = Usuario.builder()
                .email(EMAIL_PADRAO)
                .cpf(CPF_PADRAO)
                .senha("123456")
                .status(true)
                .build();
        usuarioRepository.save(usuario);
    }

    // -------------------------------------------------------------------------
    // TESTES DE SUCESSO (Caminho Feliz)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve retornar usuário quando buscar por CPF existente")
    public void findByCpf_whenSuccesful() {
        Optional<Usuario> usuarioFound = usuarioRepository.findByCpf(CPF_PADRAO);

        // Uso das boas práticas do AssertJ: isPresent() e hasValueSatisfying()
        assertThat(usuarioFound)
                .isPresent()
                .hasValueSatisfying(usuario -> {
                    assertThat(usuario.getCpf()).isEqualTo(CPF_PADRAO);
                });
    }

    @Test
    @DisplayName("Deve retornar usuário quando buscar por email existente")
    public void findByEmail_whenSuccesful() {
        Optional<Usuario> usuarioFound = usuarioRepository.findByEmail(EMAIL_PADRAO);

        // Código corrigido para usar a forma segura do AssertJ
        assertThat(usuarioFound)
                .isPresent()
                .hasValueSatisfying(usuario -> {
                    assertThat(usuario.getEmail()).isEqualTo(EMAIL_PADRAO);
                });
    }

    @Test
    @DisplayName("Deve retornar usuário quando buscar por cpf e email existentes")
    public void findByCpfAndEmail_whenSuccesful() {
        Optional<Usuario> usuarioFound = usuarioRepository
                .findByCpfAndEmail(CPF_PADRAO, EMAIL_PADRAO);

        // Código corrigido para usar a forma segura do AssertJ
        assertThat(usuarioFound)
                .isPresent()
                .hasValueSatisfying(usuario -> {
                    assertThat(usuario.getCpf()).isEqualTo(CPF_PADRAO);
                    assertThat(usuario.getEmail()).isEqualTo(EMAIL_PADRAO);
                });
    }

    // -------------------------------------------------------------------------
    // TESTES DE FALHA (Caminho Infeliz)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Não deve retornar usuário quando buscar por cpf e email INEXISTENTES")
    public void findByCpfAndEmail_whenUsuarioNotFound() {
        // Tenta buscar usando um CPF e um Email que NUNCA foram salvos
        Optional<Usuario> usuarioFound = usuarioRepository
                .findByCpfAndEmail(CPF_INEXISTENTE, EMAIL_INEXISTENTE);

        // Verifica que o Optional está VAZIO
        assertThat(usuarioFound).isNotPresent();
    }

    @Test
    @DisplayName("Não deve retornar usuário quando buscar por email INEXISTENTE")
    public void findByEmail_whenUsuarioNotFound() {
        // Tenta buscar um email que NUNCA foi salvo
        Optional<Usuario> usuarioFound = usuarioRepository.findByEmail(EMAIL_INEXISTENTE);

        // Verifica que o Optional está VAZIO
        assertThat(usuarioFound).isNotPresent();
    }

    @Test
    @DisplayName("Não deve retornar usuário quando buscar por CPF INEXISTENTE")
    public void findByCpf_whenUsuarioNotFound() {
        // Tenta buscar um CPF que NUNCA foi salvo
        Optional<Usuario> usuarioFound = usuarioRepository.findByCpf(CPF_INEXISTENTE);

        // Verifica que o Optional está VAZIO
        assertThat(usuarioFound).isNotPresent();
    }
}