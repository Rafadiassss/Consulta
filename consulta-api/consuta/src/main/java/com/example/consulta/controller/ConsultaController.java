package com.example.consulta.controller;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler; // Importa o Assembler
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;
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
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    // Injeta o assembler.
    @Autowired
    private ConsultaModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ConsultaVO>>> listarTodas() {
        // Busca a lista de VOs do serviço.
        List<ConsultaVO> consultasVO = consultaService.listarTodas();
        // Converte cada VO em um EntityModel usando o assembler.
        List<EntityModel<ConsultaVO>> consultasModel = consultasVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria um CollectionModel com os resultados e um link para a própria coleção.
        CollectionModel<EntityModel<ConsultaVO>> collectionModel = CollectionModel.of(consultasModel,
                linkTo(methodOn(ConsultaController.class).listarTodas()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ConsultaVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return consultaService.buscarPorId(id)
                // Se encontrado converte para EntityModel com links.
                .map(assembler::toModel)
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid ConsultaRequestDTO dto) {
        try {
            // Envia o DTO para o serviço e recebe o VO salvo.
            ConsultaVO consultaSalvaVO = consultaService.salvar(dto);
            // Converte o VO para um EntityModel.
            EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaSalvaVO);
            // Retorna 201 Created com a URI do novo recurso e o modelo no corpo.
            return ResponseEntity
                    .created(consultaModel.getRequiredLink("self").toUri())
                    .body(consultaModel);
        } catch (RuntimeException e) {
            // Caminho de erro: traduz a exceção para uma resposta HTTP 404 Not Found.
            // Isso funciona para "Paciente não encontrado", "Médico não encontrado"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ConsultaVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid ConsultaRequestDTO dto) {
        // Chama o serviço de atualização.
        return consultaService.atualizar(id, dto)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Dentro de ConsultaController.java
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (consultaService.deletar(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}