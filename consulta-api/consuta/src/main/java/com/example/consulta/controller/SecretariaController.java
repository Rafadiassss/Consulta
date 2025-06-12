package com.example.consulta.controller;

import com.example.consulta.dto.SecretariaRequestDTO;
import com.example.consulta.dto.SecretariaResponseDTO;
import com.example.consulta.service.SecretariaService;
import com.example.consulta.vo.SecretariaVO;
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
@RequestMapping("/secretarias")
@Tag(name = "Secretaria", description = "Operações para gerenciar secretarias")
public class SecretariaController {

    @Autowired
    private SecretariaService secretariaService;

    @GetMapping
    public ResponseEntity<List<SecretariaResponseDTO>> listar() {
        // Busca a lista de VOs do serviço.
        List<SecretariaVO> secretariasVO = secretariaService.listarTodas();
        // Converte cada VO para um DTO de Resposta.
        List<SecretariaResponseDTO> secretariasDTO = secretariasVO.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        // Retorna 200 OK com a lista de DTOs.
        return ResponseEntity.ok(secretariasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretariaResponseDTO> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return secretariaService.buscarPorId(id)
                // Se encontrado, converte para DTO de Resposta.
                .map(this::toResponseDTO)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SecretariaResponseDTO> salvar(@RequestBody @Valid SecretariaRequestDTO dto) {
        // Envia o DTO para o serviço e recebe o VO salvo.
        SecretariaVO secretariaSalvaVO = secretariaService.salvar(dto);
        // Converte o VO para o DTO de Resposta.
        SecretariaResponseDTO responseDTO = toResponseDTO(secretariaSalvaVO);
        // Constrói a URI para o novo recurso.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();
        // Retorna 201 Created com a URI e o DTO no corpo.
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecretariaResponseDTO> atualizar(@PathVariable Long id,
            @RequestBody @Valid SecretariaRequestDTO dto) {
        // Chama o serviço de atualização.
        return secretariaService.atualizar(id, dto)
                // Se bem-sucedido, converte o VO para DTO.
                .map(this::toResponseDTO)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar.
        if (secretariaService.deletar(id)) {
            // Retorna 204 No Content para sucesso.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não existia.
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    // Converte um 'SecretariaVO' (vindo do serviço) para um 'SecretariaResponseDTO'
    // (enviado ao cliente).
    private SecretariaResponseDTO toResponseDTO(SecretariaVO vo) {
        return new SecretariaResponseDTO(
                vo.id(),
                vo.nome(),
                vo.cpf(),
                vo.telefone(),
                vo.email(),
                vo.usuario());
    }
}