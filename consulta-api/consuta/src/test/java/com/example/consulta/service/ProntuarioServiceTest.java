package com.example.consulta.service;

import com.example.consulta.dto.ProntuarioRequestDTO;
import com.example.consulta.model.*;
import com.example.consulta.repository.*;
import com.example.consulta.vo.ProntuarioVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Prontuários")
class ProntuarioServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private ProntuarioRepository prontuarioRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private ProntuarioService prontuarioService;

    private Paciente paciente;
    private Medico medico;
    private Prontuario prontuario2;
    private ProntuarioRequestDTO prontuarioRequestDTO;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);

        medico = new Medico();
        medico.setId(2L);

        prontuario2 = new Prontuario(LocalDateTime.now().plusDays(1), "AGENDADA", paciente, medico);
        prontuario2.setId(1L);
        prontuario2.setNomeConsulta("Consulta de Rotina");

        prontuarioRequestDTO = new ProntuarioRequestDTO(
                LocalDateTime.now().plusDays(1), "AGENDADA", "Consulta de Rotina",
                paciente.getId(), medico.getId(), null, null);
    }

    @Test
    @DisplayName("Deve listar todos os prontuários")
    void listarTodos() {
        // Simula o retorno de uma lista com um único prontuário
        when(prontuarioRepository.findAll()).thenReturn(Collections.singletonList(prontuario2));
        // Executa o método que lista todos os prontuários
        List<ProntuarioVO> resultado = prontuarioService.listarTodos();
        // Verifica se o resultado não é nulo e tem tamanho 1
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve salvar um prontuário com sucesso")
    void salvar_comSucesso() {
        // Simula a busca do paciente com ID 1
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        // Simula a busca do médico com ID 2
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        // Simula o salvamento do prontuário
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario2);

        // Executa o método de salvar prontuário
        ProntuarioVO resultado = prontuarioService.salvar(prontuarioRequestDTO);

        // Verifica se o resultado não é nulo
        assertThat(resultado).isNotNull();
        // Verifica se o ID do paciente é 1
        assertThat(resultado.paciente().getId()).isEqualTo(1L);
        // Verifica se o método save foi chamado
        verify(prontuarioRepository).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se o paciente não for encontrado")
    void salvar_quandoPacienteNaoEncontrado() {
        // Simula o caso onde o paciente não é encontrado no repositório
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se uma RuntimeException é lançada ao tentar salvar
        assertThrows(RuntimeException.class, () -> {
            prontuarioService.salvar(prontuarioRequestDTO);
        });

        // Confirma que o método save nunca foi chamado no repositório
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve atualizar um prontuário com sucesso")
    void atualizar_quandoEncontrado() {
        // Simula encontrar o prontuário com ID 1
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario2));
        // Simula encontrar qualquer paciente buscado
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(paciente));
        // Simula encontrar qualquer médico buscado
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.of(medico));
        // Simula o salvamento do prontuário atualizado
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario2);

        // Executa o método de atualização
        Optional<ProntuarioVO> resultado = prontuarioService.atualizar(1L, prontuarioRequestDTO);

        // Verifica se o resultado está presente
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar prontuário inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula o caso onde o prontuário com ID 99 não é encontrado
        when(prontuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Tenta atualizar um prontuário inexistente
        Optional<ProntuarioVO> resultado = prontuarioService.atualizar(99L, prontuarioRequestDTO);

        // Verifica se o resultado é um Optional vazio
        assertThat(resultado).isEmpty();
        // Confirma que o método save nunca foi chamado, pois o prontuário não existe
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve deletar um prontuário existente e retornar true")
    void deletar_quandoEncontrado() {
        // Simula que o prontuário com ID 1 existe no repositório
        when(prontuarioRepository.existsById(1L)).thenReturn(true);
        // Configura o mock para não fazer nada quando deleteById for chamado
        doNothing().when(prontuarioRepository).deleteById(1L);

        // Executa o método de deleção e armazena o resultado
        boolean resultado = prontuarioService.deletar(1L);

        // Verifica se o resultado é verdadeiro
        assertThat(resultado).isTrue();
        // Confirma que o método deleteById foi chamado com ID 1
        verify(prontuarioRepository).deleteById(1L);
    }
}
