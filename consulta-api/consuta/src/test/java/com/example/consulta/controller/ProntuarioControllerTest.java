package com.example.consulta.controller;

import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.service.ProntuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test") // Isso só garante que vai ser usado o "application-test.properties" para fazer
                        // os testes da classe
@SpringBootTest
@AutoConfigureMockMvc
class ProntuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProntuarioService prontuarioService;

    // Define uma configuração especial só para testes
    @TestConfiguration
    static class MockConfig {
        // Cria um "bean" (objeto gerenciado pelo Spring) para o serviço de prontuários
        @Bean
        public ProntuarioService prontuarioService() {
            // Retorna uma versão "falsa" (mock) do ProntuarioService criada pelo Mockito
            return Mockito.mock(ProntuarioService.class);
        }
    }

    @BeforeEach
void setup() {
    // Criando um médico
    Usuario medico = new Usuario();
    medico.setId(1L);
    medico.setNome("Dr. João");
    medico.setTipo("medico");

    // Criando um usuário não-médico (id 2)
    Usuario usuarioNaoMedico1 = new Usuario();
    usuarioNaoMedico1.setId(2L);
    usuarioNaoMedico1.setNome("João Silva");
    usuarioNaoMedico1.setTipo("usuario");

    // Criando usuário não-médico (ID 5)
    Usuario usuarioNaoMedico2 = new Usuario();
    usuarioNaoMedico2.setId(5L);
    usuarioNaoMedico2.setNome("Maria Silva");
    usuarioNaoMedico2.setTipo("usuario");

    // Criando usuário não-médico (ID 6)
    Usuario usuarioNaoMedico3 = new Usuario();
    usuarioNaoMedico3.setId(6L);
    usuarioNaoMedico3.setNome("Pedro Santos");
    usuarioNaoMedico3.setTipo("usuario");

    // Mapeando os usuários mockados com seus IDs
    Mockito.when(prontuarioService.getUsuarioById(1L)).thenReturn(medico);
    Mockito.when(prontuarioService.getUsuarioById(2L)).thenReturn(usuarioNaoMedico1);
    Mockito.when(prontuarioService.getUsuarioById(5L)).thenReturn(usuarioNaoMedico2);
    Mockito.when(prontuarioService.getUsuarioById(6L)).thenReturn(usuarioNaoMedico3);

    // Mock da lógica de criação de prontuário dependendo do tipo do usuário
    Mockito.when(prontuarioService.criarProntuario(anyLong(), any(Prontuario.class)))
            .thenAnswer(invocation -> {
                Long idUsuario = invocation.getArgument(0);
                Usuario usuario = prontuarioService.getUsuarioById(idUsuario);

                if (usuario != null && "medico".equals(usuario.getTipo())) {
                    return ResponseEntity.ok("Prontuário criado com sucesso");
                }

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Apenas médicos podem criar prontuários");
            });
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
        mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()); // Espera-se um status 200 OK
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

        // Realizando a requisição POST para tentar criar o prontuário com um usuário
        // não-médico
        mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden()); // Espera-se um status 403 Forbidden
    }

    @Test
void deveRetornarNotFoundSeUsuarioNaoExistir() throws Exception {
    String json = """
            {
                "numero": "99999",
                "diagnostico": "Diabetes",
                "tratamento": "Insulina",
                "observacoes": "Revisar em 30 dias."
            }
            """;

    // Não foi configurado getUsuarioById(7L) então o mock retornará null
    mockMvc.perform(MockMvcRequestBuilders.post("/prontuarios/salvar/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isForbidden()); 
}

}
