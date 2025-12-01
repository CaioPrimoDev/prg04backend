package br.com.ifba.reserva.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserva")
public class Reserva extends PersistenceEntity {

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Sessao sessao;

    private String status; // TEMP, CONFIRMADA, CANCELADA

    private String token;

    private LocalDateTime expiracao;

    private BigDecimal total = BigDecimal.ZERO;
}

