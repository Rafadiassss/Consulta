package com.example.consulta.controller;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.hateoas.PagamentoModelAssembler;
import com.example.consulta.service.PagamentoService;
import com.example.consulta.vo.PagamentoVO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
        pagamentoVO = new PagamentoVO(1L, LocalDate.now(), new BigDecimal("150.00"), "PIX", "CONFIRMADO", 10L);
        pagamentoRequestDTO = new PagamentoRequestDTO(LocalDate.now(), new BigDecimal("150.00"), "PIX", "PENDENTE",
                10L);
        pagamentoModel = EntityModel.of(pagamentoVO,
                linkTo(methodOn(PagamentoController.class).buscarPorId(1L)).withSelfRel());
    }

    @Test
    @DisplayName("Deve buscar um pagamento por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando o pagamento.
        when(pagamentoService.buscarPorId(1L)).thenReturn(Optional.of(pagamentoVO));
        // Simula o assembler convertendo o VO para o modelo HATEOAS.
        when(assembler.toModel(pagamentoVO)).thenReturn(pagamentoModel);

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/pagamentos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pagamentos/1")));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o pagamento.
        when(pagamentoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e verifica o 404.
        mockMvc.perform(get("/pagamentos/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar um novo pagamento e retornar status 201 Created")
    void salvar() throws Exception {
        // Simula o serviço salvando e retornando o VO.
        when(pagamentoService.salvar(any(PagamentoRequestDTO.class))).thenReturn(pagamentoVO);
        // Simula o assembler.
        when(assembler.toModel(pagamentoVO)).thenReturn(pagamentoModel);

        // Executa a requisição POST.
        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/pagamentos/1")));
    }

    @Test
    @DisplayName("Deve atualizar um pagamento existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Simula o serviço e o assembler.
        when(pagamentoService.atualizar(eq(1L), any(PagamentoRequestDTO.class))).thenReturn(Optional.of(pagamentoVO));
        when(assembler.toModel(pagamentoVO)).thenReturn(pagamentoModel);

        // Executa a requisição PUT.
        mockMvc.perform(put("/pagamentos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}