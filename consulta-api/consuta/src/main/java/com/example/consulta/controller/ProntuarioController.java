package com.example.consulta.controller;

import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.hateoas.ProntuarioModelAssembler; // Importa o Assembler
import com.example.consulta.service.ProntuarioService;
import com.example.consulta.vo.ProntuarioVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/prontuario")
@Tag(name = "prontuario", description = "Operações para gerenciar as prontuario")
public class ProntuarioController {

    @Autowired
    private ProntuarioService prontuarioService;

    // Injeta o assembler.
    @Autowired
    private ProntuarioModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProntuarioVO>>> listarTodas() {
        // Busca a lista de VOs do serviço.
        List<ProntuarioVO> ProntuarioVO = prontuarioService.listarTodos();
        // Converte cada VO em um EntityModel usando o assembler.
        List<EntityModel<ProntuarioVO>> prontuarioModel = ProntuarioVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria um CollectionModel com os resultados e um link para a própria coleção.
        CollectionModel<EntityModel<ProntuarioVO>> collectionModel = CollectionModel.of(prontuarioModel,
                linkTo(methodOn(ProntuarioController.class).listarTodas()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProntuarioVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return prontuarioService.buscarPorId(id)
                // Se encontrado converte para EntityModel com links.
                .map(assembler::toModel)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid ProntuarioRequestDTO dto) {
        try {
            // Envia o DTO para o serviço e recebe o VO salvo.
            ProntuarioVO prontuarioSalvaVO = prontuarioService.salvar(dto);
            // Converte o VO para um EntityModel.
            EntityModel<ProntuarioVO> prontuarioModel = assembler.toModel(prontuarioSalvaVO);
            // Retorna 201 Created com a URI do novo recurso e o modelo no corpo.
            return ResponseEntity
                    .created(prontuarioModel.getRequiredLink("self").toUri())
                    .body(prontuarioModel);
        } catch (RuntimeException e) {
            // Caminho de erro: traduz a exceção para uma resposta HTTP 404 Not Found.
            // Isso funciona para "Paciente não encontrado", "Médico não encontrado"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProntuarioVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid ProntuarioRequestDTO dto) {
        // Chama o serviço de atualização.
        return prontuarioService.atualizar(id, dto)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Dentro de ConsultaController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (prontuarioService.deletar(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}