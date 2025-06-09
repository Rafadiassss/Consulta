package com.example.consulta.service;

import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Prontuários")
class ProntuarioServiceTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ProntuarioService prontuarioService;

    private Usuario medico;
    private Usuario paciente;
    private Prontuario prontuario;

    @BeforeEach
    void setUp() {
        medico = new Usuario();
        medico.setId(1L);
        medico.setTipo("MÉDICO");

        paciente = new Usuario();
        paciente.setId(2L);
        paciente.setTipo("PACIENTE");

        prontuario = new Prontuario();
        prontuario.setId(10L);
        prontuario.setNumero("PRT-001");
    }

    @Test
    @DisplayName("Deve criar um prontuário com sucesso para um médico")
    void criarProntuario_comSucesso() {
        // Simula que o usuário (médico) foi encontrado.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Simula a ação de salvar o prontuário.
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario);

        // Chama o método do serviço.
        ResponseEntity<String> resposta = prontuarioService.criarProntuario(1L, new Prontuario());

        // Verifica se a resposta é 201 Created.
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resposta.getBody()).isEqualTo("Prontuário criado com sucesso.");
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar prontuário com usuário que não é médico")
    void criarProntuario_quandoUsuarioNaoEhMedico() {
        // Simula que o usuário encontrado não é um médico.
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(paciente));

        // Chama o método do serviço.
        ResponseEntity<String> resposta = prontuarioService.criarProntuario(2L, new Prontuario());

        // Verifica se a resposta é 403 Forbidden.
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Deve buscar um prontuário com sucesso")
    void buscarProntuario_comSucesso() {
        // Simula que o usuário (médico) e o prontuário foram encontrados.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(prontuarioRepository.findById(10L)).thenReturn(Optional.of(prontuario));

        // Chama o método do serviço.
        ResponseEntity<?> resposta = prontuarioService.buscarProntuario(1L, 10L);

        // Verifica se a resposta é 200 OK e o corpo contém o prontuário correto.
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody()).isEqualTo(prontuario);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar prontuário inexistente")
    void buscarProntuario_quandoProntuarioNaoEncontrado() {
        // Simula que o usuário (médico) foi encontrado.
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Simula que o prontuário NÃO foi encontrado.
        when(prontuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço.
        ResponseEntity<?> resposta = prontuarioService.buscarProntuario(1L, 99L);

        // Verifica se a resposta é 404 Not Found.
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).isEqualTo("Prontuário não encontrado.");
    }
}