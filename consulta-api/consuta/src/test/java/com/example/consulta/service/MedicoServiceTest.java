package com.example.consulta.service;

import com.example.consulta.model.Medico;
import com.example.consulta.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Médicos")
class MedicoServiceTest {

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private MedicoService medicoService;

    private Medico medico;

    @BeforeEach
    void setUp() {
        medico = new Medico();
        medico.setId(1L);
        medico.setNome("Dra. Ana Oliveira");
        medico.setCrm("12345-SP");
    }

    @Test
    @DisplayName("Deve listar todos os médicos")
    void listarTodos() {
        // Simula o repositório retornando uma lista com um médico.
        when(medicoRepository.findAll()).thenReturn(Collections.singletonList(medico));

        // Chama o método do serviço.
        List<Medico> resultado = medicoService.listarTodos();

        // Verifica se o resultado está correto.
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar um médico por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Simula o repositório encontrando o médico pelo ID.
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        // Chama o método do serviço.
        Optional<Medico> resultado = medicoService.buscarPorId(1L);

        // Verifica se o médico foi encontrado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getCrm()).isEqualTo("12345-SP");
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Simula o repositório não encontrando o médico.
        when(medicoRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço.
        Optional<Medico> resultado = medicoService.buscarPorId(99L);

        // Verifica se o resultado está vazio.
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar um médico com sucesso")
    void salvar() {
        // Simula o repositório salvando e retornando o médico.
        when(medicoRepository.save(any(Medico.class))).thenReturn(medico);

        // Chama o serviço para salvar.
        Medico resultado = medicoService.salvar(new Medico());

        // Verifica se o médico salvo foi retornado.
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um médico por ID")
    void deletar() {
        // Configura o mock para a chamada de 'deleteById'.
        doNothing().when(medicoRepository).deleteById(1L);

        // Chama o serviço para deletar.
        medicoService.deletar(1L);

        // Confirma que o método do repositório foi invocado uma vez.
        verify(medicoRepository, times(1)).deleteById(1L);
    }
}