package br.com.ifba.usuario.controller;

import br.com.ifba.infrastructure.mapper.ObjectMapperUtill;
import br.com.ifba.usuario.dto.UsuarioCadastroDTO;
import br.com.ifba.usuario.dto.UsuarioListagemDTO;
import br.com.ifba.usuario.dto.UsuarioResponseDTO;
import br.com.ifba.usuario.entity.Usuario;
import br.com.ifba.usuario.service.UsuarioIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioIService usuarioService;
    private final ObjectMapperUtill mapper;

    // POST - Criar  usuário
    @PostMapping(path = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioCadastroDTO dto) {

        // 1. Mapeia DTO de Entrada para a Entidade
        Usuario entityToSave = mapper.map(dto, Usuario.class);

        // 2. Salva a Entidade no Service (Service deve cuidar de adicionar data/status)
        Usuario criado = usuarioService.save(entityToSave);

        // 3. Mapeia a Entidade Criada para o DTO de Saída
        UsuarioResponseDTO responseDto = mapper.map(criado, UsuarioResponseDTO.class);

        // 4. Retorna a Resposta Segura
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto); // ⬅️ Retorna o DTO, sem a senha
    }

    // GET não usa consume, mas sim produces
    @GetMapping(path = "/find-by-email/{email}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> findByEmail(@PathVariable("email") String email) {

        Usuario usuario = usuarioService.findByEmail(email);

        if(usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioResponseDTO responseDto = mapper.map(usuario, UsuarioResponseDTO.class);

        return ResponseEntity.ok(responseDto);

    }

    @GetMapping(path = "/find-by-cpf/{cpf}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDTO> findByCpf(@PathVariable("cpf") String cpf) {
        Usuario usuario = usuarioService.findByCpf(cpf);

        if(usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioResponseDTO responseDto = mapper.map(usuario, UsuarioResponseDTO.class);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping(path = "/findall",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UsuarioListagemDTO>> findAll(@PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) { // Retorna List<DTO>

        Page<Usuario> paginaUsuarios = usuarioService.findAll(pageable);

        if(paginaUsuarios.isEmpty()) {
            // Retorna 200 OK com uma lista vazia, ou 204 No Content, que é melhor que 404.
            return ResponseEntity.ok(Page.empty());
        }

        //  Transforma a Lista de Entidades em Lista de DTOs usando Streams
        Page<UsuarioListagemDTO> paginaDtos = paginaUsuarios
                .map(usuario -> mapper.map(usuario, UsuarioListagemDTO.class));

        return ResponseEntity.ok(paginaDtos); // Retorna a lista de DTOs
    }

    // Ele não retorna ou consome, então não há necessidade de um DTO especifico
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}




