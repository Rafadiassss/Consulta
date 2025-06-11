package com.example.consulta.controller;

import com.example.consulta.dto.AgendaRequestDTO;
import com.example.consulta.dto.AgendaResponseDTO;
import com.example.consulta.service.AgendaService;
import com.example.consulta.vo.AgendaVO;
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
@RequestMapping("/agendas")
@Tag(name = "Agenda", description = "Operações para gerenciar agendas")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendaResponseDTO>> listar() {
        // Busca a lista de VOs (Value Objects) do serviço.
        List<AgendaVO> listaVO = agendaService.listarTodas();
        // Converte a lista de VOs para uma lista de DTOs de resposta.
        List<AgendaResponseDTO> listaDTO = listaVO.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        // Retorna a resposta HTTP 200 OK com a lista de DTOs no corpo.
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> buscarPorId(@PathVariable Long id) {
        // Busca a agenda pelo ID no serviço, que retorna um Optional de VO.
        return agendaService.buscarPorId(id)
                // Se um VO for encontrado, converte-o para DTO de resposta.
                .map(this::toResponseDTO)
                // Em seguida, envolve o DTO em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se o Optional do serviço estiver vazio, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AgendaResponseDTO> salvar(@RequestBody @Valid AgendaRequestDTO dto) {
        // Envia o DTO de requisição para o serviço e recebe o VO da agenda salva.
        AgendaVO agendaSalvaVO = agendaService.salvar(dto);
        // Converte o VO retornado pelo serviço em um DTO de resposta.
        AgendaResponseDTO responseDTO = toResponseDTO(agendaSalvaVO);

        // Constrói a URI para o novo recurso, que será incluída no header 'Location'.
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();

        // Retorna a resposta HTTP 201 Created, com a URI e o DTO no corpo.
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> atualizar(@PathVariable Long id,
            @RequestBody @Valid AgendaRequestDTO dto) {
        // Chama o serviço de atualização, passando o ID e o DTO com os novos dados.
        return agendaService.atualizar(id, dto)
                // Se o serviço encontrar e atualizar, converte o VO retornado para DTO.
                .map(this::toResponseDTO)
                // Envolve o DTO em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se o serviço não encontrar a agenda, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar, que retorna 'true' se a exclusão foi
        // bem-sucedida.
        if (agendaService.deletar(id)) {
            // Retorna o status 204 No Content, o padrão para exclusão com sucesso.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o serviço indicou que o recurso não foi encontrado.
            return ResponseEntity.notFound().build();
        }
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    // Converte um 'AgendaVO' (vindo do serviço) para um 'AgendaResponseDTO'
    // (enviado ao cliente).
    private AgendaResponseDTO toResponseDTO(AgendaVO vo) {
        return new AgendaResponseDTO(
                vo.id(),
                vo.dataAgendada(),
                vo.horarios());
    }
}
