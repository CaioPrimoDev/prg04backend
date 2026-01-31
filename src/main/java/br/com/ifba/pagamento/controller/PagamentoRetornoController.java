package br.com.ifba.pagamento.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class PagamentoRetornoController {

    @Value("${APP_FRONTEND_URL}")
    private String frontendUrl;

    @GetMapping("/sucesso")
    public void pagamentoSucesso(@RequestParam Map<String, String> allParams, HttpServletResponse response) throws IOException {
        // Aqui você pode salvar o status no banco antes de redirecionar
        String paymentId = allParams.get("payment_id");
        System.out.println("Redirecionando para o front. ID: " + paymentId);

        // Redireciona para a rota /sucesso do React passando o ID na URL
        response.sendRedirect(frontendUrl + "/sucesso?payment_id=" + paymentId);
    }

    @GetMapping("/pendente")
    public void pagamentoPendente(HttpServletResponse response) throws IOException {
        response.sendRedirect(frontendUrl + "/pendente");
    }

    @GetMapping("/falha")
    public void pagamentoFalha(HttpServletResponse response) throws IOException {
        response.sendRedirect(frontendUrl + "/falha");
    }
}
