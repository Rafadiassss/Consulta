package com.example.consulta.controller;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.hateoas.PagamentoModelAssembler;
import com.example.consulta.service.PagamentoService;
import com.example.consulta.vo.PagamentoVO;
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
@RequestMapping("/pagamentos")
@Tag(name = "Pagamento", description = "Operações para gerenciar pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PagamentoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PagamentoVO>>> listar() {
        // Busca a lista de VOs do serviço.
        List<PagamentoVO> pagamentosVO = pagamentoService.listarTodos();
        // Converte cada VO em um EntityModel com links HATEOAS.
        List<EntityModel<PagamentoVO>> pagamentosModel = pagamentosVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Cria o modelo de coleção com um link para a própria lista.
        CollectionModel<EntityModel<PagamentoVO>> collectionModel = CollectionModel.of(pagamentosModel,
                linkTo(methodOn(PagamentoController.class).listar()).withSelfRel());
        // Retorna 200 OK com a coleção HATEOAS.
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PagamentoVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return pagamentoService.buscarPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<PagamentoVO>> salvar(@RequestBody @Valid PagamentoRequestDTO dto) {
        // Envia o DTO para o serviço e recebe o VO salvo.
        PagamentoVO pagamentoSalvoVO = pagamentoService.salvar(dto);
        // Converte o VO para um EntityModel.
        EntityModel<PagamentoVO> pagamentoModel = assembler.toModel(pagamentoSalvoVO);
        // Retorna 201 Created com a URI do novo recurso.
        return ResponseEntity
                .created(pagamentoModel.getRequiredLink("self").toUri())
                .body(pagamentoModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PagamentoVO>> atualizar(@PathVariable Long id,
            @RequestBody @Valid PagamentoRequestDTO dto) {
        // Chama o serviço de atualização.
        return pagamentoService.atualizar(id, dto)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar.
        if (pagamentoService.deletar(id)) {
            // Retorna 204 No Content para sucesso.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não existia.
            return ResponseEntity.notFound().build();
        }
    }
}