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
        when(prontuarioRepository.findAll()).thenReturn(Collections.singletonList(prontuario2));
        List<ProntuarioVO> resultado = prontuarioService.listarTodos();
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve salvar um prontuário com sucesso")
    void salvar_comSucesso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario2);

        ProntuarioVO resultado = prontuarioService.salvar(prontuarioRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.paciente().getId()).isEqualTo(1L);
        verify(prontuarioRepository).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se o paciente não for encontrado")
    void salvar_quandoPacienteNaoEncontrado() {
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            prontuarioService.salvar(prontuarioRequestDTO);
        });

        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve atualizar um prontuário com sucesso")
    void atualizar_quandoEncontrado() {
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario2));
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.of(medico));
        when(prontuarioRepository.save(any(Prontuario.class))).thenReturn(prontuario2);

        Optional<ProntuarioVO> resultado = prontuarioService.atualizar(1L, prontuarioRequestDTO);

        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar prontuário inexistente")
    void atualizar_quandoNaoEncontrado() {
        when(prontuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProntuarioVO> resultado = prontuarioService.atualizar(99L, prontuarioRequestDTO);

        assertThat(resultado).isEmpty();
        verify(prontuarioRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve deletar um prontuário existente e retornar true")
    void deletar_quandoEncontrado() {
        when(prontuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(prontuarioRepository).deleteById(1L);

        boolean resultado = prontuarioService.deletar(1L);

        assertThat(resultado).isTrue();
        verify(prontuarioRepository).deleteById(1L);
    }
}
