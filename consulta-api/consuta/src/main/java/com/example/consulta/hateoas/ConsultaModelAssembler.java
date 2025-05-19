package com.example.consulta.hateoas;

import com.example.consulta.controller.ConsultaController;
import com.example.consulta.model.Consulta;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

@Component
public class ConsultaModelAssembler {

    public EntityModel<Consulta> toModel(Consulta consulta) {
        EntityModel<Consulta> consultaModel = EntityModel.of(consulta);
        Link selfLink = linkTo(methodOn(ConsultaController.class).buscarPorId(consulta.getId())).withSelfRel();
        Link allLink = linkTo(methodOn(ConsultaController.class).listarTodas()).withRel("all");
        Link createLink = linkTo(methodOn(ConsultaController.class).salvar(null)).withRel("create");
        Link updateLink = linkTo(methodOn(ConsultaController.class).atualizar(consulta.getId(), null)).withRel("update");
        Link deleteLink = linkTo(methodOn(ConsultaController.class).deletar(consulta.getId())).withRel("delete");
        consultaModel.add(selfLink, allLink, createLink, updateLink, deleteLink);
        return consultaModel;
    }
}