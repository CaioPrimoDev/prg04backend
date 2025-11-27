package br.com.ifba.dto;

import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.reserva.entity.Reserva;
import lombok.Data;
import java.util.List;

@Data
public class ReservaRequest {
    private Reserva reserva;
    private List<Poltrona> poltronas;
}

