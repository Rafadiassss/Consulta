package com.example.consulta.controller;

import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.hateoas.PacienteModelAssembler;
import com.example.consulta.service.PacienteService;
import com.example.consulta.vo.PacienteVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/pacientes")
@Tag(name = "Paciente", description = "Operações para gerenciar pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PacienteModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PacienteVO>>> listar() {
        // Busca a lista de VOs do serviço.
        List<PacienteVO> pacientesVO = pacienteService.listarTodos();
        // Converte cada VO em um EntityModel com links HATEOAS.
        List<EntityModel<PacienteVO>> pacientesModel = pacientesVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria o modelo de coleção com um link para a própria lista.
        CollectionModel<EntityModel<PacienteVO>> collectionModel = CollectionModel.of(pacientesModel,
                linkTo(methodOn(PacienteController.class).listar()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PacienteVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return pacienteService.buscarPorId(id)
                // Se encontrado, converte para EntityModel.
                .map(assembler::toModel)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<PacienteVO>> salvar(@RequestBody @Valid PacienteRequestDTO dto) {
        // Envia o DTO para o serviço e recebe o VO salvo.
        PacienteVO pacienteSalvoVO = pacienteService.salvar(dto);
        // Converte o VO para um EntityModel.
        EntityModel<PacienteVO> pacienteModel = assembler.toModel(pacienteSalvoVO);
        // Retorna 201 Created com a URI do novo recurso.
        return ResponseEntity
                .created(pacienteModel.getRequiredLink("self").toUri())
                .body(pacienteModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PacienteVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid PacienteRequestDTO dto) {
        // Chama o serviço de atualização.
        return pacienteService.atualizar(id, dto)
                // Se bem-sucedido, converte o VO para EntityModel.
                .map(assembler::toModel)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar.
        if (pacienteService.deletar(id)) {
            // Se o serviço retornar true, envia 204 No Content.
            return ResponseEntity.noContent().build();
        } else {
            // Se retornar false, envia 404 Not Found.
            return ResponseEntity.notFound().build();
        }
    }
}