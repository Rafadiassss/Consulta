package com.example.consulta.controller;

import com.example.consulta.hateoas.MedicoModelAssembler;
import com.example.consulta.model.Medico;
import com.example.consulta.service.MedicoService;
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

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicoController.class)
@DisplayName("Testes do Controller de Médicos (com HATEOAS)")
class MedicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicoService medicoService;

    @MockBean
    private MedicoModelAssembler assembler;

    private Medico medico;

    @BeforeEach
    void setUp() {
        // Arrange: Cria um objeto Medico base para ser usado nos testes.
        // Assumimos que os campos 'id' e 'nome' vêm da classe pai 'Usuario'.
        medico = new Medico();
        medico.setId(1L);
        medico.setNome("Dr. Gregory House");
        medico.setCrm("123456-SP");
        medico.setEspecialidade("Infectologia e Nefrologia");
    }

    @Test
    @DisplayName("Deve listar médicos e retornar status 200 OK")
    void listar() throws Exception {
        // Arrange: Cria um modelo HATEOAS para o médico.
        EntityModel<Medico> medicoModel = EntityModel.of(medico,
                linkTo(methodOn(MedicoController.class).buscarPorId(medico.getId())).withSelfRel());

        // Arrange: Configura os mocks. Quando o serviço listar, retorna nosso médico.
        // Quando o assembler for chamado, retorna nosso modelo HATEOAS.
        when(medicoService.listarTodos()).thenReturn(Collections.singletonList(medico));
        when(assembler.toModel(medico)).thenReturn(medicoModel);

        // Act: Executa a requisição GET para /medicos.
        mockMvc.perform(get("/medicos"))
                // Assert: Verifica o status, o conteúdo e os links HATEOAS.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.medicoList[0].nome", is("Dr. Gregory House")))
                .andExpect(jsonPath("$._embedded.medicoList[0]._links.self.href", containsString("/medicos/1")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos")));
    }

    @Test
    @DisplayName("Deve buscar médico por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Arrange: Prepara o modelo e configura os mocks para encontrar o médico.
        EntityModel<Medico> medicoModel = EntityModel.of(medico,
                linkTo(methodOn(MedicoController.class).buscarPorId(medico.getId())).withSelfRel());
        when(medicoService.buscarPorId(1L)).thenReturn(Optional.of(medico));
        when(assembler.toModel(medico)).thenReturn(medicoModel);

        // Act: Executa a requisição GET para /medicos/1.
        mockMvc.perform(get("/medicos/{id}", 1L))
                // Assert: Verifica o status e os dados do médico retornado.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dr. Gregory House")))
                .andExpect(jsonPath("$.crm", is("123456-SP")))
                .andExpect(jsonPath("$._links.self.href", containsString("/medicos/1")));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Arrange: Configura o mock para não encontrar nenhum médico com o ID 99.
        when(medicoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act: Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/medicos/{id}", 99L))
                // Assert: Verifica se o status da resposta é 404 Not Found.
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar um novo médico e retornar status 201 Created")
    void salvar() throws Exception {
        // Arrange: Prepara o médico a ser salvo (sem ID) e o médico salvo (com ID).
        Medico medicoParaSalvar = new Medico();
        medicoParaSalvar.setNome("Dr. James Wilson");

        // Arrange: Configura os mocks para simular o salvamento e a montagem do modelo.
        when(medicoService.salvar(any(Medico.class))).thenReturn(medico); // Retorna o médico com ID 1
        when(assembler.toModel(medico)).thenReturn(EntityModel.of(medico,
                linkTo(methodOn(MedicoController.class).buscarPorId(medico.getId())).withSelfRel()));

        // Act: Executa a requisição POST para /medicos.
        mockMvc.perform(post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoParaSalvar)))
                // Assert: Verifica o status 201 Created e o header 'Location'.
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/medicos/1")));
    }

    @Test
    @DisplayName("Deve atualizar um médico existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Arrange: Prepara os dados para a atualização.
        Medico medicoComDadosNovos = new Medico();
        medicoComDadosNovos.setNome("Dr. Gregory House MD");

        // Arrange: Configura os mocks para encontrar, salvar e montar a resposta.
        when(medicoService.buscarPorId(1L)).thenReturn(Optional.of(medico));
        when(medicoService.salvar(any(Medico.class))).thenReturn(medico); // Simula salvar e retornar o objeto
        when(assembler.toModel(medico)).thenReturn(EntityModel.of(medico));

        // Act: Executa a requisição PUT para /medicos/1.
        mockMvc.perform(put("/medicos/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medicoComDadosNovos)))
                // Assert: Verifica o status OK.
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar atualizar médico inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Arrange: Configura o mock para não encontrar o médico a ser atualizado.
        when(medicoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act: Executa a requisição PUT para um ID que não existe.
        mockMvc.perform(put("/medicos/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Medico())))
                // Assert: Verifica se o status é 404 Not Found.
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um médico existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Arrange: Configura o mock para encontrar o médico e permitir a exclusão.
        // Como o método 'deletar' do serviço é void, usamos doNothing().
        when(medicoService.buscarPorId(1L)).thenReturn(Optional.of(medico));
        doNothing().when(medicoService).deletar(1L);

        // Act: Executa a requisição DELETE para /medicos/1.
        mockMvc.perform(delete("/medicos/{id}", 1L))
                // Assert: Verifica o status 204 No Content.
                .andExpect(status().isNoContent());

        // Verify: Garante que o método 'deletar' do serviço foi chamado.
        verify(medicoService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar médico inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Arrange: Configura o mock para não encontrar o médico a ser deletado.
        when(medicoService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act: Executa a requisição DELETE para um ID que não existe.
        mockMvc.perform(delete("/medicos/{id}", 99L))
                // Assert: Verifica se o status é 404 Not Found.
                .andExpect(status().isNotFound());

        // Verify: Garante que o método 'deletar' NUNCA foi chamado.
        verify(medicoService, never()).deletar(anyLong());
    }
}