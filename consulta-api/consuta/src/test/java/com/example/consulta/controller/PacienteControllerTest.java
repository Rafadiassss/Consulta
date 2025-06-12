package com.example.consulta.controller;

import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.hateoas.PacienteModelAssembler;
import com.example.consulta.service.PacienteService;
import com.example.consulta.vo.PacienteVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@DisplayName("Testes do Controller de Pacientes (API com HATEOAS)")
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired

    @MockBean
    private PacienteService pacienteService;
    @MockBean
    private PacienteModelAssembler assembler;

    private PacienteVO pacienteVO;
    private PacienteRequestDTO pacienteRequestDTO;
    private EntityModel<PacienteVO> pacienteModel;

    @BeforeEach
    void setUp() {
        pacienteVO = new PacienteVO(1L, "Carlos Souza", "carlos.s", null, null, null, "111.222.333-44", null);
        pacienteRequestDTO = new PacienteRequestDTO("Carlos Souza", "carlos.s", "senha", null, null, null,
                "111.222.333-44", null);
        pacienteModel = EntityModel.of(pacienteVO,
                linkTo(methodOn(PacienteController.class).buscarPorId(1L)).withSelfRel());
    }

    @Test
    @DisplayName("Deve buscar paciente por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando o paciente.
        when(pacienteService.buscarPorId(1L)).thenReturn(Optional.of(pacienteVO));
        // Simula o assembler convertendo o VO para o modelo HATEOAS.
        when(assembler.toModel(pacienteVO)).thenReturn(pacienteModel);

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/pacientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf", is("111.222.333-44")))
                .andExpect(jsonPath("$._links.self.href", containsString("/pacientes/1")));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando o paciente.
        when(pacienteService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição e verifica o 404.
        mockMvc.perform(get("/pacientes/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um paciente existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Simula o serviço retornando 'true' para a exclusão.
        when(pacienteService.deletar(1L)).thenReturn(true);

        // Executa a requisição DELETE e verifica o 204.
        mockMvc.perform(delete("/pacientes/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar deletar paciente inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando 'false' para a exclusão.
        when(pacienteService.deletar(99L)).thenReturn(false);

        // Executa a requisição e verifica o 404.
        mockMvc.perform(delete("/pacientes/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}
