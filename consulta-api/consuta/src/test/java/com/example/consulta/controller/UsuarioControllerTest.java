package com.example.consulta.controller;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Medico medico;
    private Paciente paciente;

    @BeforeEach
    void setup(WebApplicationContext context) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Criação de objetos para os testes
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setUsername("joao123");
        usuario.setSenha("senha123");
        usuario.setTipo("medico");

        medico = new Medico();
        medico.setId(2L);
        medico.setNome("Dr. Marcos");
        medico.setUsername("drmarcos");
        medico.setSenha("senhaMedico");
        medico.setTipo("medico");

        paciente = new Paciente();
        paciente.setId(3L);
        paciente.setNome("Maria");
        paciente.setUsername("maria123");
        paciente.setSenha("senhaPaciente");
        paciente.setTipo("paciente");
    }

    @Test
    void deveCriarUsuario() throws Exception {
        String json = """
            {
                "nome": "João",
                "username": "joao123",
                "senha": "senha123",
                "tipo": "medico",
                "telefone": "123456789",
                "email": "joao@email.com",
                "dataNascimento": "1990-01-01"
            }
            """;

        Mockito.when(usuarioService.salvar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.username").value("joao123"));
    }

    @Test
    void deveCriarMedico() throws Exception {
        String json = """
            {
                "nome": "Dr. Marcos",
                "username": "drmarcos",
                "senha": "senhaMedico",
                "tipo": "medico",
                "telefone": "987654321",
                "email": "drmarcos@email.com",
                "dataNascimento": "1985-05-10"
            }
            """;

        Mockito.when(usuarioService.salvarMedico(any(Medico.class))).thenReturn(medico);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/medico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Dr. Marcos"))
                .andExpect(jsonPath("$.username").value("drmarcos"));
    }

    @Test
    void deveCriarPaciente() throws Exception {
        String json = """
            {
                "nome": "Maria",
                "username": "maria123",
                "senha": "senhaPaciente",
                "tipo": "paciente",
                "telefone": "321654987",
                "email": "maria@email.com",
                "dataNascimento": "1992-07-20"
            }
            """;

        Mockito.when(usuarioService.salvarPaciente(any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(MockMvcRequestBuilders.post("/usuarios/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.username").value("maria123"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        Mockito.when(usuarioService.buscarPorId(1L)).thenReturn(java.util.Optional.of(usuario));

        mockMvc.perform(MockMvcRequestBuilders.get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.username").value("joao123"));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        Mockito.doNothing().when(usuarioService).deletar(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/usuarios/1"))
                .andExpect(status().isOk());
    }
}
