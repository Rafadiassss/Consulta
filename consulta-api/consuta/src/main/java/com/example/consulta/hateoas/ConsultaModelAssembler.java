package com.example.consulta.hateoas;

import com.example.consulta.controller.ConsultaController;
import com.example.consulta.vo.ConsultaVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ConsultaModelAssembler implements RepresentationModelAssembler<ConsultaVO, EntityModel<ConsultaVO>> {

    @Override
    public EntityModel<ConsultaVO> toModel(ConsultaVO consultaVO) {
        EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);

        // Link para buscar esta consulta (idUsuario genérico como exemplo)
        consultaModel.add(linkTo(methodOn(ConsultaController.class)
                .buscarConsultaVO(0L, consultaVO.id())).withSelfRel());

        // Link para adicionar nova entrada
        consultaModel.add(linkTo(methodOn(ConsultaController.class)
                .adicionarNovaEntrada(consultaVO.id(), null)).withRel("adicionarEntrada"));

        // Link para listar todas as consultas
        consultaModel.add(linkTo(methodOn(ConsultaController.class)
                .listarTodos()).withRel("todasAsConsultas"));

        return consultaModel;
    }
}
