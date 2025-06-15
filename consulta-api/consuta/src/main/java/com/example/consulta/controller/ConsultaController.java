package com.example.consulta.controller;

import com.example.consulta.dto.EntradaConsultaRequestDTO;
import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;

@RestController
@RequestMapping("/consulta")
@Tag(name = "Consulta", description = "Operações para gerenciar os Consulta")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final ConsultaModelAssembler assembler;

    // Usando injeção via construtor, que é a melhor prática.
    public ConsultaController(ConsultaService consultaService, ConsultaModelAssembler assembler) {
        this.consultaService = consultaService;
        this.assembler = assembler;
    }

    // Este método vai retornar uma resposta HTTP, mas o tipo do objeto no corpo
    // pode variar. Pode ser um modelo de sucesso, uma String de erro, ou até mesmo
    // vazio por isso usar o '?'.
    @PostMapping("/{idUsuario}")
    public ResponseEntity<?> criarCusulta(
            @PathVariable Long idUsuario,
            @RequestBody @Valid ConsultaRequestDTO dto) throws IllegalArgumentException {

        try {
            // Chama o serviço para criar o prontuário.
            ConsultaVO consultaVO = consultaService.criarConsulta(idUsuario, dto);
            // Usa o assembler para converter o VO em um modelo HATEOAS.
            EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaVO);
            // Retorna 201 Created com o modelo no corpo.
            return ResponseEntity.status(HttpStatus.CREATED).body(consultaModel);
        } catch (IllegalArgumentException e) {
            // Se o usuário não for médico, retorna 403 Forbidden.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Captura o "Usuário não encontrado"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

   @GetMapping("/{idUsuario}/{idConsulta}")
public ResponseEntity<EntityModel<ConsultaVO>> buscarConsultaVO(
        @PathVariable Long idUsuario,
        @PathVariable Long idConsulta) throws IllegalArgumentException {
    try {
        // CORRIGIDO: chama o serviço corretamente
        ConsultaVO consultaVO = consultaService.buscarConsultaVO(idUsuario, idConsulta);

        // Converte para o modelo HATEOAS
        EntityModel<ConsultaVO> consultaModel = assembler.toModel(consultaVO);

        // Adiciona o link "self"
        consultaModel.add(
                linkTo(methodOn(ConsultaController.class)
                        .buscarConsultaVO(idUsuario, idConsulta)).withSelfRel());

        // Retorna 200 OK
        return ResponseEntity.ok(consultaModel);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    }

    @PostMapping("/{idConsulta}/entradas")
    public ResponseEntity<EntityModel<ConsultaVO>> adicionarNovaEntrada(
            @PathVariable Long idConsulta,
            @RequestBody @Valid EntradaConsultaRequestDTO dto) {
        try {
            // Adiciona a nova entrada e obtém o prontuário atualizado.
            ConsultaVO consultaAtualizadoVO = consultaService.adicionarEntrada(idConsulta, dto);
            // Converte para o modelo HATEOAS.
            EntityModel<ConsultaVO> cunsultaModel = assembler.toModel(consultaAtualizadoVO);
            // Retorna 200 OK com o prontuário completo.
            return ResponseEntity.ok(cunsultaModel);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint aberto para listar todos os prontuários para fins de desenvolvimento
    // e teste.
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ConsultaVO>>> listarTodos() {
        // Chama o novo método do serviço que não possui validação de usuário.
        List<ConsultaVO> consultaVO = consultaService.listarTodosSemValidacao();

        // Converte cada VO em um modelo HATEOAS.
        List<EntityModel<ConsultaVO>> consultaModel = consultaVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        // Cria a coleção de resposta HATEOAS com um link para si mesma.
        CollectionModel<EntityModel<ConsultaVO>> collectionModel = CollectionModel.of(consultaModel,
                linkTo(methodOn(ConsultaController.class).listarTodos()).withSelfRel());

        // Retorna 200 OK com a lista completa.
        return ResponseEntity.ok(collectionModel);
    }
}