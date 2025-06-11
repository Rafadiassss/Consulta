package com.example.consulta.controller;

import com.example.consulta.dto.ExameRequestDTO;
import com.example.consulta.service.ExameService;
import com.example.consulta.vo.ExameVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExameController.class)
@DisplayName("Testes do Controller de Exames (API)")
class ExameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Usando @MockBean para que o Spring substitua o serviço real por um mock.
    @MockBean
    private ExameService exameService;

    private ExameVO exameVO;
    private ExameRequestDTO exameRequestDTO;

    @BeforeEach
    void setUp() {
        // Objeto VO que o serviço (mock) irá simular retornar.
        exameVO = new ExameVO(1L, "Hemograma Completo", "Resultados normais", "Coleta realizada sem problemas.", 10L);

        // Objeto DTO que será enviado no corpo das requisições.
        exameRequestDTO = new ExameRequestDTO("Hemograma Completo", "Resultados normais",
                "Coleta realizada sem problemas.", 10L);
    }

    @Test
    @DisplayName("Deve listar todos os exames e retornar status 200 OK")
    void listar() throws Exception {
        // Simula o serviço retornando uma lista de VOs.
        when(exameService.listarTodos()).thenReturn(Collections.singletonList(exameVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/exames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Hemograma Completo")));
    }

    @Test
    @DisplayName("Deve buscar um exame por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando o exame pelo ID.
        when(exameService.buscarPorId(1L)).thenReturn(Optional.of(exameVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/exames/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o exame.
        when(exameService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET e espera um 404.
        mockMvc.perform(get("/exames/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar um novo exame e retornar status 201 Created")
    void salvar() throws Exception {
        // Simula o serviço salvando e retornando o VO.
        when(exameService.salvar(any(ExameRequestDTO.class))).thenReturn(exameVO);

        // Executa a requisição POST.
        mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exameRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/exames/1")))
                .andExpect(jsonPath("$.nome", is("Hemograma Completo")));
    }

    @Test
    @DisplayName("Deve atualizar um exame existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Simula o serviço de atualização retornando o VO atualizado.
        when(exameService.atualizar(eq(1L), any(ExameRequestDTO.class))).thenReturn(Optional.of(exameVO));

        // Executa a requisição PUT.
        mockMvc.perform(put("/exames/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exameRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar exame inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o exame para atualizar.
        when(exameService.atualizar(eq(99L), any(ExameRequestDTO.class))).thenReturn(Optional.empty());

        // Executa a requisição PUT e espera um 404.
        mockMvc.perform(put("/exames/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exameRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um exame existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando 'true' para a exclusão bem-sucedida.
        when(exameService.deletar(1L)).thenReturn(true);

        // Executa a requisição DELETE e verifica o status 204.
        mockMvc.perform(delete("/exames/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar exame inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando 'false' pois o exame não foi encontrado.
        when(exameService.deletar(99L)).thenReturn(false);

        // Executa a requisição DELETE e verifica o status 404.
        mockMvc.perform(delete("/exames/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}