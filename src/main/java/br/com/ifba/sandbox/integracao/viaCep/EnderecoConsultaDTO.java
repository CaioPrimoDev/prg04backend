package br.com.ifba.sandbox.integracao.viaCep;

import lombok.Data;

@Data
public class EnderecoConsultaDTO {
    // Getters e Setters
    private String cep;
    private String logradouro; // Rua
    private String bairro;
    private String localidade; // Cidade
    private String uf;

}
