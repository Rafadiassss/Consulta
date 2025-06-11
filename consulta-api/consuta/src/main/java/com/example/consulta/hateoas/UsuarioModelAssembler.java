package com.example.consulta.hateoas;

import com.example.consulta.controller.UsuarioController;
import com.example.consulta.vo.UsuarioVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioVO, EntityModel<UsuarioVO>> {

    @Override
    public EntityModel<UsuarioVO> toModel(UsuarioVO usuarioVO) {
        // Cria o EntityModel a partir do VO genérico.
        EntityModel<UsuarioVO> usuarioModel = EntityModel.of(usuarioVO,
                linkTo(methodOn(UsuarioController.class).buscarPorId(usuarioVO.id())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarTodos()).withRel("usuarios"));

        usuarioModel.add(linkTo(methodOn(UsuarioController.class).deletar(usuarioVO.id())).withRel("delete"));

        return usuarioModel;
    }
}