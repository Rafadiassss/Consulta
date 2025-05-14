package com.example.consulta.controller;

import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.ProntuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureMockMvc
class ProntuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProntuarioService prontuarioService;

    @BeforeEach
    void setup() {
        // Criando um médico
        Usuario medico = new Usuario();
        medico.setId(1L);
        medico.setNome("Dr. João");
        medico.setTipo("medico");

        // Mockando o retorno do método getUsuarioById para o médico
        Mockito.when(prontuarioService.getUsuarioById(1L)).thenReturn(medico);

        // Criando um usuário não-médico
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("João Silva");
        usuario.setTipo("usuario");

        // Mockando o retorno do método getUsuarioById para o usuário não-médico
        Mockito.when(prontuarioService.getUsuarioById(2L)).thenReturn(usuario);
    }

    @Test
    void devePermitirCriarProntuarioParaMedico() throws Exception {
        // JSON para o prontuário
        String json = """
                {
                    "numero": "12345",
                    "diagnostico": "Hipertensão Arterial",
                    "tratamento": "Medicamento para controle da pressão arterial",
                    "observacoes": "Paciente deve realizar acompanhamento mensal."
                }
                """;

        // Realizando a requisição POST para criar o prontuário
        mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idUsuario", "1")  // Passando o id do médico
                        .content(json))
                .andExpect(status().isOk());  // Espera-se um status 200 OK
    }

    @Test
    void deveBloquearCriarProntuarioParaUsuarioNaoMedico() throws Exception {
        // JSON para o prontuário
        String json = """
                {
                    "numero": "12345",
                    "diagnostico": "Hipertensão Arterial",
                    "tratamento": "Medicamento para controle da pressão arterial",
                    "observacoes": "Paciente deve realizar acompanhamento mensal."
                }
                """;

        // Realizando a requisição POST para tentar criar o prontuário com um usuário não-médico
        mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idUsuario", "2")  // Passando o id de um usuário não-médico
                        .content(json))
                .andExpect(status().isForbidden());  // Espera-se um status 403 Forbidden
    }
}
