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
        Usuario medico = new Usuario();
        medico.setId(1L);
        medico.setNome("Dr. João");
        medico.setTipo("medico");

        Mockito.when(prontuarioService.getUsuarioById(anyLong())).thenReturn(medico);
    }

    @Test
    void devePermitirCriarProntuarioParaMedico() throws Exception {
        String json = """
                {
                    "numero": "12345",
                    "diagnostico": "Hipertensão Arterial",
                    "tratamento": "Medicamento para controle da pressão arterial",
                    "observacoes": "Paciente deve realizar acompanhamento mensal."
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idUsuario", "1")
                        .content(json))
                .andExpect(status().isOk());
    }
}
 