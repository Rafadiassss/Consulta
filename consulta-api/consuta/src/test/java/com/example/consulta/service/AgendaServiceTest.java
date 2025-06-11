package com.example.consulta.service;

import com.example.consulta.model.Agenda;
import com.example.consulta.repository.AgendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Agendas")
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agenda = new Agenda(LocalDate.of(2025, 10, 20));
        agenda.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todas as agendas com sucesso")
    void listarTodas() {
        // Configura o mock do repositório para retornar uma lista com nossa agenda.
        when(agendaRepository.findAll()).thenReturn(Collections.singletonList(agenda));

        // Chama o método do serviço.
        List<Agenda> resultado = agendaService.listarTodas();

        // Verifica se a lista retornada não é nula e contém um item.
        assertThat(resultado).isNotNull().hasSize(1);
        // Garante que o método 'findAll' do repositório foi chamado.
        verify(agendaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar uma agenda por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Configura o mock do repositório para retornar a agenda quando o ID for
        // encontrado.
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        // Chama o método do serviço.
        Optional<Agenda> resultado = agendaService.buscarPorId(1L);

        // Verifica se o Optional contém o objeto esperado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Configura o mock para retornar um Optional vazio para um ID que não existe.
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço.
        Optional<Agenda> resultado = agendaService.buscarPorId(99L);

        // Verifica se o Optional está vazio.
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar uma agenda com sucesso")
    void salvar() {
        // Configura o mock para retornar a agenda quando o método 'save' for chamado.
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        // Chama o método de salvar do serviço.
        Agenda resultado = agendaService.salvar(new Agenda());

        // Verifica se o objeto retornado não é nulo e tem o ID esperado.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve deletar uma agenda por ID")
    void deletar() {
        // Configura o mock para não fazer nada quando 'deleteById' for chamado (método
        // void).
        doNothing().when(agendaRepository).deleteById(1L);

        // Chama o método do serviço.
        agendaService.deletar(1L);

        // Garante que o método 'deleteById' do repositório foi chamado exatamente uma
        // vez com o ID correto.
        verify(agendaRepository, times(1)).deleteById(1L);
    }
}