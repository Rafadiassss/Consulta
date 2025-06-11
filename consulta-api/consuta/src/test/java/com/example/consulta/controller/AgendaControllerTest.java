package com.example.consulta.controller;

import com.example.consulta.dto.AgendaRequestDTO;
import com.example.consulta.dto.AgendaResponseDTO;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private AgendaResponseDTO agendaResponseDTO;

    @BeforeEach
    void setUp() {
        agendaVO = new AgendaVO(1L, LocalDate.of(2025, 10, 20), List.of(LocalTime.of(9, 0)));
        agendaRequestDTO = new AgendaRequestDTO(LocalDate.of(2025, 10, 20), List.of(LocalTime.of(9, 0)));
        agendaResponseDTO = new AgendaResponseDTO(1L, LocalDate.of(2025, 10, 20), List.of(LocalTime.of(9, 0)));
    }

    @Test
    @DisplayName("Deve listar agendas e retornar status 200 OK")
    void listar() throws Exception {
        when(agendaService.listarTodas()).thenReturn(Collections.singletonList(agendaVO));

        mockMvc.perform(get("/agendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].dataAgendada", is("2025-10-20")));
    }

    @Test
    @DisplayName("Deve salvar uma nova agenda e retornar status 201 Created")
    void salvar_comDadosValidos() throws Exception {
        when(agendaService.salvar(any(AgendaRequestDTO.class))).thenReturn(agendaVO);

        mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/agendas/1")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar salvar com data no passado")
    void salvar_comDataNoPassado() throws Exception {
        // Cria um DTO com dados inválidos (data no passado).
        AgendaRequestDTO dtoInvalido = new AgendaRequestDTO(LocalDate.of(2020, 1, 1), List.of(LocalTime.of(9, 0)));

        // Executa a requisição e espera um erro de validação.
        mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar salvar com lista de horários vazia")
    void salvar_comHorariosVazios() throws Exception {
        // Cria um DTO com dados inválidos (lista de horários vazia).
        AgendaRequestDTO dtoInvalido = new AgendaRequestDTO(LocalDate.now().plusDays(1), Collections.emptyList());

        // Executa a requisição e espera um erro de validação.
        mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar uma agenda existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        when(agendaService.atualizar(eq(1L), any(AgendaRequestDTO.class))).thenReturn(Optional.of(agendaVO));

        mockMvc.perform(put("/agendas/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar agenda inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        when(agendaService.atualizar(eq(99L), any(AgendaRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/agendas/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isNotFound());
    }
}