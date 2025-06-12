package com.example.consulta.controller;

import com.example.consulta.dto.ProcedimentoRequestDTO;
import com.example.consulta.hateoas.ProcedimentoModelAssembler;
import com.example.consulta.service.ProcedimentoService;
import com.example.consulta.vo.ProcedimentoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProcedimentoController.class)
@DisplayName("Testes do Controller de Procedimentos (API com HATEOAS)")
class ProcedimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcedimentoService procedimentoService;
    @MockBean
    private ProcedimentoModelAssembler assembler;

    private ProcedimentoVO procedimentoVO;
    private ProcedimentoRequestDTO procedimentoRequestDTO;
    private EntityModel<ProcedimentoVO> procedimentoModel;

    @BeforeEach
    void setUp() {
        procedimentoVO = new ProcedimentoVO(1L, "Consulta de Rotina", "Consulta médica de rotina.", 250.0);
        procedimentoRequestDTO = new ProcedimentoRequestDTO("Consulta de Rotina", "Consulta médica de rotina.", 250.0);
        procedimentoModel = EntityModel.of(procedimentoVO,
                linkTo(methodOn(ProcedimentoController.class).buscarPorId(1L)).withSelfRel());
    }

    @Test
    @DisplayName("Deve buscar um procedimento por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando o procedimento e o assembler convertendo o VO.
        when(procedimentoService.buscarPorId(1L)).thenReturn(Optional.of(procedimentoVO));
        when(assembler.toModel(procedimentoVO)).thenReturn(procedimentoModel);

        // Executa a requisição e verifica a resposta HATEOAS.
        mockMvc.perform(get("/procedimentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Consulta de Rotina")))
                .andExpect(jsonPath("$._links.self.href", containsString("/procedimentos/1")));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o procedimento.
        when(procedimentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e verifica o 404.
        mockMvc.perform(get("/procedimentos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar um novo procedimento e retornar status 201 Created")
    void salvar() throws Exception {
        // Simula o serviço e o assembler.
        when(procedimentoService.salvar(any(ProcedimentoRequestDTO.class))).thenReturn(procedimentoVO);
        when(assembler.toModel(procedimentoVO)).thenReturn(procedimentoModel);

        // Executa a requisição POST.
        mockMvc.perform(post("/procedimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(procedimentoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/procedimentos/1")));
    }

    @Test
    @DisplayName("Deve deletar um procedimento existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando 'true' para a exclusão.
        when(procedimentoService.deletar(1L)).thenReturn(true);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/procedimentos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar procedimento inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando 'false' para a exclusão.
        when(procedimentoService.deletar(99L)).thenReturn(false);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/procedimentos/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}