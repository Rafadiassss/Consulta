package com.example.consulta.hateoas;

import com.example.consulta.controller.PagamentoController;
import com.example.consulta.model.Pagamento;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PagamentoModelAssembler {

    public EntityModel<Pagamento> toModel(Pagamento pagamento) {
        EntityModel<Pagamento> pagamentoModel = EntityModel.of(pagamento);
        Link selfLink = linkTo(methodOn(PagamentoController.class).buscarPorId(pagamento.getId())).withSelfRel();
        Link allLink = linkTo(methodOn(PagamentoController.class).listar()).withRel("all");
        Link createLink = linkTo(methodOn(PagamentoController.class).salvar(null)).withRel("create");
        pagamentoModel.add(selfLink, allLink,createLink);
        return pagamentoModel;
    }
}
