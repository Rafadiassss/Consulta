package com.example.consulta.controller;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.dto.EspecialidadeResponseDTO;
import com.example.consulta.service.EspecialidadeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/especialidades")
@Tag(name = "Especialidade", description = "Operações para gerenciar especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> listar() {
        // Chama o serviço para buscar todas as especialidades.
        List<EspecialidadeResponseDTO> listaDeDTOs = especialidadeService.listarTodas();
        // Retorna a lista dentro de uma resposta HTTP com status 200 OK.
        return ResponseEntity.ok(listaDeDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        // Chama o serviço para buscar uma especialidade pelo ID.
        return especialidadeService.buscarPorId(id)
                // Se o Optional retornado pelo serviço contiver um valor, o map é executado.
                // 'ResponseEntity::ok' cria uma resposta 200 OK com o DTO no corpo.
                .map(ResponseEntity::ok)
                // Se o Optional estiver vazio, 'orElse' é executado, criando uma resposta 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponseDTO> salvar(@RequestBody @Valid EspecialidadeRequestDTO dto) {
        // A anotação @Valid aciona as validações definidas no DTO (ex: @NotBlank).
        // Chama o serviço para salvar a nova especialidade, passando o DTO recebido.
        EspecialidadeResponseDTO especialidadeSalva = especialidadeService.salvar(dto);

        // Constrói a URI para o novo recurso criado, que será retornada no header 'Location'.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(especialidadeSalva.id()).toUri();

        // Retorna a resposta HTTP 201 Created, com a URI no header e o objeto salvo no corpo.
        return ResponseEntity.created(uri).body(especialidadeSalva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar. O serviço retorna 'true' se a exclusão foi bem-sucedida.
        if (especialidadeService.deletar(id)) {
            // Se o serviço retornou 'true', envia uma resposta 204 No Content (sucesso sem corpo).
            return ResponseEntity.noContent().build();
        } else {
            // Se o serviço retornou 'false' (não encontrou o ID), envia uma resposta 404 Not Found.
            return ResponseEntity.notFound().build();
        }
    }
}