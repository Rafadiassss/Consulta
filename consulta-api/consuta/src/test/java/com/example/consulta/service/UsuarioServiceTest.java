package com.example.consulta.service;

import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Usuários")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve salvar um usuário genérico com sucesso")
    void salvarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Comum");

        // Simula o repositório salvando e retornando o usuário.
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Chama o método do serviço.
        Usuario resultado = usuarioService.salvar(new Usuario());

        // Verifica o resultado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Usuário Comum");
    }

    @Test
    @DisplayName("Deve salvar um Medico usando o método específico")
    void salvarMedico() {
        Medico medico = new Medico();
        medico.setId(2L);
        medico.setNome("Dra. Ana");
        medico.setCrm("12345-SP");

        // Simula o repositório salvando um objeto Medico.
        when(usuarioRepository.save(any(Medico.class))).thenReturn(medico);

        // Chama o método específico para salvar médicos.
        Usuario resultado = usuarioService.salvarMedico(new Medico());

        // Verifica se o resultado é do tipo Medico e se os dados estão corretos.
        assertThat(resultado).isInstanceOf(Medico.class);
        Medico resultadoMedico = (Medico) resultado;
        assertThat(resultadoMedico.getCrm()).isEqualTo("12345-SP");
    }

    @Test
    @DisplayName("Deve salvar um Paciente usando o método específico")
    void salvarPaciente() {
        Paciente paciente = new Paciente();
        paciente.setId(3L);
        paciente.setNome("Carlos Souza");
        paciente.setCpf("111.222.333-44");

        // Simula o repositório salvando um objeto Paciente.
        when(usuarioRepository.save(any(Paciente.class))).thenReturn(paciente);

        // Chama o método específico para salvar pacientes.
        Usuario resultado = usuarioService.salvarPaciente(new Paciente());

        // Verifica se o resultado é do tipo Paciente e se os dados estão corretos.
        assertThat(resultado).isInstanceOf(Paciente.class);
        Paciente resultadoPaciente = (Paciente) resultado;
        assertThat(resultadoPaciente.getCpf()).isEqualTo("111.222.333-44");
    }
}