package br.com.ifba.reserva.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCadastroDTO {

    @NotNull(message = "Id do Usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "Id da Sessão é obrigatório")
    private Long sessaoId;

    @NotNull(message = "A lista de poltronas é obrigatória.")
    @NotEmpty(message = "A lista de poltronas não pode estar vazia.")
    private List<Long> poltronasIds; // Lista de IDs das Poltronas
}