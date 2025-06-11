package com.example.consulta.controller;

import com.example.consulta.dto.ProcedimentoRequestDTO;
import com.example.consulta.hateoas.ProcedimentoModelAssembler;
import com.example.consulta.service.ProcedimentoService;
import com.example.consulta.vo.ProcedimentoVO;
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
@RequestMapping("/procedimentos")
@Tag(name = "Procedimento", description = "Operações para gerenciar procedimentos")
public class ProcedimentoController {

    @Autowired
    private ProcedimentoService procedimentoService;

    @Autowired
    private ProcedimentoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProcedimentoVO>>> listar() {
        // Busca a lista de VOs do serviço.
        List<ProcedimentoVO> procedimentosVO = procedimentoService.listarTodos();
        // Converte cada VO em um EntityModel com links.
        List<EntityModel<ProcedimentoVO>> procedimentosModel = procedimentosVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria o modelo de coleção com um link para a própria lista.
        CollectionModel<EntityModel<ProcedimentoVO>> collectionModel = CollectionModel.of(procedimentosModel,
                linkTo(methodOn(ProcedimentoController.class).listar()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProcedimentoVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return procedimentoService.buscarPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProcedimentoVO>> salvar(@RequestBody @Valid ProcedimentoRequestDTO dto) {
        // Envia o DTO para o serviço e recebe o VO salvo.
        ProcedimentoVO procedimentoSalvoVO = procedimentoService.salvar(dto);
        // Converte o VO para um EntityModel.
        EntityModel<ProcedimentoVO> procedimentoModel = assembler.toModel(procedimentoSalvoVO);
        // Retorna 201 Created com a URI do novo recurso.
        return ResponseEntity
                .created(procedimentoModel.getRequiredLink("self").toUri())
                .body(procedimentoModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProcedimentoVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid ProcedimentoRequestDTO dto) {
        // Chama o serviço de atualização.
        return procedimentoService.atualizar(id, dto)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar.
        if (procedimentoService.deletar(id)) {
            // Retorna 204 No Content para sucesso.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não existia.
            return ResponseEntity.notFound().build();
        }
    }
}
