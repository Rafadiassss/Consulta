package com.example.consulta.controller;

import com.example.consulta.dto.EntradaConsultaRequestDTO;
import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;

@RestController
@RequestMapping("/consulta")
@Tag(name = "Consulta", description = "Operações para gerenciar as Consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ConsultaModelAssembler assembler;

    public ConsultaController(ConsultaService consultaService, ConsultaModelAssembler assembler) {
        this.consultaService = consultaService;
        this.assembler = assembler;
    }

    @PostMapping("/{idUsuario}")
    public ResponseEntity<?> criarConsulta(
            @PathVariable Long idUsuario,
            @RequestBody @Valid ConsultaRequestDTO dto) {

        try {
            ConsultaVO consultaVO = consultaService.criarConsulta(idUsuario, dto);
            EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaVO);
            return ResponseEntity.status(HttpStatus.CREATED).body(consultaModel);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{idUsuario}/{idConsulta}")
    public ResponseEntity<EntityModel<ConsultaVO>> buscarConsultaVO(
            @PathVariable Long idUsuario,
            @PathVariable Long idConsulta) {

        try {
            ConsultaVO consultaVO = consultaService.buscarConsultaVO(idUsuario, idConsulta);
            EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaVO);
            consultaModel.add(linkTo(methodOn(ConsultaController.class)
                    .buscarConsultaVO(idUsuario, idConsulta)).withSelfRel());

            return ResponseEntity.ok(consultaModel);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{idConsulta}/entradas")
    public ResponseEntity<EntityModel<ConsultaVO>> adicionarNovaEntrada(
            @PathVariable Long idConsulta,
            @RequestBody @Valid EntradaConsultaRequestDTO dto) {

        try {
            ConsultaVO consultaAtualizadoVO = consultaService.adicionarEntrada(idConsulta, dto);
            EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaAtualizadoVO);
            return ResponseEntity.ok(consultaModel);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ConsultaVO>>> listarTodos() {
        List<ConsultaVO> consultaVO = consultaService.listarTodosSemValidacao();

        List<EntityModel<ConsultaVO>> consultaModel = consultaVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ConsultaVO>> collectionModel = CollectionModel.of(
                consultaModel,
                linkTo(methodOn(ConsultaController.class).listarTodos()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
