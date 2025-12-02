package br.com.ifba.reservapoltrona.dto;

import br.com.ifba.poltrona.dto.PoltronaResumoDTO;
import br.com.ifba.reserva.dto.ReservaResumoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaPoltronaResponseDTO {
    private Long id;

    private ReservaResumoDTO reserva;
    private PoltronaResumoDTO poltrona;

    private LocalDateTime criadoEm;
}
