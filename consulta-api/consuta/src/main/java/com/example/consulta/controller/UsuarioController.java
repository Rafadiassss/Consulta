package com.example.consulta.controller;

import com.example.consulta.dto.MedicoRequestDTO;
import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.hateoas.UsuarioModelAssembler;
import com.example.consulta.service.UsuarioService;
import com.example.consulta.vo.UsuarioVO;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuário", description = "Operações para gerenciar usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioVO>>> listarTodos() {
        // Busca a lista de VOs do serviço.
        List<EntityModel<UsuarioVO>> usuarios = usuarioService.listarTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        // Monta a resposta de coleção HATEOAS.
        return ResponseEntity.ok(
                CollectionModel.of(usuarios,
                        linkTo(methodOn(UsuarioController.class).listarTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioVO>> buscarPorId(@PathVariable Long id) {
        // Busca o VO do serviço.
        return usuarioService.buscarPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/medico")
    public ResponseEntity<EntityModel<UsuarioVO>> salvarMedico(@RequestBody @Valid MedicoRequestDTO dto) {
        // Envia o DTO específico para o serviço e recebe o VO genérico.
        UsuarioVO medicoSalvo = usuarioService.salvarMedico(dto);
        // Converte o VO para o modelo HATEOAS.
        EntityModel<UsuarioVO> medicoModel = assembler.toModel(medicoSalvo);
        // Retorna 201 Created.
        return ResponseEntity
                .created(medicoModel.getRequiredLink("self").toUri())
                .body(medicoModel);
    }

    @PostMapping("/paciente")
    public ResponseEntity<EntityModel<UsuarioVO>> salvarPaciente(@RequestBody @Valid PacienteRequestDTO dto) {
        // Envia o DTO específico para o serviço e recebe o VO genérico.
        UsuarioVO pacienteSalvo = usuarioService.salvarPaciente(dto);
        // Converte o VO para o modelo HATEOAS.
        EntityModel<UsuarioVO> pacienteModel = assembler.toModel(pacienteSalvo);
        // Retorna 201 Created.
        return ResponseEntity
                .created(pacienteModel.getRequiredLink("self").toUri())
                .body(pacienteModel);
    }

    // O endpoint PUT genérico foi removido para incentivar atualizações por
    // endpoints mais específicos se necessário.
    // Se for mantido, um DTO de atualização seria o ideal.

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Chama o serviço para deletar.
        if (usuarioService.deletar(id)) {
            // Retorna 204 No Content para sucesso.
            return ResponseEntity.noContent().build();
        } else {
            // Retorna 404 Not Found se o recurso não existia.
            return ResponseEntity.notFound().build();
        }
    }
}
