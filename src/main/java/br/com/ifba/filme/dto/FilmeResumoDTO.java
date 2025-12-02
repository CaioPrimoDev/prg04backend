package br.com.ifba.filme.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// FilmeResumoDTO: DTO para uso em listagens ou como campo aninhado
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmeResumoDTO {
    private Long id;
    private String titulo;
    private BigDecimal preco; // Essencial para a exibição de preço da sessão
    private Boolean meiaEntrada; // Essencial para a regra de preço
    private String imagemUrl; // Para exibição na interface
}
