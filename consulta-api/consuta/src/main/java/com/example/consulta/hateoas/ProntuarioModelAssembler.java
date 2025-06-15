package com.example.consulta.hateoas;

import com.example.consulta.controller.ProntuarioController;
import com.example.consulta.vo.ProntuarioVO; // Importa o VO
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProntuarioModelAssembler implements RepresentationModelAssembler<ProntuarioVO, EntityModel<ProntuarioVO>> {

        @Override
        public EntityModel<ProntuarioVO> toModel(ProntuarioVO prontuarioVO) {
                // Cria o EntityModel a partir do VO (Value Object).
                EntityModel<ProntuarioVO> consultaModel = EntityModel.of(prontuarioVO,
                                // Adiciona o link para o próprio recurso.
                                linkTo(methodOn(ProntuarioController.class).buscarPorId(prontuarioVO.id())).withSelfRel(),
                                // Adiciona o link para a lista de todas as consultas.
                                linkTo(methodOn(ProntuarioController.class).listarTodas()).withRel("prontuario"));

                // Adiciona os links para outras ações possíveis.
                consultaModel.add(linkTo(methodOn(ProntuarioController.class).salvar(null)).withRel("create"));
                consultaModel
                                .add(linkTo(methodOn(ProntuarioController.class).atualizar(prontuarioVO.id(), null))
                                                .withRel("update"));
                consultaModel.add(
                                linkTo(methodOn(ProntuarioController.class).deletar(prontuarioVO.id())).withRel("delete"));

                return consultaModel;
        }
}