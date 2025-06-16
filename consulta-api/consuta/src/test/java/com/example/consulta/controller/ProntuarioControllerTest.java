package com.example.consulta.controller;

import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.hateoas.ProntuarioModelAssembler;
import com.example.consulta.service.ProntuarioService;
import com.example.consulta.vo.ProntuarioVO;
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

@WebMvcTest(ProntuarioController.class)
@DisplayName("Testes do Controller de Prontuário (API)")
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
        private EntityModel<ProntuarioVO> prontuarioModel;

        @BeforeEach
        void setUp() {
                prontuarioVO = new ProntuarioVO(1L, LocalDateTime.now(), "AGENDADA", "Rotina", null, null, null, null);
                prontuarioRequestDTO = new ProntuarioRequestDTO(LocalDateTime.now().plusDays(5), "AGENDADA", "Rotina",
                                1L,
                                1L, null,
                                null);
                prontuarioModel = EntityModel.of(prontuarioVO,
                                linkTo(methodOn(ProntuarioController.class).buscarPorId(1L)).withSelfRel());
        }

        @Test
        @DisplayName("Deve salvar um novo prontuário e retornar status 201 Created")
        void salvar() throws Exception {
                // Mock do serviço para retornar prontuarioVO ao salvar
                when(prontuarioService.salvar(any(ProntuarioRequestDTO.class))).thenReturn(prontuarioVO);

                // Mock do assembler para retornar modelo HATEOAS com links
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa POST para criar prontuário
                mockMvc.perform(post("/prontuario")
                                // Define content type como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte DTO para JSON no body
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                // Verifica status 201 Created
                                .andExpect(status().isCreated())
                                // Verifica Location header com URI do recurso criado
                                .andExpect(header().string("Location", containsString("/prontuario/1")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar salvar com dependência inexistente")
        void salvar_quandoDependenciaNaoEncontrada() throws Exception {
                // Simula o serviço lançando uma exceção porque uma dependência não foi
                // encontrada.
                when(prontuarioService.salvar(any(ProntuarioRequestDTO.class)))
                                .thenThrow(new RuntimeException("Paciente não encontrado"));

                // Executa a requisição POST e espera um erro 404.
                mockMvc.perform(post("/prontuario")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar prontuário inexistente")
        void atualizar_quandoNaoEncontrado() throws Exception {
                // Configura o mock do serviço para simular um prontuário não encontrado
                when(prontuarioService.atualizar(eq(99L), any(ProntuarioRequestDTO.class)))
                                .thenReturn(Optional.empty());

                // Envia requisição PUT para atualizar prontuário com ID 99
                mockMvc.perform(put("/prontuario/{id}", 99L)
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte e envia o DTO no corpo da requisição
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                // Verifica se retornou status 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve buscar um prontuário por ID e retornar status 200 OK")
        void buscarPorId_quandoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar um prontuário com ID 1
                when(prontuarioService.buscarPorId(1L)).thenReturn(Optional.of(prontuarioVO));
                // Configura o mock do assembler para converter o prontuário em modelo HATEOAS
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa uma requisição GET para buscar o prontuário por ID
                mockMvc.perform(get("/prontuario/{id}", 1L))
                                // Verifica se o status da resposta é 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o ID no response é igual a 1
                                .andExpect(jsonPath("$.id").value(1L))
                                // Verifica se o status do prontuário é "AGENDADA"
                                .andExpect(jsonPath("$.status").value("AGENDADA"));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao buscar prontuário inexistente")
        void buscarPorId_quandoNaoEncontrado() throws Exception {
                // Configura o mock para retornar Optional vazio quando buscar prontuário com ID
                // 99
                when(prontuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

                // Executa uma requisição GET para buscar prontuário com ID 99
                mockMvc.perform(get("/prontuario/{id}", 99L))
                                // Verifica se o status da resposta é 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve listar todos os prontuários e retornar status 200 OK")
        void listarTodas() throws Exception {
                // Configuração do mock do serviço
                List<ProntuarioVO> prontuarios = List.of(prontuarioVO);
                when(prontuarioService.listarTodos()).thenReturn(prontuarios);

                // Mock do método toModel do assembler para qualquer prontuarioVO
                when(assembler.toModel(any(ProntuarioVO.class))).thenReturn(prontuarioModel);

                // Teste simplificado que verifica apenas o status
                mockMvc.perform(get("/prontuario"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve excluir um prontuário existente e retornar status 204 No Content")
        void deletar_quandoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar true ao deletar prontuário com ID 1
                when(prontuarioService.deletar(1L)).thenReturn(true);

                // Executa requisição DELETE para o endpoint /prontuario/1
                mockMvc.perform(delete("/prontuario/{id}", 1L))
                                // Verifica se o status da resposta é 204 No Content
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar excluir prontuário inexistente")
        void deletar_quandoNaoEncontrado() throws Exception {
                // Configura o mock do serviço para retornar falso ao tentar deletar prontuário
                // inexistente
                when(prontuarioService.deletar(99L)).thenReturn(false);

                // Executa requisição DELETE para o endpoint /prontuario/99
                mockMvc.perform(delete("/prontuario/{id}", 99L))
                                // Verifica se o status da resposta é 404 Not Found
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve atualizar um prontuário existente e retornar status 200 OK")
        void atualizar_quandoEncontrado() throws Exception {
                // Configura mock do serviço para retornar um prontuário quando atualizar for
                // chamado
                when(prontuarioService.atualizar(eq(1L), any(ProntuarioRequestDTO.class)))
                                .thenReturn(Optional.of(prontuarioVO));
                // Configura mock do assembler para converter o prontuário em modelo HATEOAS
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa requisição PUT para atualizar prontuário
                mockMvc.perform(put("/prontuario/{id}", 1L)
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte o objeto para JSON e define como corpo da requisição
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                // Verifica se retorna status 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o ID no response está correto
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request ao enviar dados inválidos")
        void salvar_quandoDadosInvalidos() throws Exception {
                // Cria um objeto prontuarioRequestDTO com dados inválidos para teste
                ProntuarioRequestDTO requestInvalido = new ProntuarioRequestDTO(
                                null, "", "Rotina", null, null, null, null);

                // Executa requisição POST para o endpoint /prontuario com o objeto inválido
                mockMvc.perform(post("/prontuario")
                                // Define o tipo de conteúdo como JSON
                                .contentType(MediaType.APPLICATION_JSON)
                                // Converte o objeto para JSON e define como corpo da requisição
                                .content(objectMapper.writeValueAsString(requestInvalido)))
                                // Verifica se retorna status 400 Bad Request
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar o corpo da resposta correto ao salvar prontuário")
        void salvar_verificarCorpoResposta() throws Exception {
                // Mock do serviço para retornar um prontuário quando salvar for chamado
                when(prontuarioService.salvar(any(ProntuarioRequestDTO.class))).thenReturn(prontuarioVO);
                // Mock do assembler para converter o prontuário em modelo HATEOAS
                when(assembler.toModel(prontuarioVO)).thenReturn(prontuarioModel);

                // Executa POST para criar prontuário e verifica:
                mockMvc.perform(post("/prontuario")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuarioRequestDTO)))
                                .andExpect(status().isCreated()) // - Status 201 Created
                                .andExpect(jsonPath("$.id").value(1L)) // - ID correto no response
                                .andExpect(jsonPath("$.status").value("AGENDADA")) // - Status do prontuário correto
                                .andExpect(jsonPath("$.nomeConsulta").value("Rotina")); // - Nome do prontuário correto
        }

        @Test
        @DisplayName("Deve incluir links HATEOAS na resposta")
        void buscarPorId_verificarLinks() throws Exception {
                // Configura o mock para retornar um prontuário quando buscar por ID 1
                when(prontuarioService.buscarPorId(1L)).thenReturn(Optional.of(prontuarioVO));
                // Configura o mock do assembler para retornar um EntityModel com links HATEOAS
                when(assembler.toModel(prontuarioVO)).thenReturn(
                                EntityModel.of(prontuarioVO,
                                                // Adiciona link self referenciando o próprio prontuário
                                                linkTo(methodOn(ProntuarioController.class).buscarPorId(1L))
                                                                .withSelfRel(),
                                                // Adiciona link para listar todos os prontuários
                                                linkTo(methodOn(ProntuarioController.class).listarTodas())
                                                                .withRel("prontuarios"),
                                                // Adiciona link para deletar o prontuário
                                                linkTo(methodOn(ProntuarioController.class).deletar(1L))
                                                                .withRel("delete")));

                // Executa GET request para buscar prontuário por ID
                mockMvc.perform(get("/prontuario/{id}", 1L))
                                // Verifica se retornou status 200 OK
                                .andExpect(status().isOk())
                                // Verifica se o link self está presente na resposta
                                .andExpect(jsonPath("$._links.self.href").exists())
                                // Verifica se o link prontuarios está presente na resposta
                                .andExpect(jsonPath("$._links.prontuarios.href").exists())
                                // Verifica se o link delete está presente na resposta
                                .andExpect(jsonPath("$._links.delete.href").exists());
        }

        @Test
        @DisplayName("Deve retornar status 405 quando o método HTTP não é permitido")
        void metodoNaoPermitido() throws Exception {
                // Tenta fazer uma requisição PATCH para /prontuario/1
                mockMvc.perform(patch("/prontuario/1"))
                                // Verifica se retorna status 405 Method Not Allowed
                                .andExpect(status().isMethodNotAllowed());
        }
}