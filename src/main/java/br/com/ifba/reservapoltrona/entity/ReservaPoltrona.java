package br.com.ifba.reservapoltrona.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.poltrona.entity.Poltrona;
import br.com.ifba.reserva.entity.Reserva;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "reserva_poltrona")
public class ReservaPoltrona extends PersistenceEntity {

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "poltrona_id")
    private Poltrona poltrona;

    private LocalDateTime criadoEm;
}

