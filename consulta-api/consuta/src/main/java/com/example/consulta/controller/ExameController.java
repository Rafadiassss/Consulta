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
        List<ExameVO> listaVO = exameService.listarTodos();
        List<ExameResponseDTO> listaDTO = listaVO.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> buscarPorId(@PathVariable Long id) {
        return exameService.buscarPorId(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExameResponseDTO> salvar(@RequestBody @Valid ExameRequestDTO dto) {
        ExameVO exameSalvoVO = exameService.salvar(dto);
        ExameResponseDTO responseDTO = toResponseDTO(exameSalvoVO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ExameRequestDTO dto) {
        return exameService.atualizar(id, dto)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (exameService.deletar(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    private ExameResponseDTO toResponseDTO(ExameVO vo) {
        return new ExameResponseDTO(
                vo.id(),
                vo.nome(),
                vo.resultado(),
                vo.observacoes(),
                vo.consultaId());
    }
}