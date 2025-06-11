package com.example.consulta.controller;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
@DisplayName("Testes do Controller de Consultas (API)")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultaService consultaService;
    @MockBean
    private ConsultaModelAssembler assembler;

    private ConsultaVO consultaVO;
    private ConsultaRequestDTO consultaRequestDTO;
    private EntityModel<ConsultaVO> consultaModel;

    @BeforeEach
    void setUp() {
        consultaVO = new ConsultaVO(1L, LocalDateTime.now(), "AGENDADA", "Rotina", null, null, null, null);
        consultaRequestDTO = new ConsultaRequestDTO(LocalDateTime.now().plusDays(5), "AGENDADA", "Rotina", 1L, 1L, null,
                null);
        consultaModel = EntityModel.of(consultaVO,
                linkTo(methodOn(ConsultaController.class).buscarPorId(1L)).withSelfRel());
    }

    @Test
    @DisplayName("Deve salvar uma nova consulta e retornar status 201 Created")
    void salvar() throws Exception {
        // Simula o serviço retornando o VO.
        when(consultaService.salvar(any(ConsultaRequestDTO.class))).thenReturn(consultaVO);
        // Simula o assembler convertendo o VO para modelo HATEOAS.
        when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

        // Executa a requisição POST e verifica a resposta.
        mockMvc.perform(post("/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/consultas/1")));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar salvar com dependência inexistente")
    void salvar_quandoDependenciaNaoEncontrada() throws Exception {
        // Simula o serviço lançando uma exceção porque uma dependência não foi
        // encontrada.
        when(consultaService.salvar(any(ConsultaRequestDTO.class)))
                .thenThrow(new RuntimeException("Paciente não encontrado"));

        // Executa a requisição POST e espera um erro 404.
        mockMvc.perform(post("/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar consulta inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando a consulta para atualizar.
        when(consultaService.atualizar(eq(99L), any(ConsultaRequestDTO.class))).thenReturn(Optional.empty());

        // Executa a requisição PUT e espera um 404.
        mockMvc.perform(put("/consultas/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                .andExpect(status().isNotFound());
    }
}