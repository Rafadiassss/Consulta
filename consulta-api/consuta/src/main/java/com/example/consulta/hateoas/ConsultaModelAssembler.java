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
        // Cria o EntityModel a partir do VO (Value Object).
        EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);

        return consultaModel;
    }
}
