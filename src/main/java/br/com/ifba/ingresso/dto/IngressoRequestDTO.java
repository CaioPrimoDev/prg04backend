package br.com.ifba.ingresso.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngressoRequestDTO {

    @NotNull(message = "O ID da sessão é obrigatório")
    private Long sessaoId;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    @NotEmpty(message = "Selecione pelo menos uma poltrona")
    private List<String> poltronas; // Ex: ["A1"] ou ["A1", "A2", "B5"]
}