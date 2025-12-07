package br.com.ifba.sandbox.integracao.viaCep;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsultaCepService {

    public EnderecoConsultaDTO consultarCep(String cep) {
        // 1. Limpeza: remove traços ou pontos (ex: 01001-000 -> 01001000)
        String cepFormatado = cep.replaceAll("\\D", "");

        // 2. Monta a URL da API externa
        String url = "https://viacep.com.br/ws/" + cepFormatado + "/json/";

        // 3. Instancia o cliente HTTP nativo do Spring
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 4. Faz o GET e converte o JSON direto para seu DTO
            return restTemplate.getForObject(url, EnderecoConsultaDTO.class);
        } catch (Exception e) {
            // Em caso de erro (ex: sem internet), loga e retorna null
            System.err.println("Erro ao consultar CEP: " + e.getMessage());
            return null;
        }
    }
}