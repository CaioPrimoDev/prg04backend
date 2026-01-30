package br.com.ifba.ingresso.dto;

import br.com.ifba.ingresso.entity.TipoIngresso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IngressoRequestDTO {

    @NotNull(message = "O ID da sessão é obrigatório")
    private Long sessaoId;

    @NotBlank(message = "A poltrona é obrigatória")
    private String codigoPoltrona; // Ex: "A1" (Singular)

    @NotNull(message = "Informe se é MEIA ou INTEIRA")
    private TipoIngresso tipo; // Enum: MEIA ou INTEIRA
}