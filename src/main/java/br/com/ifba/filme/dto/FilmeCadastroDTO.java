package br.com.ifba.filme.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmeCadastroDTO {
    @NotNull
    @Size(max = 100)
    private String titulo;

    @Size(max = 2000)
    private String descricao;

    private String trailerYoutube;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal preco;

    private Boolean meiaEntrada;

    private String imagemUrl;
}
