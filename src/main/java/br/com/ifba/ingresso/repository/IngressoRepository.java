package br.com.ifba.ingresso.repository;


import br.com.ifba.ingresso.entity.Ingresso;
import br.com.ifba.ingresso.entity.StatusIngresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    /**
     * MÉTODO PRINCIPAL PARA O MAPA DE ASSENTOS
     * Retorna apenas os CÓDIGOS (Strings) das poltronas que não podem ser compradas.
     * Ignora ingressos CANCELADOS.
     */
    @Query("SELECT i.codigoPoltrona FROM Ingresso i " +
            "WHERE i.sessao.id = :sessaoId " +
            "AND i.status IN ('PENDENTE', 'CONFIRMADO')")
    List<String> findPoltronasOcupadas(@Param("sessaoId") Long sessaoId);

    /**
     * VALIDAÇÃO DE SEGURANÇA
     * Verifica se uma poltrona específica já está ocupada antes de tentar salvar.
     * Útil para validação manual no Service antes de confiar apenas na constraint do banco.
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
            "FROM Ingresso i " +
            "WHERE i.sessao.id = :sessaoId " +
            "AND i.codigoPoltrona = :codigoPoltrona " +
            "AND i.status IN ('PENDENTE', 'CONFIRMADO')")
    boolean isPoltronaOcupada(@Param("sessaoId") Long sessaoId,
                              @Param("codigoPoltrona") String codigoPoltrona);

    /**
     * PARA O SCHEDULER (LIMPEZA AUTOMÁTICA)
     * Busca ingressos que estão PENDENTES há muito tempo (ex: criados a mais de 15 min)
     * para que possam ser deletados ou cancelados, liberando o lugar.
     */
    List<Ingresso> findAllByStatusAndDataReservaBefore(StatusIngresso status, LocalDateTime dataLimite);

    /**
     * PARA O DASHBOARD DO USUÁRIO
     * Lista o histórico de compras de um usuário.
     */
    List<Ingresso> findAllByUsuarioId(Long usuarioId);
    long deleteByStatus(StatusIngresso status);

    int deleteByStatusAndDataReservaBefore(StatusIngresso status, LocalDateTime dataLimite);
}
