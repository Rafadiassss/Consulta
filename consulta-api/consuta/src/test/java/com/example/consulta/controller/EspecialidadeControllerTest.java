<<<<<<< HEAD
/* package com.example.consulta.controller;
=======
package com.example.consulta.controller;
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f

import com.example.consulta.model.Especialidade;
import com.example.consulta.service.EspecialidadeService;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EspecialidadeController.class)
@DisplayName("Testes do Controller de Especialidades")
class EspecialidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EspecialidadeService especialidadeService;

    private Especialidade especialidade;

    @BeforeEach
    void setUp() {
        especialidade = new Especialidade();
        especialidade.setId(1L);
        especialidade.setNome("Cardiologia");
        especialidade.setDescricao(
                "Especialidade médica que se ocupa do diagnóstico e tratamento das doenças que afetam o coração.");
    }

    @Test
    @DisplayName("Deve listar todas as especialidades com status 200 OK")
    void listarTodas() throws Exception {
        // Arrange
        when(especialidadeService.listarTodas()).thenReturn(Collections.singletonList(especialidade));

        // Act & Assert
        mockMvc.perform(get("/especialidades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Cardiologia")));
    }

    @Test
    @DisplayName("Deve buscar uma especialidade por ID existente e retornar com status 200 OK")
    void buscarPorId_quandoEncontrado() throws Exception {
        // Arrange
        when(especialidadeService.buscarPorId(1L)).thenReturn(Optional.of(especialidade));

        // Act & Assert
        mockMvc.perform(get("/especialidades/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Cardiologia")));
    }

    @Test
    @DisplayName("Deve retornar corpo vazio ao buscar por um ID inexistente")
    void buscarPorId_quandoNaoEncontrado() throws Exception {
        // Arrange
        when(especialidadeService.buscarPorId(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/especialidades/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve salvar uma nova especialidade e retorná-la com ID")
    void salvarEspecialidade() throws Exception {
        // Arrange
        Especialidade especialidadeParaEnviar = new Especialidade();
        especialidadeParaEnviar.setNome("Cardiologia");
        especialidadeParaEnviar.setDescricao("Descrição da cardiologia.");

        when(especialidadeService.salvar(any(Especialidade.class))).thenReturn(especialidade);

        // Act & Assert
        mockMvc.perform(post("/especialidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(especialidadeParaEnviar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(especialidadeService).salvar(any(Especialidade.class));
    }

    @Test
    @DisplayName("Deve deletar uma especialidade por ID com status 200 OK")
    void deletarEspecialidade() throws Exception {
        when(especialidadeService.deletar(1L)).thenReturn(true); // Retorna 'true' para simular sucesso

        mockMvc.perform(delete("/especialidades/{id}", 1L))
                .andExpect(status().isOk());

        verify(especialidadeService, times(1)).deletar(1L);
    }
<<<<<<< HEAD
} */
=======
}
>>>>>>> 996e84ba9bfad1881755325609d529bacae7ce0f
