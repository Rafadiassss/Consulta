package com.example.consulta.hateoas;

import com.example.consulta.controller.ProntuarioController;
import com.example.consulta.vo.ProntuarioVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProntuarioModelAssembler implements RepresentationModelAssembler<ProntuarioVO, EntityModel<ProntuarioVO>> {

    @Override
    public EntityModel<ProntuarioVO> toModel(ProntuarioVO prontuarioVO) {
        // Cria o EntityModel a partir do VO (Value Object).
        EntityModel<ProntuarioVO> prontuarioModel = EntityModel.of(prontuarioVO);

        return prontuarioModel;
    }
}
