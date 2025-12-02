package br.com.ifba.filme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmeResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String trailerYoutube;
    private BigDecimal preco;
    private Boolean meiaEntrada;
    private String imagemUrl;
}
