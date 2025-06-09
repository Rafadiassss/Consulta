package com.example.consulta.controller;

import com.example.consulta.model.Agenda;
import com.example.consulta.service.AgendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(AgendaController.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para converter objetos Java para JSON

    @MockBean
    private AgendaService agendaService; // Mock da camada de serviço

    private Agenda agenda;

    @BeforeEach
    void setUp() {
        // Cria uma instância de Agenda com a estrutura de dados correta para ser usada
        // nos testes
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setDataAgendada(LocalDate.of(2025, 10, 20));
        agenda.setHorarios(List.of(LocalTime.of(10, 30), LocalTime.of(11, 0)));
    }

    @Test
    void quandoListar_deveRetornarStatusOkEListaDeAgendas() throws Exception {
        // Arrange (Preparação)
        List<Agenda> listaDeAgendas = Collections.singletonList(agenda);
        when(agendaService.listarTodas()).thenReturn(listaDeAgendas);

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/agendas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].dataAgendada", is("2025-10-20")))
                .andExpect(jsonPath("$[0].horarios", hasSize(2)))
                .andExpect(jsonPath("$[0].horarios[0]", is("10:30:00")));

        verify(agendaService).listarTodas();
    }

    @Test
    void testQuandoBuscarPorIdExistente_deveRetornarStatusOkEAgenda() throws Exception {
        // Arrange
        when(agendaService.buscarPorId(1L)).thenReturn(Optional.of(agenda));

        // Act & Assert
        mockMvc.perform(get("/agendas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.dataAgendada", is("2025-10-20")))
                .andExpect(jsonPath("$.horarios[0]", is("10:30:00")));

        verify(agendaService).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdInexistente() throws Exception {
        // Arrange
        when(agendaService.buscarPorId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/agendas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(agendaService).buscarPorId(99L);
    }

    @Test
    void testQuandoSalvar_deveRetornarStatusOkEAgendaSalva() throws Exception {
        // Arrange
        when(agendaService.salvar(any(Agenda.class))).thenReturn(agenda);

        // Cria um objeto para enviar na requisição (sem ID)
        Agenda agendaParaSalvar = new Agenda();
        agendaParaSalvar.setDataAgendada(LocalDate.of(2025, 10, 20));
        agendaParaSalvar.setHorarios(List.of(LocalTime.of(10, 30), LocalTime.of(11, 0)));

        // Act & Assert
        mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaParaSalvar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1))) // Verifica se o ID foi atribuído na resposta
                .andExpect(jsonPath("$.dataAgendada", is("2025-10-20")));

        // Verifica se o serviço de salvar foi chamado
        verify(agendaService).salvar(any(Agenda.class));
    }

    @Test
    void testQuandoDeletar_deveRetornarStatusOk() throws Exception {
        // Arrange
        doNothing().when(agendaService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/agendas/{id}", 1L))
                .andExpect(status().isOk());

        // Verifica se o método deletar do serviço foi chamado exatamente 1 vez com o ID
        // correto
        verify(agendaService, times(1)).deletar(1L);
    }
}