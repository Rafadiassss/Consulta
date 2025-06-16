package com.example.consulta.service;

import com.example.consulta.dto.ConsultaRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.Agenda;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Usuario;
import com.example.consulta.repository.AgendaRepository;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.UsuarioRepository;
import com.example.consulta.vo.ConsultaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Prontuários")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private ConsultaService consultaService;

    private Usuario medico;
    private Usuario paciente;
    private Consulta consulta;
    private Agenda agenda;
    private ConsultaRequestDTO consultaRequestDTO;

    @BeforeEach
    void setUp() {
        medico = new Usuario();
        medico.setTipo(TipoUsuario.MEDICO);
        ReflectionTestUtils.setField(medico, "id", 1L);

        paciente = new Usuario();
        paciente.setTipo(TipoUsuario.PACIENTE);
        ReflectionTestUtils.setField(paciente, "id", 2L);

        agenda = new Agenda();
        ReflectionTestUtils.setField(agenda, "id", 1L);

        consulta = new Consulta();
        consulta.setNumero("PRT-001");
        consulta.setAgenda(agenda);
        ReflectionTestUtils.setField(consulta, "id", 10L);

        consultaRequestDTO = new ConsultaRequestDTO("PRT-002", 1L);
    }

    @Test
    @DisplayName("Deve criar uma consulta com sucesso quando usuário for médico")
    void criarConsulta_quandoUsuarioForMedico() {
        // Configura o mock para retornar o médico quando buscar o ID 1
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Configura o mock para retornar a agenda quando buscar o ID 1
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        // Configura o mock do save para simular o salvamento e atribuir ID 20
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocation -> {
            // Obtém o objeto Consulta passado como argumento
            Consulta consultaSalva = invocation.getArgument(0);
            // Define o ID 20 via reflection
            ReflectionTestUtils.setField(consultaSalva, "id", 20L);
            // Retorna a consulta com ID atribuído
            return consultaSalva;
        });
        // Act
        ConsultaVO resultado = consultaService.criarConsulta(1L, consultaRequestDTO);

        // Verifica se o resultado não é nulo
        assertThat(resultado).isNotNull();
        // Verifica se o ID da consulta é 20
        assertThat(resultado.id()).isEqualTo(20L);
        // Verifica se o número da consulta é PRT-002
        assertThat(resultado.numero()).isEqualTo("PRT-002");
        // Verifica se o ID da agenda é 1
        assertThat(resultado.agendaId()).isEqualTo(1L);
        // Verifica se o método save foi chamado exatamente uma vez
        verify(consultaRepository, times(1)).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar consulta com usuário não-médico")
    void criarConsulta_quandoUsuarioNaoForMedico_deveLancarExcecao() {
        // Configura o mock para retornar um paciente quando buscar ID 2
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(paciente));

        // Verifica se a exceção é lançada ao tentar criar consulta com paciente
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            consultaService.criarConsulta(2L, consultaRequestDTO);
        });

        // Valida a mensagem de erro retornada
        assertThat(exception.getMessage()).contains("Apenas médicos podem criar Consulta");
        // Garante que o método save nunca foi chamado
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar consulta com usuário inexistente")
    void criarConsulta_quandoUsuarioNaoExistir_deveLancarExcecao() {
        // Configura o mock para retornar Optional vazio quando buscar ID 99
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Executa o teste e captura a exceção esperada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            consultaService.criarConsulta(99L, consultaRequestDTO);
        });

        // Verifica se a mensagem de erro contém o texto esperado
        assertThat(exception.getMessage()).contains("Usuário (médico) não encontrado");
        // Confirma que o método save nunca foi chamado
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve buscar uma consulta com sucesso quando usuário for médico")
    void buscarConsultaVO_quandoUsuarioForMedico_deveRetornarConsultaVO() {
        // Configura mock para retornar médico com ID 1
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Configura mock para retornar consulta com ID 10
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consulta));

        // Executa o método de busca de consulta
        ConsultaVO resultado = consultaService.buscarConsultaVO(1L, 10L);

        // Verifica se o resultado não é nulo
        assertThat(resultado).isNotNull();
        // Valida se o ID da consulta é 10
        assertThat(resultado.id()).isEqualTo(10L);
        // Valida se o número da consulta é PRT-001
        assertThat(resultado.numero()).isEqualTo("PRT-001");
        // Valida se o ID da agenda é 1
        assertThat(resultado.agendaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar consulta com usuário não-médico")
    void buscarConsultaVO_quandoUsuarioNaoForMedico() {
        // Configura o mock para retornar um paciente quando buscar ID 2
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(paciente));

        // Verifica se a exceção é lançada ao tentar buscar consulta com paciente
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            consultaService.buscarConsultaVO(2L, 10L);
        });

        // Valida a mensagem de erro retornada
        assertThat(exception.getMessage()).contains("Apenas médicos podem visualizar Consulta");
        // Garante que o método findById nunca foi chamado
        verify(consultaRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar consulta inexistente")
    void buscarConsultaVO_quandoConsultaNaoExistir() {
        // Configura mock para retornar médico quando buscar ID 1
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Configura mock para retornar vazio ao buscar consulta inexistente
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // Executa o teste e captura a exceção esperada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            consultaService.buscarConsultaVO(1L, 99L);
        });

        // Verifica se a mensagem de erro está correta
        assertThat(exception.getMessage()).contains("Consulta não encontrada");
    }
}