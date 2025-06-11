package com.example.consulta.controller;

import com.example.consulta.dto.EspecialidadeRequestDTO;
import com.example.consulta.service.EspecialidadeService;
import com.example.consulta.vo.EspecialidadeVO; // Importa o VO
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
@Import(EspecialidadeControllerTest.EspecialidadeControllerTestConfig.class)
@DisplayName("Testes do Controller de Especialidades (API)")
class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EspecialidadeService especialidadeService;

    @TestConfiguration
    static class EspecialidadeControllerTestConfig {
        @Bean
        public EspecialidadeService especialidadeService() {
            return Mockito.mock(EspecialidadeService.class);
        }
    }

    // Agora temos um VO para simular o retorno do serviço
    private EspecialidadeVO especialidadeVO;
    private EspecialidadeRequestDTO especialidadeRequestDTO;

    @BeforeEach
    void setUp() {
        // Objeto VO que o serviço (mock) irá simular retornar.
        especialidadeVO = new EspecialidadeVO(1L, "Cardiologia", "Cuida do coração.");
        // Objeto DTO que enviaremos no corpo das requisições POST/PUT.
        especialidadeRequestDTO = new EspecialidadeRequestDTO("Cardiologia", "Cuida do coração.");
    }

    @Test
    @DisplayName("Deve listar especialidades e retornar status 200 OK")
    void listar() throws Exception {
        // Simula o serviço retornando uma lista de VOs.
        when(especialidadeService.listarTodas()).thenReturn(Collections.singletonList(especialidadeVO));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/especialidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Cardiologia")));
    }

    @Test
    @DisplayName("Deve buscar especialidade por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // CORREÇÃO: O mock do serviço agora retorna um Optional de VO.
        when(especialidadeService.buscarPorId(1L)).thenReturn(Optional.of(especialidadeVO));

        // Executa a requisição GET.
        mockMvc.perform(get("/especialidades/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Cardiologia")));
    }

    @Test
    @DisplayName("Deve salvar uma nova especialidade e retornar status 201 Created")
    void salvar() throws Exception {
        // CORREÇÃO: O mock do serviço agora retorna o VO da especialidade salva.
        when(especialidadeService.salvar(any(EspecialidadeRequestDTO.class))).thenReturn(especialidadeVO);

        // Executa a requisição POST.
        mockMvc.perform(post("/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadeRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/especialidades/1")));
    }
}