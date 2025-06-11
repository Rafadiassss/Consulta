package com.example.consulta.hateoas;

import com.example.consulta.controller.ProcedimentoController;
import com.example.consulta.vo.ProcedimentoVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProcedimentoModelAssembler
        implements RepresentationModelAssembler<ProcedimentoVO, EntityModel<ProcedimentoVO>> {

    @Override
    public EntityModel<ProcedimentoVO> toModel(ProcedimentoVO procedimentoVO) {
        // Cria um EntityModel envolvendo o VO do procedimento.
        return EntityModel.of(procedimentoVO,
                // Adiciona um link para o próprio recurso.
                linkTo(methodOn(ProcedimentoController.class).buscarPorId(procedimentoVO.id())).withSelfRel(),
                // Adiciona um link para a coleção de todos os procedimentos.
                linkTo(methodOn(ProcedimentoController.class).listar()).withRel("procedimentos"));
    }
}