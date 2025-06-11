package com.example.consulta.service;

import com.example.consulta.model.*;
import com.example.consulta.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Consultas")
class ConsultaServiceTest {

    // Mockando todas as 5 dependências do serviço
    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private ProntuarioRepository prontuarioRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ConsultaService consultaService;

    private Paciente paciente;
    private Medico medico;
    private Consulta consulta;
    private Prontuario prontuario;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);

        medico = new Medico();
        medico.setId(1L);

        prontuario = new Prontuario();
        prontuario.setId(1L);

        consulta = new Consulta();
        consulta.setId(1L);
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.setProntuario(prontuario);
    }

    @Test
    @DisplayName("Deve salvar uma consulta com sucesso quando todas as entidades relacionadas existem")
    void salvar_comSucesso() {
        // Simula a busca bem-sucedida de todas as entidades relacionadas.
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));
        // Simula a ação de salvar do repositório principal.
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);

        // Chama o método de salvar do serviço.
        Consulta resultado = consultaService.salvar(consulta);

        // Verifica se a consulta foi salva e retornada.
        assertThat(resultado).isNotNull();
        // Confirma que o método 'save' foi chamado.
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao salvar se o médico não for encontrado")
    void salvar_quandoMedicoNaoEncontrado() {
        // Mockando o sucesso das verificações que vêm antes da do médico.
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario));

        // Agora, a falha na busca do médico.
        when(medicoRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            consultaService.salvar(consulta);
        }, "Médico não encontrado");

        // Garante que o método 'save' NUNCA foi chamado devido ao erro anterior.
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve atualizar uma consulta com sucesso")
    void atualizar_quandoEncontrado() {
        Consulta dadosNovos = new Consulta();

        // Simula a busca da consulta existente.
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        // Simula a ação de salvar, retornando a consulta com os dados novos.
        when(consultaRepository.save(any(Consulta.class))).thenReturn(dadosNovos);

        // Chama o serviço de atualização.
        Consulta resultado = consultaService.atualizar(1L, dadosNovos);

        // Verifica se a consulta foi atualizada e retornada.
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar nulo ao tentar atualizar consulta inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula que a consulta não foi encontrada.
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o serviço de atualização.
        Consulta resultado = consultaService.atualizar(99L, new Consulta());

        // Verifica que o resultado é nulo, como definido na lógica do serviço.
        assertThat(resultado).isNull();
        // Confirma que 'save' nunca foi chamado.
        verify(consultaRepository, never()).save(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve deletar uma consulta por ID")
    void deletar() {
        // Configura o mock para a chamada de 'deleteById'.
        doNothing().when(consultaRepository).deleteById(1L);
        // Chama o serviço para deletar.
        consultaService.deletar(1L);
        // Confirma que o método do repositório foi invocado.
        verify(consultaRepository, times(1)).deleteById(1L);
    }
}