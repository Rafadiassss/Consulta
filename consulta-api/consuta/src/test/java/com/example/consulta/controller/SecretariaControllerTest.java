package com.example.consulta.controller;

import com.example.consulta.model.Secretaria;
import com.example.consulta.service.SecretariaService;
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

@WebMvcTest(SecretariaController.class)
@DisplayName("Testes do Controller de Secretarias")
class SecretariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecretariaService secretariaService;

    private Secretaria secretaria;

    @BeforeEach
    void setUp() {
        // Objeto base que simula uma secretaria já existente no banco de dados.
        secretaria = new Secretaria();
        secretaria.setId(1L);
        secretaria.setNome("Ana Silva");
        secretaria.setCpf("111.222.333-44");
        secretaria.setUsuario("ana.silva");
    }

    @Test
    @DisplayName("Deve listar todas as secretarias")
    void listarTodas() throws Exception {
        // Configura o mock do serviço para retornar uma lista com a secretaria de
        // teste.
        when(secretariaService.listarTodas()).thenReturn(Collections.singletonList(secretaria));

        // Executa a requisição GET para o endpoint e verifica a resposta.
        mockMvc.perform(get("/secretarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Ana Silva")));
    }

    @Test
    @DisplayName("Deve buscar uma secretaria por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Configura o mock para encontrar a secretaria com ID 1.
        when(secretariaService.buscarPorId(1L)).thenReturn(Optional.of(secretaria));

        // Executa a requisição GET e verifica os dados retornados.
        mockMvc.perform(get("/secretarias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Ana Silva")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar a secretaria com ID 99.
        when(secretariaService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/secretarias/{id}", 99L))
                // Por padrão, o Spring retorna 200 OK com corpo vazio para um Optional.empty().
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar uma nova secretaria")
    void salvarSecretaria() throws Exception {
        // Configura o mock do serviço para retornar a secretaria salva.
        when(secretariaService.salvar(any(Secretaria.class))).thenReturn(secretaria);

        // Executa a requisição POST com um objeto secretaria no corpo.
        mockMvc.perform(post("/secretarias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secretaria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Ana Silva")));

        // Garante que o método 'salvar' do serviço foi chamado.
        verify(secretariaService, times(1)).salvar(any(Secretaria.class));
    }

    @Test
    @DisplayName("Deve deletar uma secretaria existente")
    void deletarSecretaria() throws Exception {
        // Configura o mock do serviço para o método 'deletar', que é void.
        doNothing().when(secretariaService).deletar(1L);

        // Executa a requisição DELETE para /secretarias/1.
        mockMvc.perform(delete("/secretarias/{id}", 1L))
                // Um método de controller 'void' retorna 200 OK por padrão.
                .andExpect(status().isOk());

        // Garante que o método 'deletar' do serviço foi chamado com o ID correto.
        verify(secretariaService, times(1)).deletar(1L);
    }
}