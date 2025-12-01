package br.com.ifba.sala.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sala")
public class Sala extends PersistenceEntity {

    private String nome;

    @Column(nullable = false)
    private String descricao;
}

