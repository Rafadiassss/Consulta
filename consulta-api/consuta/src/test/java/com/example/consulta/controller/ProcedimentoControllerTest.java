package com.example.consulta.controller;

import com.example.consulta.model.Procedimento;
import com.example.consulta.service.ProcedimentoService;
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

@WebMvcTest(ProcedimentoController.class)
@DisplayName("Testes do Controller de Procedimentos")
class ProcedimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcedimentoService procedimentoService;

    private Procedimento procedimento;

    @BeforeEach
    void setUp() {
        // Objeto base que simula um procedimento já existente no banco de dados.
        procedimento = new Procedimento();
        procedimento.setId(1L);
        procedimento.setNome("Consulta Padrão");
        procedimento.setDescricao("Consulta médica de rotina.");
        procedimento.setValor(250.00);
    }

    @Test
    @DisplayName("Deve listar todos os procedimentos")
    void listarTodos() throws Exception {
        // Configura o mock do serviço para retornar uma lista com o procedimento de
        // teste.
        when(procedimentoService.listarTodos()).thenReturn(Collections.singletonList(procedimento));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/procedimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Consulta Padrão")));
    }

    @Test
    @DisplayName("Deve buscar um procedimento por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Configura o mock para encontrar o procedimento com ID 1.
        when(procedimentoService.buscarPorId(1L)).thenReturn(Optional.of(procedimento));

        // Executa a requisição GET e verifica os dados retornados.
        mockMvc.perform(get("/procedimentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.valor", is(250.00)));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar o procedimento com ID 99.
        when(procedimentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/procedimentos/{id}", 99L))
                // Por padrão, Spring retorna 200 OK com corpo vazio para um Optional.empty().
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo procedimento")
    void salvarProcedimento() throws Exception {
        // Configura o mock do serviço para retornar o procedimento salvo.
        when(procedimentoService.salvar(any(Procedimento.class))).thenReturn(procedimento);

        // Executa a requisição POST com um objeto no corpo.
        mockMvc.perform(post("/procedimentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(procedimento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Consulta Padrão")));

        // Garante que o método 'salvar' do serviço foi chamado.
        verify(procedimentoService, times(1)).salvar(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve deletar um procedimento existente")
    void deletarProcedimento() throws Exception {
        // Configura o mock do serviço para o método 'deletar', que é void.
        doNothing().when(procedimentoService).deletar(1L);

        // Executa a requisição DELETE para /procedimentos/1.
        mockMvc.perform(delete("/procedimentos/{id}", 1L))
                // Um método de controller 'void' retorna 200 OK por padrão.
                .andExpect(status().isOk());

        // Garante que o método 'deletar' do serviço foi chamado com o ID correto.
        verify(procedimentoService, times(1)).deletar(1L);
    }
}