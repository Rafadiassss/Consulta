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

    // Injeta o serviço de paciente.
    @Autowired
    private PacienteService pacienteService;

    // Injeta o assembler para construir as respostas HATEOAS.
    @Autowired
    private PacienteModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PacienteVO>>> listar() {
        // Busca a lista de VOs do serviço.
        List<PacienteVO> pacientesVO = pacienteService.listarTodos();
        // Converte cada VO em um EntityModel com links HATEOAS.
        List<EntityModel<PacienteVO>> pacientesModel = pacientesVO.stream()
                .map(vo -> assembler.toModel(vo))
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
                .map(vo -> assembler.toModel(vo))
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
                .map(vo -> assembler.toModel(vo))
                // Envolve em uma resposta 200 OK.
                .map(ResponseEntity::ok)
                // Se não encontrado, retorna 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar, que retorna 'true' se a exclusão foi
        // bem-sucedida.
        if (pacienteService.deletar(id)) {
            // Retorna 204 No Content para indicar sucesso na exclusão.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não foi encontrado para deletar.
            return ResponseEntity.notFound().build();
        }
    }
}