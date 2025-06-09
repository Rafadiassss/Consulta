package com.example.consulta.controller;

import com.example.consulta.hateoas.MedicoModelAssembler;
import com.example.consulta.model.Medico;
import com.example.consulta.service.MedicoService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private MedicoModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Medico>>> listar() {
        List<EntityModel<Medico>> medicos = medicoService.listarTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Medico>> collectionModel = CollectionModel.of(medicos,
                linkTo(methodOn(MedicoController.class).listar()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> buscarPorId(@PathVariable Long id) {
        return medicoService.buscarPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Medico>> salvar(@RequestBody Medico medico) {
        Medico novoMedico = medicoService.salvar(medico);
        EntityModel<Medico> medicoModel = assembler.toModel(novoMedico);
        URI uri = linkTo(methodOn(MedicoController.class).buscarPorId(novoMedico.getId())).toUri();
        return ResponseEntity.created(uri).body(medicoModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Medico>> atualizar(@PathVariable Long id, @RequestBody Medico medicoAtualizado) {
        return medicoService.buscarPorId(id)
                .map(medicoExistente -> {
                    medicoExistente.setNome(medicoAtualizado.getNome());
                    medicoExistente.setCrm(medicoAtualizado.getCrm());
                    medicoExistente.setEspecialidade(medicoAtualizado.getEspecialidade());
                    // Atualize outros campos de Medico/Usuario conforme necessário
                    Medico atualizado = medicoService.salvar(medicoExistente);
                    EntityModel<Medico> medicoModel = assembler.toModel(atualizado);
                    return ResponseEntity.ok(medicoModel);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return medicoService.buscarPorId(id)
                .map(medico -> {
                    medicoService.deletar(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}