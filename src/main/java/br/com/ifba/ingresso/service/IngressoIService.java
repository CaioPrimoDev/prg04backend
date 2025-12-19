package br.com.ifba.ingresso.service;

import br.com.ifba.ingresso.dto.IngressoRequestDTO;
import br.com.ifba.ingresso.entity.Ingresso;

import java.util.List;
import java.util.Optional;

public interface IngressoIService {

    // Core da venda
    List<Ingresso> reservar(IngressoRequestDTO dto);
    Ingresso confirmarPagamento(Long id);
    List<String> findPoltronasOcupadas(Long sessaoId);

    // Métodos de gerenciamento (que faltavam)
    Optional<Ingresso> findById(Long id);
    List<Ingresso> findByUsuarioId(Long usuarioId);
    void cancelar(Long id);

    void limparIngressosUsados();
    void limparIngressosCancelados();
}
