package com.example.consulta.hateoas;

import com.example.consulta.controller.PacienteController;
import com.example.consulta.vo.PacienteVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PacienteModelAssembler implements RepresentationModelAssembler<PacienteVO, EntityModel<PacienteVO>> {

    @Override
    public EntityModel<PacienteVO> toModel(PacienteVO pacienteVO) {
        // Cria um EntityModel envolvendo o VO do paciente.
        EntityModel<PacienteVO> pacienteModel = EntityModel.of(pacienteVO,
                // Adiciona um link para o próprio recurso.
                linkTo(methodOn(PacienteController.class).buscarPorId(pacienteVO.id())).withSelfRel(),
                // Adiciona um link para a coleção de todos os pacientes.
                linkTo(methodOn(PacienteController.class).listar()).withRel("pacientes"));

        // Adiciona links para outras ações possíveis.
        pacienteModel
                .add(linkTo(methodOn(PacienteController.class).atualizar(pacienteVO.id(), null)).withRel("update"));
        pacienteModel.add(linkTo(methodOn(PacienteController.class).deletar(pacienteVO.id())).withRel("delete"));

        return pacienteModel;
    }
}