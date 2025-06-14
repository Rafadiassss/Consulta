package com.example.consulta.controller;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.hateoas.PagamentoModelAssembler;
import com.example.consulta.service.PagamentoService;
import com.example.consulta.vo.PagamentoVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Adicionado para LocalDate
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
@DisplayName("Testes do Controller de Pagamentos (API com HATEOAS)")
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagamentoService pagamentoService;
    @MockBean
    private PagamentoModelAssembler assembler;

    private PagamentoVO pagamentoVO;
    private PagamentoRequestDTO pagamentoRequestDTO;
    private EntityModel<PagamentoVO> pagamentoModel;

    @BeforeEach
    void setUp() {
        // Configura o ObjectMapper para lidar com tipos de data do Java 8
        objectMapper.registerModule(new JavaTimeModule());

        pagamentoVO = new PagamentoVO(1L, LocalDate.now(), new BigDecimal("150.00"), "PIX", "CONFIRMADO", 10L);

        pagamentoRequestDTO = new PagamentoRequestDTO(LocalDate.now(), new BigDecimal("150.00"), "PIX", "PENDENTE");

        pagamentoModel = EntityModel.of(pagamentoVO,
                linkTo(methodOn(PagamentoController.class).buscarPorId(1L)).withSelfRel());

        // Mock padrão para o assembler
        when(assembler.toModel(any(PagamentoVO.class)))
                .thenAnswer(invocation -> {
                    PagamentoVO vo = invocation.getArgument(0);
                    return EntityModel.of(vo,
                            linkTo(methodOn(PagamentoController.class).buscarPorId(vo.id())).withSelfRel());
                });
    }

    @Test
    @DisplayName("Deve buscar um pagamento por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Mock do serviço para retornar um pagamento quando buscado pelo ID 1
        when(pagamentoService.buscarPorId(1L)).thenReturn(Optional.of(pagamentoVO));

        // Executa a requisição GET e verifica a resposta.
        // Faz uma requisição GET para /pagamentos/1
        // Verifica se o status da resposta é 200 OK
        // Verifica se o status do pagamento na resposta é "CONFIRMADO"
        // Verifica se o link HATEOAS self contém "/pagamentos/1"
        mockMvc.perform(get("/pagamentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pagamentos/1")));

        // Verifica se o método buscarPorId do serviço foi chamado exatamente uma vez
        // com ID 1
        verify(pagamentoService, times(1)).buscarPorId(1L);
        // Verifica se o assembler foi chamado uma vez para converter o pagamentoVO para
        // modelo HATEOAS
        verify(assembler, times(1)).toModel(pagamentoVO);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o pagamento.
        when(pagamentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e verifica o 404.
        mockMvc.perform(get("/pagamentos/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(pagamentoService, times(1)).buscarPorId(99L);
        verify(assembler, never()).toModel(any(PagamentoVO.class)); // Não deve chamar o assembler se não encontrou
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos e retornar status 200 OK")
    void listar_comSucesso() throws Exception {
        // Cria um segundo pagamento para teste
        PagamentoVO pagamentoVO2 = new PagamentoVO(2L, LocalDate.now().minusDays(1), BigDecimal.valueOf(50.00),
                "Boleto", "Pendente", 11L);
        // Cria lista com os dois pagamentos
        List<PagamentoVO> pagamentos = Arrays.asList(pagamentoVO, pagamentoVO2);

        // Mock do serviço retornando a lista de pagamentos
        when(pagamentoService.listarTodos()).thenReturn(pagamentos);

        // Executa GET e valida resposta
        mockMvc.perform(get("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verifica tamanho da lista
                .andExpect(jsonPath("$._embedded.pagamentoVOList", hasSize(2)))
                // Valida campos do primeiro pagamento
                .andExpect(jsonPath("$._embedded.pagamentoVOList[0].id", is(pagamentoVO.id().intValue())))
                .andExpect(
                        jsonPath("$._embedded.pagamentoVOList[0].valorPago", is(pagamentoVO.valorPago().doubleValue())))
                .andExpect(jsonPath("$._embedded.pagamentoVOList[0].formaPagamento", is(pagamentoVO.formaPagamento())))
                .andExpect(jsonPath("$._embedded.pagamentoVOList[0].status", is(pagamentoVO.status())))
                // Valida ID do segundo pagamento
                .andExpect(jsonPath("$._embedded.pagamentoVOList[1].id", is(pagamentoVO2.id().intValue())))
                // Verifica link HATEOAS
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/pagamentos")));

        // Verifica chamadas aos mocks
        verify(pagamentoService, times(1)).listarTodos();
        verify(assembler, times(2)).toModel(any(PagamentoVO.class)); // Chamado para cada VO na lista
    }

    @Test
    @DisplayName("Deve retornar lista vazia de pagamentos com status 200 OK quando não houver pagamentos")
    void listar_quandoVazio() throws Exception {
        // Given
        when(pagamentoService.listarTodos()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist()) // No _embedded for empty lists
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/pagamentos")));

        verify(pagamentoService, times(1)).listarTodos();
        verify(assembler, never()).toModel(any(PagamentoVO.class)); // Não deve chamar o assembler se a lista estiver
                                                                    // vazia
    }

    @Test
    @DisplayName("Deve salvar um novo pagamento e retornar status 201 Created")
    void salvar_comSucesso() throws Exception {
        // Simula o serviço salvando e retornando o VO.
        when(pagamentoService.salvar(any(PagamentoRequestDTO.class))).thenReturn(pagamentoVO);

        // Executa a requisição POST.
        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/pagamentos/1")));

        verify(pagamentoService, times(1)).salvar(any(PagamentoRequestDTO.class));
        verify(assembler, times(1)).toModel(pagamentoVO);
    }

    @Test
    @DisplayName("Deve atualizar um pagamento existente e retornar status 200 OK")
    void atualizar_comSucesso() throws Exception {

        // Simula o serviço atualizando e retornando o pagamento atualizado com sucesso
        when(pagamentoService.atualizar(eq(1L), any(PagamentoRequestDTO.class))).thenReturn(Optional.of(pagamentoVO));

        // Executa a requisição PUT.
        mockMvc.perform(put("/pagamentos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        // Verifica se o método atualizar foi chamado exatamente uma vez com os
        // parâmetros corretos
        verify(pagamentoService, times(1)).atualizar(eq(1L), any(PagamentoRequestDTO.class));
        // Verifica se o assembler converteu o pagamentoVO para modelo HATEOAS uma única
        // vez
        verify(assembler, times(1)).toModel(pagamentoVO);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar pagamento inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {

        // Simula o serviço retornando Optional.empty() ao tentar atualizar um pagamento
        // inexistente
        when(pagamentoService.atualizar(eq(99L), any(PagamentoRequestDTO.class))).thenReturn(Optional.empty());

        // Executa a requisição PUT e verifica se retorna 404 Not Found
        mockMvc.perform(put("/pagamentos/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                .andExpect(status().isNotFound());

        // Verifica se o serviço foi chamado uma vez e se o assembler nunca foi chamado
        verify(pagamentoService, times(1)).atualizar(eq(99L), any(PagamentoRequestDTO.class));
        verify(assembler, never()).toModel(any(PagamentoVO.class));
    }

    @Test
    @DisplayName("Deve deletar pagamento e retornar status 204 No Content")
    void deletar_comSucesso() throws Exception {
        // Configura o mock do serviço para retornar true ao deletar o pagamento com ID
        // 1
        when(pagamentoService.deletar(1L)).thenReturn(true);

        // Executa uma requisição DELETE para /pagamentos/1 e verifica se retorna status
        // 204 (No Content)
        mockMvc.perform(delete("/pagamentos/{id}", 1L))
                .andExpect(status().isNoContent());

        // Verifica se o método deletar do serviço foi chamado exatamente uma vez com o
        // ID 1
        verify(pagamentoService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar pagamento inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {

        when(pagamentoService.deletar(99L)).thenReturn(false);

        mockMvc.perform(delete("/pagamentos/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(pagamentoService, times(1)).deletar(99L);
    }
}
