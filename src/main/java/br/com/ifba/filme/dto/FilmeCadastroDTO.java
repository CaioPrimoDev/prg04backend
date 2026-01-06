package br.com.ifba.filme.dto;

import br.com.ifba.filme.entity.Classificacao;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmeCadastroDTO {
    @NotBlank(message = "O Título é obrigatório")
    @Size(max = 100)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000)
    private String descricao;

    @URL(message = "O formato da URL é inválido. Certifique-se de incluir 'http://' ou 'https://'.")
    private String trailerYoutube;

    @NotBlank(message = "A duração é obrigatória")
    private String duracao;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal preco;

    @NotBlank(message = "O gênero é obrigatório")
    private String genero;

    @NotNull(message = "A classificação indicativa é obrigatória")
    private Classificacao classificacao;

    @NotNull(message = "O campo 'meiaEntrada' é obrigatório.")
    @JsonProperty("meia_entrada")
    private Boolean meiaEntrada;
}
