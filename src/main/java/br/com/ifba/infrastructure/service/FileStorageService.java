package br.com.ifba.infrastructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    public String uploadImagem(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("A imagem não pode estar vazia.");
        }

        try {
            // Gera nome único: "poster.jpg" -> "a1b2-c3d4-poster.jpg"
            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "imagem";
            // Remove espaços e caracteres estranhos do nome para evitar erro na URL
            String cleanName = originalName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = UUID.randomUUID() + "-" + cleanName;

            // Monta URL da API do Supabase
            String uploadEndpoint = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            // Prepara o Cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Configura a Requisição (Header com Token é vital)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadEndpoint))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("Content-Type", file.getContentType())
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro Supabase (" + response.statusCode() + "): " + response.body());
            }

            return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Falha ao conectar com Supabase", e);
        }
    }
}