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
        // Configura o mock do serviço para retornar uma lista com uma agenda
        when(agendaService.listarTodas()).thenReturn(Collections.singletonList(agendaVO));

        // Executa uma requisição GET para o endpoint /agendas
        mockMvc.perform(get("/agendas"))
                // Verifica se o status da resposta é 200 OK
                .andExpect(status().isOk())
                // Verifica se o ID do primeiro item é 1
                .andExpect(jsonPath("$[0].id", is(1)))
                // Verifica se a data agendada é 2025-10-20
                .andExpect(jsonPath("$[0].dataAgendada", is("2025-10-20")));
    }

    @Test
    @DisplayName("Deve salvar uma nova agenda e retornar status 201 Created")
    void salvar_comDadosValidos() throws Exception {
        // Configura o mock do serviço para retornar agendaVO ao salvar qualquer
        // AgendaRequestDTO
        when(agendaService.salvar(any(AgendaRequestDTO.class))).thenReturn(agendaVO);

        // Executa uma requisição POST para criar nova agenda
        mockMvc.perform(post("/agendas")
                // Define o tipo de conteúdo como JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Converte o DTO para string JSON e envia no corpo
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                // Verifica se retornou status 201 (Created)
                .andExpect(status().isCreated())
                // Verifica se o header Location contém o path correto
                .andExpect(header().string("Location", containsString("/agendas/1")))
                // Verifica se o ID retornado no corpo é 1
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar salvar com data no passado")
    void salvar_comDataNoPassado() throws Exception {
        // Cria DTO com data passada (2020) para testar validação
        AgendaRequestDTO dtoInvalido = new AgendaRequestDTO(LocalDate.of(2020, 1, 1), List.of(LocalTime.of(9, 0)));

        // Simula requisição POST para API
        mockMvc.perform(post("/agendas")
                // Define content type como JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Converte DTO para JSON string
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                // Verifica se retorna erro 400
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao tentar salvar com lista de horários vazia")
    void salvar_comHorariosVazios() throws Exception {
        // Cria um DTO inválido com data futura e sem horários
        AgendaRequestDTO dtoInvalido = new AgendaRequestDTO(LocalDate.now().plusDays(1), Collections.emptyList());

        // Inicia teste de POST para a API de agendas
        mockMvc.perform(post("/agendas")
                // Define o tipo de conteúdo como JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Serializa o DTO para JSON e envia no corpo
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                // Verifica se retornou erro 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar uma agenda existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Configura o mock do serviço para retornar uma agenda quando atualizar o ID 1
        when(agendaService.atualizar(eq(1L), any(AgendaRequestDTO.class))).thenReturn(Optional.of(agendaVO));

        // Executa uma requisição PUT para atualizar a agenda
        mockMvc.perform(put("/agendas/{id}", 1L)
                // Define o tipo de conteúdo como JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Converte o DTO para string JSON e envia no corpo da requisição
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                // Verifica se retornou status 200 (OK)
                .andExpect(status().isOk())
                // Verifica se o ID retornado é 1
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar agenda inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Configura o mock do serviço para retornar vazio quando tentar atualizar ID 99
        when(agendaService.atualizar(eq(99L), any(AgendaRequestDTO.class))).thenReturn(Optional.empty());

        // Executa requisição PUT para atualizar agenda com ID 99
        mockMvc.perform(put("/agendas/{id}", 99L)
                // Define o tipo de conteúdo como JSON
                .contentType(MediaType.APPLICATION_JSON)
                // Converte o DTO para string JSON
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                // Verifica se retornou status 404 (Not Found)
                .andExpect(status().isNotFound());
    }
}