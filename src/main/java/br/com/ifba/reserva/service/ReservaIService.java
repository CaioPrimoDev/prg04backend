package br.com.ifba.reserva.service;

import br.com.ifba.reserva.dto.ReservaCadastroDTO;
import br.com.ifba.reserva.entity.Reserva;

import java.util.List;

public interface ReservaIService {

    Reserva criarReserva(ReservaCadastroDTO dto);
    List<Reserva> findByUsuarioId(Long usuarioId);
}
