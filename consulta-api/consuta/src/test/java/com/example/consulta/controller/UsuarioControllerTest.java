package com.example.consulta.controller;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.UsuarioService;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import com.fasterxml.jackson.databind.ObjectMapper;
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
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

@WebMvcTest(UsuarioController.class)
<<<<<<< HEAD
=======
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

@WebMvcTest(UsuarioController.class)
@DisplayName("Testes do Controller de Usuários")
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
@DisplayName("Testes do Controller de Usuários")
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
<<<<<<< HEAD
=======
    @Autowired
    private ObjectMapper objectMapper;

>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
    @Autowired
    private ObjectMapper objectMapper;

>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Medico medico;
    private Paciente paciente;

    @BeforeEach
<<<<<<< HEAD
<<<<<<< HEAD
    void setup(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Criação de objetos para os testes
=======
    void setUp() {
        // Objeto base para um usuário genérico.
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Padrão");
        usuario.setUsername("user.default");
        usuario.setTipo("USUARIO");

        // Objeto para um médico, usando a classe real do seu projeto.
        medico = new Medico();
        medico.setId(2L);
        medico.setNome("Dra. Ana");
        medico.setUsername("ana.med");
        medico.setTipo("MEDICO");
        medico.setCrm("12345-SP"); // Supondo que Medico tenha o campo 'crm'

        // Objeto para um paciente, usando a classe real do seu projeto.
        paciente = new Paciente();
        paciente.setId(3L);
        paciente.setNome("Carlos Souza");
        paciente.setUsername("carlos.souza");
        paciente.setTipo("PACIENTE");
        paciente.setCpf("111.222.333-44"); // Supondo que Paciente tenha o campo 'cpf'
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarTodos() throws Exception {
        // Configura o mock do serviço para retornar uma lista de usuários.
        when(usuarioService.listarTodos()).thenReturn(Collections.singletonList(usuario));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Usuário Padrão")));
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Configura o mock para encontrar o usuário com ID 1.
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        // Executa a requisição GET e verifica os dados retornados.
        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Usuário Padrão")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar o usuário com ID 99.
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo médico")
    void salvarMedico() throws Exception {
        // Configura o mock para retornar o médico salvo.
        when(usuarioService.salvarMedico(any(Medico.class))).thenReturn(medico);

        // Executa a requisição POST para /usuarios/medico.
        mockMvc.perform(post("/usuarios/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dra. Ana")));
    }

    @Test
    @DisplayName("Deve salvar um novo paciente")
    void salvarPaciente() throws Exception {
        // Configura o mock para retornar o paciente salvo.
        when(usuarioService.salvarPaciente(any(Paciente.class))).thenReturn(paciente);

        // Executa a requisição POST para /usuarios/paciente.
        mockMvc.perform(post("/usuarios/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos Souza")));
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void atualizarUsuario() throws Exception {
        // Configura o mock do serviço para o método 'salvar' que é chamado pelo
        // 'atualizar' do controller.
        when(usuarioService.salvar(any(Usuario.class))).thenReturn(usuario);

        // Executa a requisição PUT.
        mockMvc.perform(put("/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Usuário Padrão")));
    }
<<<<<<< HEAD
}
=======
    void setUp() {
        // Objeto base para um usuário genérico.
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Padrão");
        usuario.setUsername("user.default");
        usuario.setTipo("USUARIO");

        // Objeto para um médico, usando a classe real do seu projeto.
        medico = new Medico();
        medico.setId(2L);
        medico.setNome("Dra. Ana");
        medico.setUsername("ana.med");
        medico.setTipo("MEDICO");
        medico.setCrm("12345-SP"); // Supondo que Medico tenha o campo 'crm'

        // Objeto para um paciente, usando a classe real do seu projeto.
        paciente = new Paciente();
        paciente.setId(3L);
        paciente.setNome("Carlos Souza");
        paciente.setUsername("carlos.souza");
        paciente.setTipo("PACIENTE");
        paciente.setCpf("111.222.333-44"); // Supondo que Paciente tenha o campo 'cpf'
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarTodos() throws Exception {
        // Configura o mock do serviço para retornar uma lista de usuários.
        when(usuarioService.listarTodos()).thenReturn(Collections.singletonList(usuario));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Usuário Padrão")));
    }

    @Test
    @DisplayName("Deve buscar um usuário por ID existente")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Configura o mock para encontrar o usuário com ID 1.
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        // Executa a requisição GET e verifica os dados retornados.
        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Usuário Padrão")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar o usuário com ID 99.
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar um novo médico")
    void salvarMedico() throws Exception {
        // Configura o mock para retornar o médico salvo.
        when(usuarioService.salvarMedico(any(Medico.class))).thenReturn(medico);

        // Executa a requisição POST para /usuarios/medico.
        mockMvc.perform(post("/usuarios/medico")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medico)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Dra. Ana")));
    }

    @Test
    @DisplayName("Deve salvar um novo paciente")
    void salvarPaciente() throws Exception {
        // Configura o mock para retornar o paciente salvo.
        when(usuarioService.salvarPaciente(any(Paciente.class))).thenReturn(paciente);

        // Executa a requisição POST para /usuarios/paciente.
        mockMvc.perform(post("/usuarios/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos Souza")));
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void atualizarUsuario() throws Exception {
        // Configura o mock do serviço para o método 'salvar' que é chamado pelo
        // 'atualizar' do controller.
        when(usuarioService.salvar(any(Usuario.class))).thenReturn(usuario);

        // Executa a requisição PUT.
        mockMvc.perform(put("/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Usuário Padrão")));
    }
=======
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f

    @Test
    @DisplayName("Deve deletar um usuário existente e retornar status 204 No Content")
    void deletar_quandoEncontrado() throws Exception {
        // Configura o mock para primeiro encontrar o usuário.
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        // Configura o mock para a chamada de 'deletar', que é void.
        doNothing().when(usuarioService).deletar(1L);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/usuarios/{id}", 1L))
                .andExpect(status().isNoContent()); // Verifica o status 204

        // Garante que o método 'deletar' do serviço foi chamado.
        verify(usuarioService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar usuário inexistente")
    void deletar_quandoNaoEncontrado() throws Exception {
        // Configura o mock para não encontrar o usuário.
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição DELETE para um ID que não existe.
        mockMvc.perform(delete("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound()); // Verifica o status 404

        // Garante que o método 'deletar' NUNCA foi chamado.
        verify(usuarioService, never()).deletar(anyLong());
    }
<<<<<<< HEAD
}
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
}
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
