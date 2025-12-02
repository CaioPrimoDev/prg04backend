package br.com.ifba.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCadastroDTO {
    // Chaves estrangeiras da Reserva
    private Long usuarioId;
    private Long sessaoId;

    // Chaves estrangeiras para o relacionamento Poltrona
    private List<Long> poltronasIds; // Lista de IDs das Poltronas
}