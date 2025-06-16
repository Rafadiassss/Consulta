package com.example.consulta.controller;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.EntradaConsultaVO;
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
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
@DisplayName("Testes do Controller de Consulta (API)")
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

        @BeforeEach
        void setUp() {
                EntradaConsultaVO entradaVO = new EntradaConsultaVO(1L, LocalDateTime.now(), "Diagnóstico",
                                "Tratamento", "Obs");
                consultaVO = new ConsultaVO(10L, "PRT-001", 1L, Collections.singletonList(entradaVO));
                consultaRequestDTO = new ConsultaRequestDTO("PRT-001", 1L);
        }

        @Test
        @DisplayName("Deve criar uma consulta e retornar status 201 Created")
        void criarConsulta_comSucesso() throws Exception {
                // Cria um modelo HATEOAS com o objeto consultaVO
                EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);
                // Mock: simula chamada ao service retornando consultaVO
                when(consultaService.criarConsulta(eq(1L), any(ConsultaRequestDTO.class)))
                                .thenReturn(consultaVO);
                // Mock: simula conversão do VO para modelo HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa POST para /consulta/1 com o DTO como JSON
                mockMvc.perform(post("/consulta/{idUsuario}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                // Verifica se retornou HTTP 201 Created
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve retornar status 403 Forbidden ao tentar criar consulta com usuário não-médico")
        void criarConsulta_quandoUsuarioNaoEhMedico() throws Exception {
                // Configura o mock do serviço para lançar exceção quando usuário não é médico
                when(consultaService.criarConsulta(eq(2L), any(ConsultaRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Apenas médicos podem criar consulta."));

                // Faz requisição POST para criar consulta com ID de usuário não-médico
                mockMvc.perform(post("/consulta/{idUsuario}", 2L)
                                // Define tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte DTO para JSON e envia no corpo
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                // Verifica se retorna status 403 (Forbidden)
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve buscar uma consulta e retornar status 200 OK")
        void buscarConsulta_comSucesso() throws Exception {
                // Cria um modelo HATEOAS com o objeto consultaVO
                EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);
                // Configura mock do service para retornar consultaVO quando buscar consulta
                // 1,10
                when(consultaService.buscarConsultaVO(1L, 10L)).thenReturn(consultaVO);
                // Configura mock do assembler para retornar o modelo HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Faz requisição GET para buscar consulta
                mockMvc.perform(get("/consulta/{idUsuario}/{idConsulta}", 1L, 10L))
                                // Verifica se retornou status 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o número da consulta está correto no JSON
                                .andExpect(jsonPath("$.numero", is("PRT-001")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar consulta com usuário inexistente")
        void buscarConsulta_quandoUsuarioNaoEncontrado() throws Exception {
                // Configura o mock para simular erro quando usuário 99 não existe
                when(consultaService.buscarConsultaVO(99L, 10L))
                                .thenThrow(new RuntimeException("Usuário não encontrado."));

                // Faz requisição GET para consulta com usuário inválido
                mockMvc.perform(get("/consulta/{idUsuario}/{idConsulta}", 99L, 10L))
                                // Verifica se retorna HTTP 404
                                .andExpect(status().isNotFound());
        }
}