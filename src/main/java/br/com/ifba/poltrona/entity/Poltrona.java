package br.com.ifba.poltrona.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import br.com.ifba.sessao.entity.Sessao;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "poltrona")
public class Poltrona extends PersistenceEntity {

    // poltrona específica para cada sessão
    @ManyToOne
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;

    private String codigo; // ex: "A10"

    private String letra;

    private Integer numero;

    private Boolean bloqueada = false;
}


