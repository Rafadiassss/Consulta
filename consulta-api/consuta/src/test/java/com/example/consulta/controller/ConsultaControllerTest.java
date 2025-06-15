package com.example.consulta.controller;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.hateoas.ConsultaModelAssembler;
import com.example.consulta.service.ConsultaService;
import com.example.consulta.vo.ConsultaVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
@DisplayName("Testes do Controller de Consultas (API)")
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
        private EntityModel<ConsultaVO> consultaModel;

        @BeforeEach
        void setUp() {
                consultaVO = new ConsultaVO(1L, LocalDateTime.now(), "AGENDADA", "Rotina", null, null, null, null);
                consultaRequestDTO = new ConsultaRequestDTO(LocalDateTime.now().plusDays(5), "AGENDADA", "Rotina", 1L,
                                1L, null,
                                null);
                consultaModel = EntityModel.of(consultaVO,
                                linkTo(methodOn(ConsultaController.class).buscarPorId(1L)).withSelfRel());
        }

        @Test
        @DisplayName("Deve salvar uma nova consulta e retornar status 201 Created")
        void salvar() throws Exception {
                // Mock do serviço para retornar consultaVO ao salvar
                when(consultaService.salvar(any(ConsultaRequestDTO.class))).thenReturn(consultaVO);

                // Mock do assembler para retornar modelo HATEOAS com links
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa POST para criar consulta
                mockMvc.perform(post("/consultas")
                                // Define content type como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte DTO para JSON no body
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                // Verifica status 201 Created
                                .andExpect(status().isCreated())
                                // Verifica Location header com URI do recurso criado
                                .andExpect(header().string("Location", containsString("/consultas/1")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar salvar com dependência inexistente")
        void salvar_quandoDependenciaNaoEncontrada() throws Exception {
                // Simula o serviço lançando uma exceção porque uma dependência não foi
                // encontrada.
                when(consultaService.salvar(any(ConsultaRequestDTO.class)))
                                .thenThrow(new RuntimeException("Paciente não encontrado"));

                // Executa a requisição POST e espera um erro 404.
                mockMvc.perform(post("/consultas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar consulta inexistente")
        void atualizar_quandoNaoEncontrado() throws Exception {
                // Configura o mock do serviço para simular uma consulta não encontrada
                when(consultaService.atualizar(eq(99L), any(ConsultaRequestDTO.class))).thenReturn(Optional.empty());

                // Envia requisição PUT para atualizar consulta com ID 99
                mockMvc.perform(put("/consultas/{id}", 99L)
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte e envia o DTO no corpo da requisição
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                // Verifica se retornou status 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve buscar uma consulta por ID e retornar status 200 OK")
        void buscarPorId_quandoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar uma consulta com ID 1
                when(consultaService.buscarPorId(1L)).thenReturn(Optional.of(consultaVO));
                // Configura o mock do assembler para converter a consulta em modelo HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa uma requisição GET para buscar a consulta por ID
                mockMvc.perform(get("/consultas/{id}", 1L))
                                // Verifica se o status da resposta é 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o ID no response é igual a 1
                                .andExpect(jsonPath("$.id").value(1L))
                                // Verifica se o status da consulta é "AGENDADA"
                                .andExpect(jsonPath("$.status").value("AGENDADA"));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar consulta inexistente")
        void buscarPorId_quandoNaoEncontrado() throws Exception {
                // Configura o mock para retornar Optional vazio quando buscar consulta com ID
                // 99
                when(consultaService.buscarPorId(99L)).thenReturn(Optional.empty());

                // Executa uma requisição GET para buscar consulta com ID 99
                mockMvc.perform(get("/consultas/{id}", 99L))
                                // Verifica se o status da resposta é 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve listar todas as consultas e retornar status 200 OK")
        void listarTodas() throws Exception {
                // Configuração do mock do serviço
                List<ConsultaVO> consultas = List.of(consultaVO);
                when(consultaService.listarTodas()).thenReturn(consultas);

                // Mock do método toModel do assembler para qualquer ConsultaVO
                when(assembler.toModel(any(ConsultaVO.class))).thenReturn(consultaModel);

                // Teste simplificado que verifica apenas o status
                mockMvc.perform(get("/consultas"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve excluir uma consulta existente e retornar status 204 No Content")
        void deletar_quandoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar true ao deletar consulta com ID 1
                when(consultaService.deletar(1L)).thenReturn(true);

                // Executa requisição DELETE para o endpoint /consultas/1
                mockMvc.perform(delete("/consultas/{id}", 1L))
                                // Verifica se o status da resposta é 204 No Content
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar excluir consulta inexistente")
        void deletar_quandoNaoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar falso ao tentar deletar consulta
                // inexistente
                when(consultaService.deletar(99L)).thenReturn(false);

                // Executa requisição DELETE para o endpoint /consultas/99
                mockMvc.perform(delete("/consultas/{id}", 99L))
                                // Verifica se o status da resposta é 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve atualizar uma consulta existente e retornar status 200 OK")
        void atualizar_quandoEncontrado() throws Exception {
                // Configura mock do serviço para retornar uma consulta quando atualizar for
                // chamado
                when(consultaService.atualizar(eq(1L), any(ConsultaRequestDTO.class)))
                                .thenReturn(Optional.of(consultaVO));
                // Configura mock do assembler para converter a consulta em modelo HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa requisição PUT para atualizar consulta
                mockMvc.perform(put("/consultas/{id}", 1L)
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte o objeto para JSON e define como corpo da requisição
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                // Verifica se retorna status 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o ID no response está correto
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request ao enviar dados inválidos")
        void salvar_quandoDadosInvalidos() throws Exception {
                // Cria um objeto ConsultaRequestDTO com dados inválidos para teste
                ConsultaRequestDTO requestInvalido = new ConsultaRequestDTO(
                                null, "", "Rotina", null, null, null, null);

                // Executa requisição POST para o endpoint /consultas com o objeto inválido
                mockMvc.perform(post("/consultas")
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte o objeto para JSON e define como corpo da requisição
                                .content(objectMapper.writeValueAsString(requestInvalido)))
                                // Verifica se retorna status 400 Bad Request
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar o corpo da resposta correto ao salvar consulta")
        void salvar_verificarCorpoResposta() throws Exception {
                // Mock do serviço para retornar uma consulta quando salvar for chamado
                when(consultaService.salvar(any(ConsultaRequestDTO.class))).thenReturn(consultaVO);
                // Mock do assembler para converter a consulta em modelo HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(consultaModel);

                // Executa POST para criar consulta e verifica:
                mockMvc.perform(post("/consultas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(consultaRequestDTO)))
                                .andExpect(status().isCreated()) // - Status 201 Created
                                .andExpect(jsonPath("$.id").value(1L)) // - ID correto no response
                                .andExpect(jsonPath("$.status").value("AGENDADA")) // - Status da consulta correto
                                .andExpect(jsonPath("$.nomeConsulta").value("Rotina")); // - Nome da consulta correto
        }

        @Test
        @DisplayName("Deve incluir links HATEOAS na resposta")
        void buscarPorId_verificarLinks() throws Exception {
                // Configura o mock para retornar uma consulta quando buscar por ID 1
                when(consultaService.buscarPorId(1L)).thenReturn(Optional.of(consultaVO));
                // Configura o mock do assembler para retornar um EntityModel com links HATEOAS
                when(assembler.toModel(consultaVO)).thenReturn(
                                EntityModel.of(consultaVO,
                                                // Adiciona link self referenciando a própria consulta
                                                linkTo(methodOn(ConsultaController.class).buscarPorId(1L))
                                                                .withSelfRel(),
                                                // Adiciona link para listar todas as consultas
                                                linkTo(methodOn(ConsultaController.class).listarTodas())
                                                                .withRel("consultas"),
                                                // Adiciona link para deletar a consulta
                                                linkTo(methodOn(ConsultaController.class).deletar(1L))
                                                                .withRel("delete")));

                // Executa GET request para buscar consulta por ID
                mockMvc.perform(get("/consultas/{id}", 1L))
                                // Verifica se retornou status 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o link self está presente na resposta
                                .andExpect(jsonPath("$._links.self.href").exists())
                                // Verifica se o link consultas está presente na resposta
                                .andExpect(jsonPath("$._links.consultas.href").exists())
                                // Verifica se o link delete está presente na resposta
                                .andExpect(jsonPath("$._links.delete.href").exists());
        }

        @Test
        @DisplayName("Deve retornar status 405 quando o método HTTP não é permitido")
        void metodoNaoPermitido() throws Exception {
                // Tenta fazer uma requisição PATCH para /consultas/1
                mockMvc.perform(patch("/consultas/1"))
                                // Verifica se retorna status 405 Method Not Allowed
                                .andExpect(status().isMethodNotAllowed());
        }
}
