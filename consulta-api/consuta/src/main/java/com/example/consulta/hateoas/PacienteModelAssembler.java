package com.example.consulta.hateoas;

import com.example.consulta.controller.PacienteController;
import com.example.consulta.model.Paciente;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PacienteModelAssembler {

    public EntityModel<Paciente> toModel(Paciente paciente) {
        EntityModel<Paciente> pacienteModel = EntityModel.of(paciente);
        Link selfLink = linkTo(methodOn(PacienteController.class).buscarPorId(paciente.getId())).withSelfRel();
        Link allLink = linkTo(methodOn(PacienteController.class).listar()).withRel("all");
        Link createLink = linkTo(methodOn(PacienteController.class).salvar(null)).withRel("create");
        Link updateLink = linkTo(methodOn(PacienteController.class).atualizar(paciente.getId(), null)).withRel("update");
        Link deleteLink = linkTo(methodOn(PacienteController.class).deletar(paciente.getId())).withRel("delete");
        
        pacienteModel.add(selfLink, allLink, createLink, updateLink, deleteLink);
        return pacienteModel;
    }
}
