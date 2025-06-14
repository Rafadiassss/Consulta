package com.example.consulta.controller;

import com.example.consulta.dto.EntradaProntuarioRequestDTO;
import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.hateoas.ProntuarioModelAssembler;
import com.example.consulta.service.ProntuarioService;
import com.example.consulta.vo.ProntuarioVO;

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
@RequestMapping("/prontuarios")
@Tag(name = "Prontuarios", description = "Operações para gerenciar os prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;
    private final ProntuarioModelAssembler assembler;

    // Usando injeção via construtor, que é a melhor prática.
    public ProntuarioController(ProntuarioService prontuarioService, ProntuarioModelAssembler assembler) {
        this.prontuarioService = prontuarioService;
        this.assembler = assembler;
    }

    // Este método vai retornar uma resposta HTTP, mas o tipo do objeto no corpo
    // pode variar. Pode ser um modelo de sucesso, uma String de erro, ou até mesmo
    // vazio por isso usar o '?'.
    @PostMapping("/{idUsuario}")
    public ResponseEntity<?> criarProntuario(
            @PathVariable Long idUsuario,
            @RequestBody @Valid ProntuarioRequestDTO dto) throws IllegalArgumentException {

        try {
            // Chama o serviço para criar o prontuário.
            ProntuarioVO prontuarioVO = prontuarioService.criarProntuario(idUsuario, dto);
            // Usa o assembler para converter o VO em um modelo HATEOAS.
            EntityModel<ProntuarioVO> prontuarioModel = assembler.toModel(prontuarioVO);
            // Retorna 201 Created com o modelo no corpo.
            return ResponseEntity.status(HttpStatus.CREATED).body(prontuarioModel);
        } catch (IllegalArgumentException e) {
            // Se o usuário não for médico, retorna 403 Forbidden.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) { // Captura o "Usuário não encontrado"
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{idUsuario}/{idProntuario}")
    public ResponseEntity<EntityModel<ProntuarioVO>> buscarProntuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idProntuario) throws IllegalArgumentException {
        try {
            // Busca o VO do prontuário no serviço.
            ProntuarioVO prontuarioVO = prontuarioService.buscarProntuario(idUsuario, idProntuario);
            // Converte para o modelo HATEOAS.
            EntityModel<ProntuarioVO> prontuarioModel = assembler.toModel(prontuarioVO);

            // Adiciona o link "self" dinâmico aqui no controller, onde temos todo o
            // contexto.
            prontuarioModel.add(
                    linkTo(methodOn(ProntuarioController.class)
                            .buscarProntuario(idUsuario, idProntuario)).withSelfRel());

            // Retorna 200 OK com o modelo HATEOAS completo.
            return ResponseEntity.ok(prontuarioModel);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{idProntuario}/entradas")
    public ResponseEntity<EntityModel<ProntuarioVO>> adicionarNovaEntrada(
            @PathVariable Long idProntuario,
            @RequestBody @Valid EntradaProntuarioRequestDTO dto) {
        try {
            // Adiciona a nova entrada e obtém o prontuário atualizado.
            ProntuarioVO prontuarioAtualizadoVO = prontuarioService.adicionarEntrada(idProntuario, dto);
            // Converte para o modelo HATEOAS.
            EntityModel<ProntuarioVO> prontuarioModel = assembler.toModel(prontuarioAtualizadoVO);
            // Retorna 200 OK com o prontuário completo.
            return ResponseEntity.ok(prontuarioModel);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Endpoint aberto para listar todos os prontuários para fins de desenvolvimento
    // e teste.
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProntuarioVO>>> listarTodos() {
        // Chama o novo método do serviço que não possui validação de usuário.
        List<ProntuarioVO> prontuariosVO = prontuarioService.listarTodosSemValidacao();

        // Converte cada VO em um modelo HATEOAS.
        List<EntityModel<ProntuarioVO>> prontuariosModel = prontuariosVO.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        // Cria a coleção de resposta HATEOAS com um link para si mesma.
        CollectionModel<EntityModel<ProntuarioVO>> collectionModel = CollectionModel.of(prontuariosModel,
                linkTo(methodOn(ProntuarioController.class).listarTodos()).withSelfRel());

        // Retorna 200 OK com a lista completa.
        return ResponseEntity.ok(collectionModel);
    }
}