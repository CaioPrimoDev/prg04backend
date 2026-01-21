package br.com.ifba.sessao.repository;

import br.com.ifba.sessao.entity.Sessao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByData(LocalDate data);
    List<Sessao> findByFilmeIdAndData(Long filmeId, LocalDate data);
    List<Sessao> findByFilmeId(Long filmeId);
    List<Sessao> findBySalaId(Long salaId);
    List<Sessao> findByAtivoTrue();
    List<Sessao> findByAtivoFalse();

    // Busca sessões ativas que são de hoje ou datas passadas (não precisa checar o futuro)
    List<Sessao> findAllByAtivoTrueAndDataLessThanEqual(LocalDate data);

    // Busca sessões inativas antigas para deletar (ex: antes de uma data X)
    // @Modifying para permitir delete/update customizado
    @Modifying
    @Transactional
    @Query("DELETE FROM Sessao s WHERE s.ativo = false AND s.data <= :dataLimite")
    void deleteByAtivoFalseAndDataBefore(@Param("dataLimite") LocalDate dataLimite);

    // Verifica se existe alguma sessão do filme com data HOJE ou FUTURA
    boolean existsByFilmeIdAndDataGreaterThanEqual(Long filmeId, LocalDate data);
}

