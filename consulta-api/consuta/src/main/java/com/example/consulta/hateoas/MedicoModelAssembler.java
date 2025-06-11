package com.example.consulta.hateoas;

import com.example.consulta.controller.MedicoController;
import com.example.consulta.vo.MedicoVO; // Importa o VO
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class MedicoModelAssembler implements RepresentationModelAssembler<MedicoVO, EntityModel<MedicoVO>> {

    @Override
    public EntityModel<MedicoVO> toModel(MedicoVO medicoVO) {
        // Cria um EntityModel envolvendo o VO do médico.
        EntityModel<MedicoVO> medicoModel = EntityModel.of(medicoVO,
                // Adiciona um link para o próprio recurso. Ex: /medicos/1
                linkTo(methodOn(MedicoController.class).buscarPorId(medicoVO.id())).withSelfRel(),
                // Adiciona um link para a coleção de todos os médicos. Ex: /medicos
                linkTo(methodOn(MedicoController.class).listar()).withRel("medicos"));

        // Adicionando os links de ações possíveis, como no seu original.
        // Passamos 'null' para os @RequestBody pois só queremos gerar a URL.
        medicoModel.add(linkTo(methodOn(MedicoController.class).salvar(null)).withRel("create"));
        medicoModel.add(linkTo(methodOn(MedicoController.class).atualizar(medicoVO.id(), null)).withRel("update"));
        medicoModel.add(linkTo(methodOn(MedicoController.class).deletar(medicoVO.id())).withRel("delete"));

        return medicoModel;
    }
}
