package com.example.consulta.hateoas;

import com.example.consulta.controller.ConsultaController;
import com.example.consulta.vo.ConsultaVO; // Importa o VO
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ConsultaModelAssembler implements RepresentationModelAssembler<ConsultaVO, EntityModel<ConsultaVO>> {

    @Override
    public EntityModel<ConsultaVO> toModel(ConsultaVO consultaVO) {
        // Cria o EntityModel a partir do VO (Value Object).
        EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO,
                // Adiciona o link para o próprio recurso.
                linkTo(methodOn(ConsultaController.class).buscarPorId(consultaVO.id())).withSelfRel(),
                // Adiciona o link para a lista de todas as consultas.
                linkTo(methodOn(ConsultaController.class).listarTodas()).withRel("consultas"));

        // Adiciona os links para outras ações possíveis.
        consultaModel.add(linkTo(methodOn(ConsultaController.class).salvar(null)).withRel("create"));
        consultaModel
                .add(linkTo(methodOn(ConsultaController.class).atualizar(consultaVO.id(), null)).withRel("update"));
        consultaModel.add(linkTo(methodOn(ConsultaController.class).deletar(consultaVO.id())).withRel("delete"));

        return consultaModel;
    }
}