package com.example.consulta.service;

import com.example.consulta.dto.AgendaRequestDTO;
import com.example.consulta.model.Agenda;
import com.example.consulta.repository.AgendaRepository;
import com.example.consulta.vo.AgendaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Agendas")
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda agenda;
    private AgendaRequestDTO agendaRequestDTO;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setDataAgendada(LocalDate.of(2025, 10, 20));
        agenda.setHorarios(List.of(LocalTime.of(9, 0)));

        agendaRequestDTO = new AgendaRequestDTO(
                LocalDate.of(2025, 10, 20),
                List.of(LocalTime.of(9, 0), LocalTime.of(10, 0)));
    }

    @Test
    @DisplayName("Deve listar todas as agendas e converter para VO")
    void listarTodas() {
        when(agendaRepository.findAll()).thenReturn(Collections.singletonList(agenda));

        List<AgendaVO> resultado = agendaService.listarTodas();

        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
        verify(agendaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar por ID existente e retornar um Optional de VO")
    void buscarPorId_quandoEncontrado() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        Optional<AgendaVO> resultado = agendaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().dataAgendada()).isEqualTo(LocalDate.of(2025, 10, 20));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AgendaVO> resultado = agendaService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar a partir de um DTO e retornar um VO")
    void salvar() {
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        AgendaVO resultado = agendaService.salvar(agendaRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve atualizar uma agenda existente com sucesso")
    void atualizar_quandoEncontrado() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        Optional<AgendaVO> resultado = agendaService.atualizar(1L, agendaRequestDTO);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar agenda inexistente")
    void atualizar_quandoNaoEncontrado() {
        when(agendaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<AgendaVO> resultado = agendaService.atualizar(99L, agendaRequestDTO);

        assertThat(resultado).isEmpty();
        verify(agendaRepository, never()).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Deve deletar uma agenda existente e retornar true")
    void deletar_quandoEncontrado() {
        when(agendaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(agendaRepository).deleteById(1L);

        boolean resultado = agendaService.deletar(1L);

        assertThat(resultado).isTrue();
        verify(agendaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar agenda inexistente")
    void deletar_quandoNaoEncontrado() {
        when(agendaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = agendaService.deletar(99L);

        assertThat(resultado).isFalse();
        verify(agendaRepository, never()).deleteById(anyLong());
    }
}