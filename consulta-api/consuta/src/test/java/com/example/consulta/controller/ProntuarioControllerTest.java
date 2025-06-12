package com.example.consulta.controller;

import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.hateoas.ProntuarioModelAssembler;
import com.example.consulta.service.ProntuarioService;
import com.example.consulta.vo.EntradaProntuarioVO;
import com.example.consulta.vo.ProntuarioVO;
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

@WebMvcTest(ProntuarioController.class)
@DisplayName("Testes do Controller de Prontuários (API)")
class ProntuarioControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ProntuarioService prontuarioService;
        @MockBean
        private ProntuarioModelAssembler assembler;

        private ProntuarioVO prontuarioVO;
        private ProntuarioRequestDTO prontuarioRequestDTO;

        @BeforeEach
        void setUp() {
                EntradaProntuarioVO entradaVO = new EntradaProntuarioVO(1L, LocalDateTime.now(), "Diagnóstico",
                                "Tratamento", "Obs");
                prontuarioVO = new ProntuarioVO(10L, "PRT-001", Collections.singletonList(entradaVO));
                prontuarioRequestDTO = new ProntuarioRequestDTO("PRT-001");
        }

        @Test
        @DisplayName("Deve criar um prontuário e retornar status 201 Created")
        void criarProntuario_comSucesso() throws Exception {
                // Prepara o modelo HATEOAS esperado.
                EntityModel<ProntuarioVO> prontuarioModel = EntityModel.of(prontuarioVO);
                // Simula o serviço retornando o VO com sucesso.
                when(prontuarioService.criarProntuario(eq(1L), any(ProntuarioRequestDTO.class)))
                                .thenReturn(prontuarioVO);
                // Simula o assembler convertendo o VO.
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa a requisição POST e verifica o resultado.
                mockMvc.perform(post("/prontuarios/{idUsuario}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Deve retornar status 403 Forbidden ao tentar criar prontuário com usuário não-médico")
        void criarProntuario_quandoUsuarioNaoEhMedico() throws Exception {
                // Simula o serviço lançando uma exceção de permissão.
                when(prontuarioService.criarProntuario(eq(2L), any(ProntuarioRequestDTO.class)))
                                .thenThrow(new IllegalArgumentException("Apenas médicos podem criar prontuários."));

                // Executa a requisição e espera o status 403.
                mockMvc.perform(post("/prontuarios/{idUsuario}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve buscar um prontuário e retornar status 200 OK")
        void buscarProntuario_comSucesso() throws Exception {
                // Prepara o modelo HATEOAS esperado.
                EntityModel<ProntuarioVO> prontuarioModel = EntityModel.of(prontuarioVO);
                // Simula o serviço e o assembler.
                when(prontuarioService.buscarProntuario(1L, 10L)).thenReturn(prontuarioVO);
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa a requisição e verifica a resposta.
                mockMvc.perform(get("/prontuarios/{idUsuario}/{idProntuario}", 1L, 10L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.numero", is("PRT-001")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar prontuário com usuário inexistente")
        void buscarProntuario_quandoUsuarioNaoEncontrado() throws Exception {
                // Simula o serviço lançando uma exceção porque o usuário não foi encontrado.
                when(prontuarioService.buscarProntuario(99L, 10L))
                                .thenThrow(new RuntimeException("Usuário não encontrado."));

                // Executa a requisição e espera o status 404.
                mockMvc.perform(get("/prontuarios/{idUsuario}/{idProntuario}", 99L, 10L))
                                .andExpect(status().isNotFound());
        }
}