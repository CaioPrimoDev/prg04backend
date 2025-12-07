package br.com.ifba.ingresso.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.sessao.entity.Sessao;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ingresso",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sessao_id", "codigo_poltrona"})
        } // Impede o registro da mesma poltrona na mesma sessão, VALIDAÇÃO MÁXIMA
)
public class Ingresso extends PersistenceEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;

    @ManyToOne
    @JoinColumn(name = "usuario_id") // Opcional se a compra for anônima
    private Usuario usuario;

    // AQUI ESTÁ A MÁGICA
    // guarda apenas a String que dá match com o ID da sua DIV no front
    @Column(name = "codigo_poltrona", nullable = false)
    private String codigoPoltrona; // Ex: "A10"

    @Enumerated(EnumType.STRING)
    private StatusIngresso status; // PENDENTE (reserva), PAGO

    private LocalDateTime dataReserva; // Para limpar reservas expiradas
}

enum StatusIngresso {
    BLOQUEADO_TEMPORARIO, // O usuário clicou (segura por 10 min)
    CONFIRMADO            // Pagou
}