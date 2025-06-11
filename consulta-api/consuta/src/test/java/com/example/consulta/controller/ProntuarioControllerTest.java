<<<<<<< HEAD
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
=======
package com.example.consulta.controller;

import com.example.consulta.model.EntradaProntuario;
import com.example.consulta.model.Prontuario;
import com.example.consulta.service.ProntuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(ProntuarioController.class)
@DisplayName("Testes do Controller de Prontuarios")
class ProntuarioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ProntuarioService prontuarioService;

        private Prontuario prontuario;

        @BeforeEach
        void setUp() {
                // Objeto Prontuario base que será usado nos testes.
                prontuario = new Prontuario();
                prontuario.setId(10L);
                prontuario.setNumero("PR-001");
        }

        @Test
        @DisplayName("Deve salvar um prontuário com sucesso e retornar status 201 Created")
        void salvar_comSucesso() throws Exception {
                // Prepara a resposta de sucesso que o serviço deve retornar.
                ResponseEntity<String> respostaEsperada = ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body("Prontuário criado com sucesso.");

                // Configura o mock para retornar a resposta de sucesso quando 'criarProntuario'
                // for chamado.
                when(prontuarioService.criarProntuario(eq(1L), any(Prontuario.class))).thenReturn(respostaEsperada);

                // Executa a requisição POST e verifica a resposta.
                mockMvc.perform(post("/prontuarios/salvar/{idUsuario}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuario)))
                                .andExpect(status().isCreated())
                                .andExpect(content().string("Prontuário criado com sucesso."));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao tentar salvar para usuário inexistente")
        void salvar_comUsuarioNaoEncontrado() throws Exception {
                // Prepara a resposta de erro (404) que o serviço deve retornar.
                ResponseEntity<String> respostaEsperada = ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("Usuário (médico) não encontrado.");

                // Configura o mock para retornar o erro 404 para o usuário com ID 99.
                when(prontuarioService.criarProntuario(eq(99L), any(Prontuario.class))).thenReturn(respostaEsperada);

                // Executa a requisição POST para um usuário que não existe e verifica o erro.
                mockMvc.perform(post("/prontuarios/salvar/{idUsuario}", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(prontuario)))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Usuário (médico) não encontrado."));
        }

        @Test
        @DisplayName("Deve retornar 403 Forbidden ao tentar criar prontuário com usuário não autorizado")
        void salvar_quandoServicoRetornaForbidden() throws Exception {
                // Prepara a resposta de erro (403 Forbidden)
                ResponseEntity<String> respostaDeErro = ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body("Apenas médicos podem criar prontuários.");

                // Dizemos ao mock para retornar esta resposta de erro quando o método for
                // chamado com o ID 2 (simulando um não-médico).
                when(prontuarioService.criarProntuario(eq(2L), any(Prontuario.class))).thenReturn(respostaDeErro);

                // Executa a requisição POST com o ID do usuário não autorizado.
                mockMvc.perform(post("/prontuarios/salvar/{idUsuario}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new Prontuario())))
                                // Verifica se o controller repassou corretamente o status 403 Forbidden.
                                .andExpect(status().isForbidden())
                                .andExpect(content().string("Apenas médicos podem criar prontuários."));
        }

        @Test
        @DisplayName("Deve buscar um prontuário com sucesso e retornar status 200 OK")
        void buscarProntuario_comSucesso() throws Exception {
                // Prepara uma entrada para o histórico do prontuário.
                EntradaProntuario entrada = new EntradaProntuario();
                entrada.setDiagnostico("Hipertensão");

                // Adiciona a entrada ao prontuário que será retornado.
                prontuario.adicionarEntrada(entrada);

                // Prepara a resposta de sucesso com o prontuário contendo o histórico.
                ResponseEntity<?> respostaEsperada = ResponseEntity.ok(prontuario);

                // Configura o mock para retornar o prontuário quando buscado.
                doReturn(respostaEsperada).when(prontuarioService).buscarProntuario(1L, 10L);

                // Executa a requisição GET e verifica os dados retornados.
                mockMvc.perform(get("/prontuarios/buscar/{idUsuario}/{idProntuario}", 1L, 10L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.numero", is("PR-001")))
                                // A verificação aponta para o diagnóstico dentro da primeira entrada do
                                // histórico.
                                .andExpect(jsonPath("$.entradas[0].diagnostico", is("Hipertensão")));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found ao buscar um prontuário inexistente")
        void buscarProntuario_naoEncontrado() throws Exception {
                // Prepara a resposta de erro (404) para quando o prontuário não é encontrado.
                ResponseEntity<String> respostaDeErro = ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("Prontuário não encontrado.");

                // Configura o mock para retornar o erro.
                doReturn(respostaDeErro).when(prontuarioService).buscarProntuario(1L, 99L);

                // Executa a requisição GET para um prontuário que não existe e verifica o erro.
                mockMvc.perform(get("/prontuarios/buscar/{idUsuario}/{idProntuario}", 1L, 99L))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Prontuário não encontrado."));
        }

        @Test
        @DisplayName("Deve adicionar nova entrada a um prontuário existente, mantendo o histórico")
        void adicionarNovaEntrada_mantendoHistorico() throws Exception {

                // Simula uma entrada antiga que já existe no prontuário.
                EntradaProntuario entradaAntiga = new EntradaProntuario();
                entradaAntiga.setDiagnostico("Diagnóstico antigo");

                // Simula o prontuário como ele está no banco, já com a entrada antiga.
                Prontuario prontuarioExistente = new Prontuario();
                prontuarioExistente.setId(1L);
                prontuarioExistente.setNumero("PR-001");
                prontuarioExistente.adicionarEntrada(entradaAntiga);

                // Este é o objeto com as NOVAS informações que estamos enviando na
                // requisição.
                EntradaProntuario novaEntrada = new EntradaProntuario();
                novaEntrada.setDiagnostico("Novo diagnóstico");
                novaEntrada.setTratamento("Novo tratamento");

                // Prepara a resposta que esperamos do serviço: o prontuário com AMBAS as
                // entradas.
                prontuarioExistente.adicionarEntrada(novaEntrada); // Adiciona a nova entrada ao objeto
                ResponseEntity<?> respostaEsperada = ResponseEntity.ok(prontuarioExistente);

                // Configura o mock do serviço para simular a lógica de adição.
                doReturn(respostaEsperada).when(prontuarioService)
                                .adicionarEntrada(eq(1L), any(EntradaProntuario.class));

                // Executa a requisição POST para o novo endpoint, enviando a nova entrada.
                mockMvc.perform(post("/prontuarios/{idProntuario}/entradas", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(novaEntrada)))

                                .andExpect(status().isOk())
                                // Verifica se a lista de entradas no JSON de resposta agora tem 2 itens.
                                .andExpect(jsonPath("$.entradas", hasSize(2)))
                                // Verifica se a primeira entrada (o histórico) ainda está lá.
                                .andExpect(jsonPath("$.entradas[0].diagnostico", is("Diagnóstico antigo")))
                                // Verifica se a segunda entrada (a nova) foi adicionada corretamente.
                                .andExpect(jsonPath("$.entradas[1].diagnostico", is("Novo diagnóstico")));
        }
}
>>>>>>> 1b19c972bcbcc15b70fc0e087c0d6b07c2c37776
