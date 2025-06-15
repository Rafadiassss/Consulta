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
@DisplayName("Testes da Camada de Serviço de Consultas")
class ConsultaServiceTest {

    @Mock
    private Prontuario prontuario;
    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private ProntuarioService prontuarioService;

    private Paciente paciente;
    private Medico medico;
    private Prontuario consulta;
    private ProntuarioRequestDTO consultaRequestDTO;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);

        medico = new Medico();
        medico.setId(2L);

        consulta = new Prontuario(LocalDateTime.now().plusDays(1), "AGENDADA", paciente, medico);
        consulta.setId(1L);

        consultaRequestDTO = new ProntuarioRequestDTO(
                LocalDateTime.now().plusDays(1), "AGENDADA", "Consulta de Rotina",
                paciente.getId(), medico.getId(), null, null);
    }

    @Test
    @DisplayName("Deve listar todas as consultas")
    void listarTodas() {
        when(consultaRepository.findAll()).thenReturn(Collections.singletonList(consulta));
        List<ProntuarioVO> resultado = prontuarioService.listarTodas();
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve salvar uma consulta com sucesso")
    void salvar_comSucesso() {
        // Simula a busca bem-sucedida das entidades relacionadas.
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(2L)).thenReturn(Optional.of(medico));
        // Simula a ação de salvar.
        when(consultaRepository.save(any(Prontuario.class))).thenReturn(consulta);

        // Chama o método de salvar do serviço.
        ProntuarioVO resultado = consultaService.salvar(consultaRequestDTO);

        // Verifica o resultado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.paciente().getId()).isEqualTo(1L);
        verify(consultaRepository).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se o paciente não for encontrado")
    void salvar_quandoPacienteNaoEncontrado() {
        // Simula a falha na busca do paciente.
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se a chamada lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            consultaService.salvar(consultaRequestDTO);
        });

        // Garante que o 'save' nunca foi chamado.
        verify(consultaRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta com sucesso")
    void atualizar_quandoEncontrado() {
        // Simula a busca da consulta e das entidades relacionadas.
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(pacienteRepository.findById(anyLong())).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.of(medico));
        // Simula a ação de salvar com os dados atualizados.
        when(consultaRepository.save(any(Prontuario.class))).thenReturn(consulta);

        // Chama o serviço de atualização.
        Optional<ProntuarioVO> resultado = consultaService.atualizar(1L, consultaRequestDTO);

        // Verifica se a consulta foi atualizada.
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar consulta inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula que a consulta a ser atualizada não existe.
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o serviço.
        Optional<ProntuarioVO> resultado = consultaService.atualizar(99L, consultaRequestDTO);

        // Verifica o resultado.
        assertThat(resultado).isEmpty();
        verify(consultaRepository, never()).save(any(Prontuario.class));
    }

    @Test
    @DisplayName("Deve deletar uma consulta existente e retornar true")
    void deletar_quandoEncontrado() {
        when(consultaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(consultaRepository).deleteById(1L);

        boolean resultado = consultaService.deletar(1L);

        assertThat(resultado).isTrue();
        verify(consultaRepository).deleteById(1L);
    }
}