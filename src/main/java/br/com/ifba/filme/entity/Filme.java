package br.com.ifba.filme.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "filme")
public class Filme extends PersistenceEntity {

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "trailer_youtube")
    private String trailerYoutube;

    private String duracao;

    private BigDecimal preco;

    @Column(name = "meia_entrada")
    private Boolean meiaEntrada;

    /**
     * URL pública/assinada para a imagem (Supabase Storage)
    **/
    @Column(name = "imagem_url", columnDefinition = "TEXT")
    private String imagemUrl;

    // controle lógico
    private Boolean ativo = true;
}

