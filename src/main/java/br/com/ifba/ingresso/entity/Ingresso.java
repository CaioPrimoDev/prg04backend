package br.com.ifba.ingresso.entity;

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
@Table(name = "ingresso",
        uniqueConstraints = {
                // Impede que a mesma poltrona seja vendida 2x na mesma sessão
                @UniqueConstraint(columnNames = {"sessao_id", "codigo_poltrona"})
        }
)
public class Ingresso extends PersistenceEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Substitui a entidade Poltrona e ReservaPoltrona
    @Column(nullable = false)
    private String codigoPoltrona; // "A10", "B5"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIngresso status;

    @Column(nullable = false)
    private LocalDateTime dataReserva;

    @Column(name = "preco_final")
    private BigDecimal precoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIngresso tipo;
}

