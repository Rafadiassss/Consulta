package com.example.consulta.controller;

import com.example.consulta.dto.AgendaRequestDTO;
import com.example.consulta.service.AgendaService;
import com.example.consulta.vo.AgendaVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaController.class)
@DisplayName("Testes do Controller de Agendas (API com DTO)")
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendaService agendaService;

    private AgendaVO agendaVO;
    private AgendaRequestDTO agendaRequestDTO;

    @BeforeEach
    void setUp() {
        // Objeto VO que o serviço irá simular retornar.
        agendaVO = new AgendaVO(1L, LocalDate.of(2025, 10, 20), List.of(LocalTime.of(9, 0)));

        // Objeto DTO que será enviado no corpo das requisições POST/PUT.
        agendaRequestDTO = new AgendaRequestDTO(LocalDate.of(2025, 10, 20), List.of(LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("Deve listar agendas e retornar status 200 OK")
    void listar() throws Exception {
        // Simula o serviço retornando uma lista de VOs.
        when(agendaService.listarTodas()).thenReturn(Collections.singletonList(agendaVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/agendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].dataAgendada", is("2025-10-20")));
    }

    @Test
    @DisplayName("Deve buscar agenda por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando a agenda.
        when(agendaService.buscarPorId(1L)).thenReturn(Optional.of(agendaVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/agendas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando a agenda.
        when(agendaService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e verifica o status 404.
        mockMvc.perform(get("/agendas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar uma nova agenda e retornar status 201 Created")
    void salvar() throws Exception {
        // Simula o serviço salvando e retornando o VO.
        when(agendaService.salvar(any(AgendaRequestDTO.class))).thenReturn(agendaVO);

        // Executa a requisição POST e verifica a resposta.
        mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/agendas/1"))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve deletar uma agenda existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando 'true' para a exclusão bem-sucedida.
        when(agendaService.deletar(1L)).thenReturn(true);

        // Executa a requisição DELETE e verifica o status 204.
        mockMvc.perform(delete("/agendas/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar agenda inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando 'false' pois a agenda não foi encontrada.
        when(agendaService.deletar(99L)).thenReturn(false);

        // Executa a requisição DELETE e verifica o status 404.
        mockMvc.perform(delete("/agendas/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}