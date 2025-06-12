package com.example.consulta.controller;

import com.example.consulta.dto.SecretariaRequestDTO;
import com.example.consulta.dto.SecretariaResponseDTO;
import com.example.consulta.service.SecretariaService;
import com.example.consulta.vo.SecretariaVO;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Este teste não usa HATEOAS, então não mockamos o Assembler.
@WebMvcTest(SecretariaController.class)
@DisplayName("Testes do Controller de Secretarias (API sem HATEOAS)")
class SecretariaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecretariaService secretariaService;

    private SecretariaVO secretariaVO;
    private SecretariaRequestDTO secretariaRequestDTO;
    private SecretariaResponseDTO secretariaResponseDTO;

    @BeforeEach
    void setUp() {
        // Usando um número de CPF matematicamente válido para passar na
        // validação @CPF.
        String cpfValidoParaTeste = "705.503.340-97"; // Geradores online podem fornecer CPFs válidos para teste.

        secretariaVO = new SecretariaVO(1L, "Ana Silva", cpfValidoParaTeste, "11987654321", "ana@email.com",
                "ana.silva");
        secretariaRequestDTO = new SecretariaRequestDTO("Ana Silva", cpfValidoParaTeste, "11987654321", "ana@email.com",
                "ana.silva", "senha123");
        secretariaResponseDTO = new SecretariaResponseDTO(1L, "Ana Silva", cpfValidoParaTeste, "11987654321",
                "ana@email.com", "ana.silva");
    }

    @Test
    @DisplayName("Deve listar todas as secretarias e retornar 200 OK")
    void listar() throws Exception {
        when(secretariaService.listarTodas()).thenReturn(Collections.singletonList(secretariaVO));
        mockMvc.perform(get("/secretarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Ana Silva")));
    }

    @Test
    @DisplayName("Deve buscar por ID existente e retornar 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        when(secretariaService.buscarPorId(1L)).thenReturn(Optional.of(secretariaVO));
        mockMvc.perform(get("/secretarias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        when(secretariaService.buscarPorId(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/secretarias/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar uma nova secretaria com dados válidos e retornar 201 Created")
    void salvar_comDadosValidos() throws Exception {
        // Simula o serviço retornando o VO.
        when(secretariaService.salvar(any(SecretariaRequestDTO.class))).thenReturn(secretariaVO);

        // Executa a requisição POST com o DTO contendo o CPF válido.
        mockMvc.perform(post("/secretarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secretariaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/secretarias/1")))
                .andExpect(jsonPath("$.cpf", is(secretariaVO.cpf())));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar salvar com e-mail vazio")
    void salvar_comEmailVazio() throws Exception {
        // Cria um DTO com dados inválidos (e-mail em branco).
        SecretariaRequestDTO dtoInvalido = new SecretariaRequestDTO("Nome Valido", "111.222.333-44", null, "", "user",
                "senha");

        // Executa a requisição e espera um erro de validação (400).
        mockMvc.perform(post("/secretarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar uma secretaria existente e retornar 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando o VO atualizado.
        when(secretariaService.atualizar(eq(1L), any(SecretariaRequestDTO.class)))
                .thenReturn(Optional.of(secretariaVO));

        // Executa a requisição PUT com o DTO contendo o CPF válido.
        mockMvc.perform(put("/secretarias/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secretariaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Ana Silva")));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar atualizar secretaria inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        when(secretariaService.atualizar(eq(99L), any(SecretariaRequestDTO.class))).thenReturn(Optional.empty());
        mockMvc.perform(put("/secretarias/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secretariaRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar secretaria existente e retornar 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        when(secretariaService.deletar(1L)).thenReturn(true);
        mockMvc.perform(delete("/secretarias/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar secretaria inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        when(secretariaService.deletar(99L)).thenReturn(false);
        mockMvc.perform(delete("/secretarias/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}