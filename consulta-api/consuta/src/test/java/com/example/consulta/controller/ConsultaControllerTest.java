package com.example.consulta.controller;

<<<<<<< HEAD
<<<<<<< HEAD
import java.util.Arrays;
import java.util.List;
=======
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.service.ConsultaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
@DisplayName("Testes do Controller de Consultas (API)")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

<<<<<<< HEAD
    @Mock
=======
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.service.ConsultaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaController.class)
@DisplayName("Testes do Controller de Consultas (API)")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

=======
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
<<<<<<< HEAD
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
    private ConsultaService consultaService;

    private Consulta consulta;

    @BeforeEach
    void setUp() {
<<<<<<< HEAD
<<<<<<< HEAD
        MockitoAnnotations.openMocks(this);
        consulta = new Consulta(); // você pode configurar atributos aqui se necessário
=======
=======
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
        // Objeto base para os testes.
        Paciente paciente = new Paciente();
        paciente.setId(1L);

        Medico medico = new Medico();
        medico.setId(1L);

        consulta = new Consulta(
                LocalDateTime.of(2025, 10, 20, 10, 0),
                "AGENDADA",
                paciente,
                medico);
<<<<<<< HEAD
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
        consulta.setId(1L);
    }

    @Test
<<<<<<< HEAD
<<<<<<< HEAD
    void testListarTodas() {
        List<Consulta> consultas = Arrays.asList(consulta);
        when(consultaService.listarTodas()).thenReturn(consultas);
=======
    @DisplayName("Deve listar todas as consultas e retornar status 200 OK")
    void listarTodas() throws Exception {
        // Simula o serviço retornando uma lista com a nossa consulta de teste.
        when(consultaService.listarTodas()).thenReturn(Collections.singletonList(consulta));
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/consultas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("AGENDADA")));
    }

    @Test
    @DisplayName("Deve buscar uma consulta por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando a consulta pelo ID.
        when(consultaService.buscarPorId(1L)).thenReturn(Optional.of(consulta));

        // Executa a requisição GET para o ID existente.
        mockMvc.perform(get("/consultas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando a consulta.
        when(consultaService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/consultas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar uma nova consulta e retornar status 200 OK")
    void salvar() throws Exception {
        // Simula o serviço salvando e retornando a consulta.
        when(consultaService.salvar(any(Consulta.class))).thenReturn(consulta);

        // Executa a requisição POST.
        mockMvc.perform(post("/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AGENDADA")));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Simula o serviço de atualização retornando o objeto atualizado.
        when(consultaService.atualizar(eq(1L), any(Consulta.class))).thenReturn(consulta);

        // Executa a requisição PUT.
        mockMvc.perform(put("/consultas/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar consulta inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando nulo, indicando que a consulta não foi
        // encontrada.
        when(consultaService.atualizar(eq(99L), any(Consulta.class))).thenReturn(null);

        // Executa a requisição PUT para um ID que não existe.
        mockMvc.perform(put("/consultas/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Consulta())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar uma consulta existente e retornar status 204 No Content")
    void deletar() throws Exception {
        // Configura o mock para o método 'deletar', que é void.
        doNothing().when(consultaService).deletar(1L);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/consultas/{id}", 1L))
                // Verifica o status 204, que indica sucesso sem conteúdo no corpo.
                .andExpect(status().isNoContent());

        // Confirma que o método do serviço foi chamado.
        verify(consultaService, times(1)).deletar(1L);
    }
<<<<<<< HEAD
}
=======
    @DisplayName("Deve listar todas as consultas e retornar status 200 OK")
    void listarTodas() throws Exception {
        // Simula o serviço retornando uma lista com a nossa consulta de teste.
        when(consultaService.listarTodas()).thenReturn(Collections.singletonList(consulta));

        // Executa a requisição GET e verifica a resposta.
        mockMvc.perform(get("/consultas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("AGENDADA")));
    }

    @Test
    @DisplayName("Deve buscar uma consulta por ID existente e retornar status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Simula o serviço encontrando a consulta pelo ID.
        when(consultaService.buscarPorId(1L)).thenReturn(Optional.of(consulta));

        // Executa a requisição GET para o ID existente.
        mockMvc.perform(get("/consultas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Simula o serviço não encontrando a consulta.
        when(consultaService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Executa a requisição GET para um ID que não existe.
        mockMvc.perform(get("/consultas/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve salvar uma nova consulta e retornar status 200 OK")
    void salvar() throws Exception {
        // Simula o serviço salvando e retornando a consulta.
        when(consultaService.salvar(any(Consulta.class))).thenReturn(consulta);

        // Executa a requisição POST.
        mockMvc.perform(post("/consultas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AGENDADA")));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta existente e retornar status 200 OK")
    void atualizar_quandoEncontrado() throws Exception {
        // Simula o serviço de atualização retornando o objeto atualizado.
        when(consultaService.atualizar(eq(1L), any(Consulta.class))).thenReturn(consulta);

        // Executa a requisição PUT.
        mockMvc.perform(put("/consultas/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consulta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao tentar atualizar consulta inexistente")
    void atualizar_quandoNaoEncontrado() throws Exception {
        // Simula o serviço retornando nulo, indicando que a consulta não foi
        // encontrada.
        when(consultaService.atualizar(eq(99L), any(Consulta.class))).thenReturn(null);

        // Executa a requisição PUT para um ID que não existe.
        mockMvc.perform(put("/consultas/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Consulta())))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar uma consulta existente e retornar status 204 No Content")
    void deletar() throws Exception {
        // Configura o mock para o método 'deletar', que é void.
        doNothing().when(consultaService).deletar(1L);

        // Executa a requisição DELETE.
        mockMvc.perform(delete("/consultas/{id}", 1L))
                // Verifica o status 204, que indica sucesso sem conteúdo no corpo.
                .andExpect(status().isNoContent());

        // Confirma que o método do serviço foi chamado.
        verify(consultaService, times(1)).deletar(1L);
    }
}
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
=======
}
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
