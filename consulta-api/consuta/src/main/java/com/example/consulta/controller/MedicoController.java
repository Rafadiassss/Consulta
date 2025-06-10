package com.example.consulta.controller;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.hateoas.MedicoModelAssembler;
import com.example.consulta.vo.MedicoVO;
import com.example.consulta.service.MedicoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/medicos")
@Tag(name = "Medico", description = "Operações para gerenciar médicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    // Injeta o assembler.
    @Autowired
    private MedicoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MedicoVO>>> listar() {
        // Busca a lista de VOs do serviço.
        List<MedicoVO> medicosVO = medicoService.listarTodos();
        // Converte cada VO em um EntityModel com links HATEOAS.
        List<EntityModel<MedicoVO>> medicosModel = medicosVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria o modelo de coleção com um link para a própria lista.
        CollectionModel<EntityModel<MedicoVO>> collectionModel = CollectionModel.of(medicosModel,
                linkTo(methodOn(MedicoController.class).listar()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MedicoVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return medicoService.buscarPorId(id)
                // Se encontrado, converte para EntityModel.
                .map(assembler::toModel)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<MedicoVO>> salvar(@RequestBody @Valid MedicoRequestDTO dto) {
        // Envia o DTO para o serviço e recebe o VO salvo.
        MedicoVO medicoSalvoVO = medicoService.salvar(dto);
        // Converte o VO para um EntityModel.
        EntityModel<MedicoVO> medicoModel = assembler.toModel(medicoSalvoVO);
        // Retorna 201 Created com a URI do novo recurso.
        return ResponseEntity
                .created(medicoModel.getRequiredLink("self").toUri())
                .body(medicoModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MedicoVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid MedicoRequestDTO dto) {
        // Chama o serviço de atualização.
        return medicoService.atualizar(id, dto)
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
        if (medicoService.deletar(id)) {
            // Se o serviço retornar true, envia 204 No Content.
            return ResponseEntity.noContent().build();
        } else {
            // Se retornar false, envia 404 Not Found.
            return ResponseEntity.notFound().build();
        }
    }
}