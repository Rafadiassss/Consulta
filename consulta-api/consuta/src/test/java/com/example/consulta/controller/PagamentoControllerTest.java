package com.example.consulta.controller;

import com.example.consulta.model.Pagamento;
import com.example.consulta.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
@DisplayName("Testes do Controller de Pagamentos")
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagamentoService pagamentoService;

    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        // Objeto base que simula um pagamento já existente no banco.
        pagamento = new Pagamento(
                LocalDate.of(2025, 6, 10),
                new BigDecimal("250.00"),
                "Cartão de Crédito",
                "CONFIRMADO");
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void listarTodos() throws Exception {
        // Configura o mock do serviço para retornar uma lista com o pagamento de teste.
        when(pagamentoService.listarTodos()).thenReturn(Collections.singletonList(pagamento));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formaPagamento", is("Cartão de Crédito")))
                .andExpect(jsonPath("$[0].valorPago", is(250.00)));
    }

    @Test
    @DisplayName("Deve buscar um pagamento por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Configura o mock para encontrar o pagamento com ID 1.
        when(pagamentoService.buscarPorId(1L)).thenReturn(Optional.of(pagamento));

        // Executa a requisição GET e verifica os dados retornados.
        mockMvc.perform(get("/pagamentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar o pagamento com ID 99.
        when(pagamentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/pagamentos/{id}", 99L))
                // Por padrão, Spring retorna 404 NotFound com corpo vazio
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo pagamento")
    void salvarPagamento() throws Exception {
        // Objeto que será retornado pelo serviço, simulando que o ID foi gerado.
        Pagamento pagamentoSalvo = new Pagamento(
                pagamento.getDataPagamento(),
                pagamento.getValorPago(),
                pagamento.getFormaPagamento(),
                pagamento.getStatus());

        // Configura o mock do serviço para retornar o pagamento salvo.
        when(pagamentoService.salvar(any(Pagamento.class))).thenReturn(pagamentoSalvo);

        // Executa a requisição POST com o objeto pagamento no corpo.
        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamento)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formaPagamento", is("Cartão de Crédito")));
    }
}