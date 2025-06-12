package com.example.consulta.controller;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.dto.EspecialidadeResponseDTO;
import com.example.consulta.service.EspecialidadeService;
import com.example.consulta.vo.EspecialidadeVO;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EspecialidadeController.class)
@DisplayName("Testes do Controller de Especialidades (API com DTO)")
class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EspecialidadeService especialidadeService;

    private EspecialidadeVO especialidadeVO;
    private EspecialidadeRequestDTO especialidadeRequestDTO;
    private EspecialidadeResponseDTO especialidadeResponseDTO;

    @BeforeEach
    void setUp() {
        // Objeto VO que o serviço (mock) irá simular retornar.
        especialidadeVO = new EspecialidadeVO(1L, "Cardiologia", "Cuida do coração.");
        // Objeto DTO que enviaremos no corpo das requisições POST/PUT.
        especialidadeRequestDTO = new EspecialidadeRequestDTO("Cardiologia", "Cuida do coração.");
        // Objeto DTO que esperamos na resposta.
        especialidadeResponseDTO = new EspecialidadeResponseDTO(1L, "Cardiologia", "Cuida do coração.");
    }

    @Test
    @DisplayName("Deve listar especialidades e retornar status 200 OK")
    void listar() throws Exception {
        // Simula o serviço retornando uma lista com nosso VO.
        when(especialidadeService.listarTodas()).thenReturn(Collections.singletonList(especialidadeVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/especialidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Cardiologia")));
    }

    @Test
    @DisplayName("Deve buscar por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando a especialidade.
        when(especialidadeService.buscarPorId(1L)).thenReturn(Optional.of(especialidadeVO));

        // Executa a requisição GET.
        mockMvc.perform(get("/especialidades/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando a especialidade.
        when(especialidadeService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e espera o status 404.
        mockMvc.perform(get("/especialidades/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar uma nova especialidade e retornar status 201 Created")
    void salvar_comDadosValidos() throws Exception {
        // Simula o serviço salvando e retornando o VO.
        when(especialidadeService.salvar(any(EspecialidadeRequestDTO.class))).thenReturn(especialidadeVO);

        // Executa a requisição POST.
        mockMvc.perform(post("/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadeRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/especialidades/1")));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar salvar com nome vazio")
    void salvar_comNomeVazio() throws Exception {
        // Cria um DTO com dados inválidos (nome em branco).
        EspecialidadeRequestDTO dtoInvalido = new EspecialidadeRequestDTO("", "Descrição válida");

        // Executa a requisição e espera um erro de validação.
        mockMvc.perform(post("/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve deletar especialidade existente e retornar 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando 'true' para a exclusão bem-sucedida.
        when(especialidadeService.deletar(1L)).thenReturn(true);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/especialidades/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar especialidade inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando 'false'.
        when(especialidadeService.deletar(99L)).thenReturn(false);

        // Executa a requisição DELETE e espera 404.
        mockMvc.perform(delete("/especialidades/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}