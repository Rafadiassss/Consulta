package com.example.consulta.hateoas;

import com.example.consulta.controller.MedicoController;
import com.example.consulta.model.Medico;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class MedicoModelAssembler {

    public EntityModel<Medico> toModel(Medico medico) {
        EntityModel<Medico> medicoModel = EntityModel.of(medico);
        Link selfLink = linkTo(methodOn(MedicoController.class).buscarPorId(medico.getId())).withSelfRel();
        Link allLink = linkTo(methodOn(MedicoController.class).listar()).withRel("all");
        Link createLink = linkTo(methodOn(MedicoController.class).salvar(null)).withRel("create");
        Link updateLink = linkTo(methodOn(MedicoController.class).atualizar(medico.getId(), null)).withRel("update");
        Link deleteLink = linkTo(methodOn(MedicoController.class).deletar(medico.getId())).withRel("delete");
        medicoModel.add(selfLink, allLink, createLink, updateLink, deleteLink);
        return medicoModel;
    }
}
