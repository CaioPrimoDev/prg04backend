package br.com.ifba.sandbox.integracao.viaCep;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consulta-cep") // Caminho separado dos Usuarios
@RequiredArgsConstructor
public class ConsultaCepController {

    private final ConsultaCepService consultaCepService;

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoConsultaDTO> consultar(@PathVariable String cep) {

        // Chama o serviço (que agora usa RestTemplate)
        EnderecoConsultaDTO resultado = consultaCepService.consultarCep(cep);

        // Validação: se voltou nulo ou CEP vazio, retorna 404 (Not Found)
        if (resultado == null || resultado.getCep() == null) {
            return ResponseEntity.notFound().build();
        }

        // Retorna 200 OK com os dados
        return ResponseEntity.ok(resultado);
    }
}
