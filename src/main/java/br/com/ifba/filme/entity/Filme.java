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

    // Sugestão: Idealmente mudar para Integer no futuro, mas mantive String conforme seu código
    private String duracao;

    private BigDecimal preco;

    @Column(name = "meia_entrada")
    private Boolean meiaEntrada;

    // --- NOVOS CAMPOS ---
    private String genero; // Ex: "Ação", "Comédia"

    @Enumerated(EnumType.STRING) // Salva como texto no banco: "LIVRE", "DEZ", etc.
    private Classificacao classificacao;
    // --------------------

    /**
     * URL pública/assinada para a imagem (Supabase Storage)
     **/
    @Column(name = "imagem_url", columnDefinition = "TEXT")
    private String imagemUrl;

    // controle lógico
    @Builder.Default // Garante que o builder use o valor padrão
    private Boolean ativo = true;
}