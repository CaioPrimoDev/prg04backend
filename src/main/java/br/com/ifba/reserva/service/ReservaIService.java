package br.com.ifba.reserva.service;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.reserva.entity.Reserva;

import java.util.List;

public interface ReservaIService {

    Reserva criarReserva(Reserva reserva, List<Poltrona> poltronas);
}
