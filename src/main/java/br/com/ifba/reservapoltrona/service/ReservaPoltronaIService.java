package br.com.ifba.reservapoltrona.service;

import br.com.ifba.reservapoltrona.entity.ReservaPoltrona;

import java.util.List;

public interface ReservaPoltronaIService {

    ReservaPoltrona save(ReservaPoltrona rp);
    List<ReservaPoltrona> findByReservaId(Long reservaId);
    void deleteById(Long id);
}
