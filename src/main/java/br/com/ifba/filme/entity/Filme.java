package br.com.ifba.filme.entity;

import br.com.ifba.infrastructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "filme")
public class Filme extends PersistenceEntity {

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String trailerYoutube;

    private BigDecimal preco;

    private Boolean meiaEntrada;

    /**
     * URL pública/assinada para a imagem (recomendado: Supabase Storage)
    **/
    @Column(name = "imagem_url", columnDefinition = "TEXT")
    private String imagemUrl;

    // controle lógico
    private Boolean ativo = true;
}

