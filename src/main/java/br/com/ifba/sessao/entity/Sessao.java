package br.com.ifba.sessao.entity;

import br.com.ifba.filme.entity.Filme;
import br.com.ifba.sala.entity.Sala;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sessao")
public class Sessao extends Persistence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filme_id")
    private Filme filme;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private Sala sala;

    private LocalDate data;

    private LocalTime horario;

    // controle lógico
    private Boolean ativo = true;
}
