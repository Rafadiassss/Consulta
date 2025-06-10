package com.example.consulta.controller;

import com.example.consulta.dto.ExameRequestDTO;
import com.example.consulta.dto.ExameResponseDTO;
import com.example.consulta.service.ExameService;
import com.example.consulta.vo.ExameVO;
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
@RequestMapping("/exames")
@Tag(name = "Exame", description = "Operações para gerenciar exames")
public class ExameController {

    @Autowired
    private ExameService exameService;

    @GetMapping
    public ResponseEntity<List<ExameResponseDTO>> listar() {
        // Busca a lista de VOs do serviço.
        List<ExameVO> listaVO = exameService.listarTodos();
        // Converte a lista de VOs para uma lista de DTOs de resposta.
        List<ExameResponseDTO> listaDTO = listaVO.stream().map(this::toResponseDTO).collect(Collectors.toList());
        // Retorna a resposta HTTP 200 OK com a lista de DTOs.
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> buscarPorId(@PathVariable Long id) {
        // Busca o exame pelo ID no serviço, que retorna um Optional de VO.
        return exameService.buscarPorId(id)
                // Se um VO for encontrado, converte para DTO de resposta.
                .map(this::toResponseDTO)
                // Envolve o DTO em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se o Optional estiver vazio, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExameResponseDTO> salvar(@RequestBody @Valid ExameRequestDTO dto) {
        // Envia o DTO de requisição para o serviço e recebe o VO do exame salvo.
        ExameVO exameSalvoVO = exameService.salvar(dto);
        // Converte o VO em DTO de resposta.
        ExameResponseDTO responseDTO = toResponseDTO(exameSalvoVO);
        // Constrói a URI para o novo recurso.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();
        // Retorna a resposta 201 Created com a URI e o DTO no corpo.
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ExameRequestDTO dto) {
        // Chama o serviço de atualização com o ID e o DTO.
        return exameService.atualizar(id, dto)
                // Se o serviço encontrar e atualizar, converte o VO retornado para DTO.
                .map(this::toResponseDTO)
                // Envolve o DTO em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se o serviço não encontrar o exame, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar, que retorna 'true' se a exclusão foi
        // bem-sucedida.
        if (exameService.deletar(id)) {
            // Retorna 204 No Content para indicar sucesso na exclusão.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não foi encontrado para deletar.
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    // Converte um 'ExameVO' (vindo do serviço) para um 'ExameResponseDTO' (enviado
    // ao cliente).
    private ExameResponseDTO toResponseDTO(ExameVO vo) {
        return new ExameResponseDTO(
                vo.id(),
                vo.nome(),
                vo.resultado(),
                vo.observacoes(),
                vo.consultaId());
    }
}