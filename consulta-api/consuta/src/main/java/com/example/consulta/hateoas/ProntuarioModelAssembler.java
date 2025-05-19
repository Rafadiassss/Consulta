package com.example.consulta.hateoas;

import com.example.consulta.controller.ProntuarioController;
import com.example.consulta.model.Prontuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProntuarioModelAssembler implements RepresentationModelAssembler<Prontuario, EntityModel<Prontuario>> {

    @Override
    public EntityModel<Prontuario> toModel(Prontuario prontuario) {
        return EntityModel.of(prontuario,
            linkTo(methodOn(ProntuarioController.class).buscarProntuario(
                // Como o método busca precisa do idUsuario e idProntuario, você deve passar o idUsuario (por exemplo, do contexto ou outro lugar)
                // Para exemplo, estou passando 0L (você vai precisar adaptar)
                0L, prontuario.getId()))
                .withSelfRel(),
            linkTo(methodOn(ProntuarioController.class).salvar(
                0L, prontuario))
                .withRel("salvar")
        );
    }
}
