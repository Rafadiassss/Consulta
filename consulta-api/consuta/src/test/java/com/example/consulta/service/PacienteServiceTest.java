package com.example.consulta.service;

import com.example.consulta.dto.PacienteRequestDTO;
import com.example.consulta.enums.TipoUsuario;
import com.example.consulta.model.Paciente;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.vo.PacienteVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Pacientes")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;
    private PacienteRequestDTO pacienteRequestDTO;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setNome("Carlos Souza");
        paciente.setCpf("111.222.333-44");
        paciente.setTipo(TipoUsuario.PACIENTE);
        ReflectionTestUtils.setField(paciente, "id", 1L);

        pacienteRequestDTO = new PacienteRequestDTO("Carlos Souza", "carlos.s", "senha", "c@email.com", "123",
                LocalDate.now().minusYears(20), "111.222.333-44", "98765");
    }

    @Test
    @DisplayName("Deve listar todos os pacientes")
    void listarTodos() {
        when(pacienteRepository.findAll()).thenReturn(Collections.singletonList(paciente));
        List<PacienteVO> resultado = pacienteService.listarTodos();
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve salvar um paciente com sucesso")
    void salvar() {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        PacienteVO resultado = pacienteService.salvar(pacienteRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo("111.222.333-44");
    }

    @Test
    @DisplayName("Deve atualizar um paciente existente com sucesso")
    void atualizar_quandoEncontrado() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente);

        Optional<PacienteVO> resultado = pacienteService.atualizar(1L, pacienteRequestDTO);

        assertThat(resultado).isPresent();
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar paciente inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula que o paciente com ID 99 não foi encontrado.
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método de serviço.
        Optional<PacienteVO> resultado = pacienteService.atualizar(99L, pacienteRequestDTO);

        // A verificação espera um Optional vazio.
        assertThat(resultado).isEmpty();

        // Garante que o método 'save' NUNCA foi chamado neste cenário.
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Deve deletar um paciente existente e retornar true")
    void deletar_quandoEncontrado() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        boolean resultado = pacienteService.deletar(1L);

        assertThat(resultado).isTrue();
        verify(pacienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar paciente inexistente")
    void deletar_quandoNaoEncontrado() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        boolean resultado = pacienteService.deletar(99L);

        assertThat(resultado).isFalse();
        verify(pacienteRepository, never()).deleteById(anyLong());
    }
}