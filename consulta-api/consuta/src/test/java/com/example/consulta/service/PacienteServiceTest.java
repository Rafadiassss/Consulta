package com.example.consulta.service;

import com.example.consulta.model.Paciente;
import com.example.consulta.repository.PacienteRepository;
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
@DisplayName("Testes da Camada de Serviço de Pacientes")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("Carlos Souza");
        paciente.setCpf("111.222.333-44");
    }

    @Test
    @DisplayName("Deve buscar um paciente por ID existente")
    void buscarPorId_quandoEncontrado() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        Optional<Paciente> resultado = pacienteService.buscarPorId(1L);
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve salvar um paciente com sucesso")
    void salvar() {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);
        Paciente resultado = pacienteService.salvar(new Paciente());
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar um paciente existente com sucesso")
    void atualizar_quandoEncontrado() {
        Paciente dadosNovos = new Paciente();
        dadosNovos.setCpf("999.999.999-99");

        // Simula a busca do paciente que será atualizado.
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        // Simula a ação de salvar, retornando o paciente com os dados já atualizados.
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(dadosNovos);

        // Chama o método de serviço.
        Paciente resultado = pacienteService.atualizar(1L, dadosNovos);

        // Verifica se o paciente retornado tem os dados atualizados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCpf()).isEqualTo("999.999.999-99");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar paciente inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula que o paciente com ID 99 não foi encontrado.
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            pacienteService.atualizar(99L, new Paciente());
        });

        // Garante que o método 'save' NUNCA foi chamado neste cenário.
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Deve deletar um paciente por ID")
    void deletar() {
        doNothing().when(pacienteRepository).deleteById(1L);
        pacienteService.deletar(1L);
        verify(pacienteRepository, times(1)).deleteById(1L);
    }
}