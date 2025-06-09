package com.example.consulta.controller;

import com.example.consulta.model.Paciente;
import com.example.consulta.service.PacienteService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@DisplayName("Testes do Controller de Pacientes")
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteService pacienteService;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        // Arrange: Cria um objeto Paciente base para ser usado nos testes.
        // Assumimos que os campos 'id' e 'nome' vêm da classe pai 'Usuario'.
        paciente = new Paciente();
        // paciente.setId(1L);
        // paciente.setNome("João da Silva");
        paciente.setCpf("123.456.789-00");
    }

    @Test
    @DisplayName("Deve listar todos os pacientes")
    void listarTodos() throws Exception {
        // Arrange: Configura o mock do serviço para retornar uma lista com um paciente.
        when(pacienteService.listarTodos()).thenReturn(Collections.singletonList(paciente));

        // Act: Executa a requisição GET para o endpoint /pacientes.
        mockMvc.perform(get("/pacientes"))
                // Assert: Verifica se o status é 200 (OK) e se o CPF está correto no JSON.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpf", is("123.456.789-00")));
    }

    @Test
    @DisplayName("Deve buscar um paciente por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Arrange: Configura o mock para encontrar o paciente com ID 1.
        when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(paciente));

        // Act: Executa a requisição GET para /pacientes/1.
        mockMvc.perform(get("/pacientes/{id}", 1L))
                // Assert: Verifica se o status é 200 (OK) e os dados estão corretos.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", is("123.456.789-00")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Arrange: Configura o mock para não encontrar o paciente com ID 99.
        when(pacienteService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act: Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/pacientes/{id}", 99L))
                // Assert: O Spring, por padrão, retorna 200 OK com corpo vazio para
                // Optional.empty().
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo paciente")
    void salvarPaciente() throws Exception {
        // Arrange: Configura o mock do serviço para retornar o paciente salvo.
        when(pacienteService.salvar(any(Paciente.class))).thenReturn(paciente);

        // Act: Executa a requisição POST para /pacientes.
        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                // Assert: Verifica se o status é 200 (OK).
                .andExpect(status().isOk());

        // Verify: Garante que o método 'salvar' do serviço foi chamado.
        verify(pacienteService, times(1)).salvar(any(Paciente.class));
    }

    @Test
    @DisplayName("Deve atualizar um paciente existente")
    void atualizarPaciente() throws Exception {
        // Arrange: Cria um objeto com os dados atualizados.
        Paciente pacienteAtualizado = new Paciente();
        pacienteAtualizado.setCpf("987.654.321-99");

        // Arrange: Configura o mock para retornar o paciente com os dados atualizados.
        when(pacienteService.atualizar(eq(1L), any(Paciente.class))).thenReturn(pacienteAtualizado);

        // Act: Executa a requisição PUT para /pacientes/1.
        mockMvc.perform(put("/pacientes/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteAtualizado)))
                // Assert: Verifica se o status é 200 (OK) e se o CPF foi atualizado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", is("987.654.321-99")));
    }

    @Test
    @DisplayName("Deve deletar um paciente existente e retornar status 204 No Content")
    void deletarPaciente() throws Exception {
        // Arrange: Configura o mock do serviço para o método 'deletar', que é void.
        doNothing().when(pacienteService).deletar(1L);

        // Act: Executa a requisição DELETE para /pacientes/1.
        mockMvc.perform(delete("/pacientes/{id}", 1L))
                // Assert: Verifica se o status da resposta é 204 (No Content).
                .andExpect(status().isNoContent());

        // Verify: Garante que o método 'deletar' do serviço foi chamado com o ID
        // correto.
        verify(pacienteService, times(1)).deletar(1L);
    }
}