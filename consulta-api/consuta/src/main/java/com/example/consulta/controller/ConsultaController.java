package com.example.consulta.controller;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.dto.ConsultaResponseDTO;
import com.example.consulta.dto.MedicoDTO;
import com.example.consulta.dto.PacienteDTO;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodas() {
        List<ConsultaVO> listaVO = consultaService.listarTodas();
        List<ConsultaResponseDTO> listaDTO = listaVO.stream().map(this::toResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(listaDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Long id) {
        return consultaService.buscarPorId(id)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> salvar(@RequestBody @Valid ConsultaRequestDTO dto) {
        ConsultaVO consultaSalvaVO = consultaService.salvar(dto);
        ConsultaResponseDTO responseDTO = toResponseDTO(consultaSalvaVO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(responseDTO.id()).toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> atualizar(@PathVariable Long id,
            @RequestBody @Valid ConsultaRequestDTO dto) {
        return consultaService.atualizar(id, dto)
                .map(this::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        consultaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- MÉTODO DE MAPEAMENTO DO CONTROLLER ---

    private ConsultaResponseDTO toResponseDTO(ConsultaVO vo) {
        PacienteDTO pacienteDTO = new PacienteDTO(
                vo.paciente().getId(),
                vo.paciente().getNome());
        MedicoDTO medicoDTO = new MedicoDTO(
                vo.medico().getId(),
                vo.medico().getNome(),
                vo.medico().getEspecialidade() // Supondo que Medico tenha getEspecialidade()
        );
        Long pagamentoId = (vo.pagamento() != null) ? vo.pagamento().getId() : null;

        return new ConsultaResponseDTO(
                vo.id(),
                vo.data(),
                vo.status(),
                vo.nomeConsulta(),
                pacienteDTO,
                medicoDTO,
                pagamentoId);
    }
}