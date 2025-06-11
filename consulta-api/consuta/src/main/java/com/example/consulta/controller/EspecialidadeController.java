package com.example.consulta.controller;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.dto.EspecialidadeResponseDTO;
import com.example.consulta.service.EspecialidadeService;
import com.example.consulta.vo.EspecialidadeVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/especialidades")
@Tag(name = "Especialidade", description = "Operações para gerenciar especialidades")
public class EspecialidadeController {

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> listar() {
        // Chama o serviço para buscar todas as especialidades.

        List<EspecialidadeVO> listaVO = especialidadeService.listarTodas();
        List<EspecialidadeResponseDTO> listaDTO = listaVO.stream().map(this::toResponseDTO)
                .collect(Collectors.toList());
        // Retorna a resposta OK com a lista de DTOs
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        return especialidadeService.buscarPorId(id) // Recebe Optional<EspecialidadeVO>
                .map(this::toResponseDTO) // Converte o VO para DTO de resposta
                .map(ResponseEntity::ok) // Cria a resposta OK
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EspecialidadeVO> salvar(@RequestBody @Valid EspecialidadeRequestDTO dto) {
        // Chama o serviço para salvar a nova especialidade, passando o VO recebido.
        EspecialidadeVO especialidadeSalva = especialidadeService.salvar(dto);

        // Constrói a URI para o novo recurso criado, que será retornada no header
        // 'Location'.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(especialidadeSalva.id()).toUri();

        // Retorna a resposta HTTP 201 Created, com a URI no header e o objeto salvo no
        // corpo.
        return ResponseEntity.created(uri).body(especialidadeSalva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar. O serviço retorna 'true' se a exclusão foi
        // bem-sucedida.
        if (especialidadeService.deletar(id)) {
            // Se o serviço retornou 'true', envia uma resposta 204 No Content (sucesso sem
            // corpo).
            return ResponseEntity.noContent().build();
        } else {
            // Se o serviço retornou 'false' (não encontrou o ID), envia uma resposta 404
            // Not Found.
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    // Converte VO para DTO de resposta
    private EspecialidadeResponseDTO toResponseDTO(EspecialidadeVO vo) {
        return new EspecialidadeResponseDTO(
                vo.id(),
                vo.nome(),
                vo.descricao());
    }
}