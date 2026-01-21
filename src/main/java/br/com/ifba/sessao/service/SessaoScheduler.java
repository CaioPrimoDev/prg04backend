package br.com.ifba.sessao.service;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.filme.repository.FilmeRepository;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.sessao.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoScheduler {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;

    /**
     * TAREFA 1: Desativar sessões após o término do filme.
     * Executa a cada 1 minuto (60000 ms).
     */
    @Scheduled(fixedRate = 60000)
    public void desativarSessoesEncerradas() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime agora = LocalDateTime.now();

        // Busca apenas sessões ativas de hoje ou antes (ignora as de amanhã/futuro)
        List<Sessao> sessoesAtivas = sessaoRepository.findAllByAtivoTrueAndDataLessThanEqual(hoje);

        for (Sessao sessao : sessoesAtivas) {
            try {
                // Converte a duração (String ou Integer) para long
                long duracaoMinutos = Long.parseLong(sessao.getFilme().getDuracao());

                // Calcula o fim do filme: Data + Horario + Duracao
                LocalDateTime inicioSessao = LocalDateTime.of(sessao.getData(), sessao.getHorario());
                LocalDateTime fimSessao = inicioSessao.plusMinutes(duracaoMinutos);

                // Se Agora > Fim da Sessão -> Desativa
                if (agora.isAfter(fimSessao)) {
                    sessao.setAtivo(false);
                    sessaoRepository.save(sessao);
                    System.out.println("Sessão encerrada e desativada: ID " + sessao.getId());
                }

            } catch (Exception e) {
                System.err.println("Erro ao processar sessão ID " + sessao.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * TAREFA 2: Excluir sessões inativas antigas.
     * Cron: Segundo Minuto Hora Dia Mês DiaDaSemana
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void limparSessoesAntigas() {
        // Define o limite: Excluir tudo que for inativo e mais velho que 7 dias
        int diasParaManter = 7;
        LocalDate dataLimite = LocalDate.now().minusDays(diasParaManter);

        System.out.println("Iniciando limpeza de sessões anteriores a " + dataLimite);

        sessaoRepository.deleteByAtivoFalseAndDataBefore(dataLimite);

        System.out.println("Limpeza concluída.");
    }

    // Roda todos os dias à meia-noite (cron: segundo minuto hora dia mes dia_sem)
    @Scheduled(cron = "0 0 0 * * *")
    public void atualizarStatusFilmesVencidos() {
        System.out.println("--- ROBÔ: Verificando filmes vencidos... ---");

        // Pega todos os filmes que estão marcadoss como ATIVOS
        List<Filme> filmesAtivos = filmeRepository.findByAtivoTrue();

        for (Filme filme : filmesAtivos) {
            boolean temSessao = sessaoRepository
                    .existsByFilmeIdAndDataGreaterThanEqual(filme.getId(), LocalDate.now());

            if (!temSessao) {
                filme.setAtivo(false);
                filmeRepository.save(filme);
                System.out.println("Filme desativado por expiração: " + filme.getTitulo());
            }
        }
    }
}