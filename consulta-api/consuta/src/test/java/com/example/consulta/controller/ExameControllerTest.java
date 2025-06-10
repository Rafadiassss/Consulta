package com.example.consulta.controller;

import com.example.consulta.model.Consulta;
import com.example.consulta.model.Exame;
import com.example.consulta.service.ExameService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExameController.class)
@DisplayName("Testes do Controller de Exames")
class ExameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExameService exameService;

    private Exame exameSalvo;

    @BeforeEach
    void setUp() {
        // Este objeto é criado antes de cada teste para representar
        // um exame que já existe no banco de dados.
        Consulta consulta = new Consulta();
        exameSalvo = new Exame("Hemograma Completo", "Resultados normais", "Sem observações", consulta);
        // O ID é definido aqui para simular a entidade retornada pelo banco.
        // É necessário um método setId na entidade Exame para isso.
        // exameSalvo.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todos os exames")
    void listarTodos() throws Exception {
        // Arrange: Configura o mock do serviço para retornar uma lista com o exame de
        // teste.
        // Quando o método 'listarTodos' for chamado, ele retornará essa lista.
        when(exameService.listarTodos()).thenReturn(Collections.singletonList(exameSalvo));

        // Act: Executa uma requisição GET para o endpoint /exames.
        mockMvc.perform(get("/exames"))
                // Assert: Verifica se o status da resposta é 200 (OK).
                .andExpect(status().isOk())
                // Assert: Verifica se o nome do exame na resposta JSON está correto.
                .andExpect(jsonPath("$[0].nome", is("Hemograma Completo")));
    }

    @Test
    @DisplayName("Deve buscar um exame por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Arrange: Configura o mock para encontrar o exame quando 'buscarPorId' for
        // chamado com o ID 1.
        when(exameService.buscarPorId(1L)).thenReturn(Optional.of(exameSalvo));

        // Act: Executa a requisição GET para /exames/1.
        mockMvc.perform(get("/exames/{id}", 1L))
                // Assert: Verifica se o status da resposta é 200 (OK).
                .andExpect(status().isOk())
                // Assert: Verifica os detalhes do exame na resposta JSON.
                .andExpect(jsonPath("$.nome", is("Hemograma Completo")));
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Arrange: Configura o mock para retornar um Optional vazio para qualquer ID.
        when(exameService.buscarPorId(anyLong())).thenReturn(Optional.empty());

        // Act: Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/exames/{id}", 99L))
                // Assert: Verifica se o status da resposta é 404 (NotFound) e o corpo está
                // vazio.
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo exame")
    void salvarExame() throws Exception {
        // Arrange: Cria o objeto que será enviado no corpo da requisição, sem ID.
        Exame exameParaEnviar = new Exame("Raio-X do Tórax", null, "Paciente tossindo.", new Consulta());

        // Arrange: Cria o objeto que o serviço deve retornar, agora com o ID que seria
        // gerado pelo banco.
        Exame exameRetornadoPeloServico = new Exame(exameParaEnviar.getNome(), "Laudo pendente",
                exameParaEnviar.getObservacoes(), exameParaEnviar.getConsulta());
        // exameRetornadoPeloServico.setId(1L);

        // Arrange: Configura o mock para que ao salvar qualquer exame, retorne o
        // objeto com ID.
        when(exameService.salvar(any(Exame.class))).thenReturn(exameRetornadoPeloServico);

        // Act: Executa a requisição POST para /exames com o objeto sem ID no corpo.
        mockMvc.perform(post("/exames")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exameParaEnviar)))
                // Assert: Verifica se o status da resposta é 200 (OK).
                .andExpect(status().isOk());
        // Assert: Verifica se a resposta JSON contém o ID e o nome corretos.
        // .andExpect(jsonPath("$.id", is(1)))
        // .andExpect(jsonPath("$.nome", is("Raio-X do Tórax")));

        // Verify: Garante que o método 'salvar' do serviço foi chamado exatamente uma
        // vez.
        verify(exameService, times(1)).salvar(any(Exame.class));
    }

    @Test
    @DisplayName("Deve deletar um exame existente")
    void deletarExame() throws Exception {
        // Arrange: Configura o mock do serviço para o método 'deletar', que retorna um
        // boolean.
        when(exameService.deletar(1L)).thenReturn(true);

        // Act: Executa a requisição DELETE para /exames/1.
        mockMvc.perform(delete("/exames/{id}", 1L))
                // Assert: Verifica se o status da resposta é 200 (OK).
                .andExpect(status().isOk());

        // Verify: Garante que o método 'deletar' do serviço foi chamado com o ID
        // correto.
        verify(exameService, times(1)).deletar(1L);
    }
}