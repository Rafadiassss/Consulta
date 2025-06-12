package com.example.consulta.hateoas;

import com.example.consulta.controller.SecretariaController;
import com.example.consulta.dto.SecretariaResponseDTO; // Importa o DTO de Resposta
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
// A interface agora é parametrizada com o DTO de Resposta
public class SecretariaModelAssembler
        implements RepresentationModelAssembler<SecretariaResponseDTO, EntityModel<SecretariaResponseDTO>> {

    @Override
    public EntityModel<SecretariaResponseDTO> toModel(SecretariaResponseDTO dto) {
        // Cria um EntityModel envolvendo o DTO.
        return EntityModel.of(dto,
                // Os links são construídos usando o ID do DTO.
                linkTo(methodOn(SecretariaController.class).buscarPorId(dto.id())).withSelfRel(),
                linkTo(methodOn(SecretariaController.class).listar()).withRel("secretarias"));
    }
}