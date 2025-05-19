package com.example.consulta.hateoas;

import com.example.consulta.controller.UsuarioController;
import com.example.consulta.model.Usuario;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler {

    public EntityModel<Usuario> toModel(Usuario usuario) {
        EntityModel<Usuario> usuarioModel = EntityModel.of(usuario);

        Link selfLink = linkTo(methodOn(UsuarioController.class).buscarPorId(usuario.getId())).withSelfRel();
        Link allLink = linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("all");
        Link createLink = linkTo(methodOn(UsuarioController.class).salvar(null)).withRel("create");
        Link updateLink = linkTo(methodOn(UsuarioController.class).atualizar(usuario.getId(), null)).withRel("update");
        Link deleteLink = linkTo(methodOn(UsuarioController.class).deletar(usuario.getId())).withRel("delete");

        usuarioModel.add(selfLink, allLink, createLink, updateLink, deleteLink);

        return usuarioModel;
    }
}
