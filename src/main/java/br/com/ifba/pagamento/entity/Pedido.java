package br.com.ifba.pagamento.entity;


import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pedidos")
@Data // Lombok para Getters e Setters
@NoArgsConstructor
@AllArgsConstructor
public class Pedido extends PersistenceEntity {

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "email_comprador")
    private String emailComprador;

    @Enumerated(EnumType.STRING) // Salva "APROVADO" no banco, não 0 ou 1
    private StatusPedido status;

    @Column(name = "mercado_pago_id")
    private String mercadoPagoId;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.status == null) this.status = StatusPedido.PENDENTE;
    }
}
