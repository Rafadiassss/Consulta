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
@DisplayName("Testes do Controller de Prontuários (API)")
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
                consultaVO = new ConsultaVO(10L, "PRT-001", Collections.singletonList(entradaVO));
                consultaRequestDTO = new ConsultaRequestDTO("PRT-001");
        }

        @Test
        @DisplayName("Deve criar um prontuário e retornar status 201 Created")
        void criarProntuario_comSucesso() throws Exception {
                // Prepara o modelo HATEOAS esperado.
                EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);
                // Simula o serviço retornando o VO com sucesso.
                when(consultaService.criarConsulta(eq(1L), any(ConsultaRequestDTO.class)))
                                .thenReturn(consultaVO);
                // Simula o assembler convertendo o VO.
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa a requisição POST e verifica o resultado.
                mockMvc.perform(post("/consulta/{idUsuario}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve retornar status 403 Forbidden ao tentar criar consulta com usuário não-médico")
        void criarProntuario_quandoUsuarioNaoEhMedico() throws Exception {
                // Simula o serviço lançando uma exceção de permissão.
                when(consultaService.criarConsulta(eq(2L), any(ConsultaRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Apenas médicos podem criar consulta."));

                // Executa a requisição e espera o status 403.
                mockMvc.perform(post("/prontuarios/{idUsuario}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve buscar um prontuário e retornar status 200 OK")
        void buscarProntuario_comSucesso() throws Exception {
                // Prepara o modelo HATEOAS esperado.
                EntityModel<ConsultaVO> consultaModel = EntityModel.of(consultaVO);
                // Simula o serviço e o assembler.
                when(consultaService.buscarConsultaVO(1L, 10L)).thenReturn(consultaVO);
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa a requisição e verifica a resposta.
                mockMvc.perform(get("/consulta/{idUsuario}/{idConsulta}", 1L, 10L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.numero", is("PRT-001")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar consulta com usuário inexistente")
        void buscarProntuario_quandoUsuarioNaoEncontrado() throws Exception {
                // Simula o serviço lançando uma exceção porque o usuário não foi encontrado.
                when(consultaService.buscarConsultaVO(99L, 10L))
                                .thenThrow(new RuntimeException("Usuário não encontrado."));

                // Executa a requisição e espera o status 404.
                mockMvc.perform(get("/prontuarios/{idUsuario}/{idConsulta}", 99L, 10L))
                                .andExpect(status().isNotFound());
        }
}