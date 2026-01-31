package br.com.ifba.ingresso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngressoResponseDTO {
    private Long id;
    private String filmeTitulo;
    private LocalDateTime dataHora;
    private String poltrona; // Ex: A1
    private String status;   // APROVADO, PENDENTE
    private Long pedidoId;   // Para usar no botão de enviar email
}
