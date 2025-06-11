package com.example.consulta.hateoas;

import com.example.consulta.controller.PagamentoController;
import com.example.consulta.vo.PagamentoVO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PagamentoModelAssembler implements RepresentationModelAssembler<PagamentoVO, EntityModel<PagamentoVO>> {

    @Override
    public EntityModel<PagamentoVO> toModel(PagamentoVO pagamentoVO) {
        // Cria um EntityModel envolvendo o VO do pagamento.
        return EntityModel.of(pagamentoVO,
                // Adiciona um link para o próprio recurso.
                linkTo(methodOn(PagamentoController.class).buscarPorId(pagamentoVO.id())).withSelfRel(),
                // Adiciona um link para a coleção de todos os pagamentos.
                linkTo(methodOn(PagamentoController.class).listar()).withRel("pagamentos"));
    }
}
