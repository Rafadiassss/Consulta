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
        // Objeto Entidade que o repositório irá simular retornar.
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setDataAgendada(LocalDate.of(2025, 10, 20));
        agenda.setHorarios(List.of(LocalTime.of(9, 0)));

        // Objeto DTO que simularemos receber do controller.
        agendaRequestDTO = new AgendaRequestDTO(
                LocalDate.of(2025, 10, 20),
                List.of(LocalTime.of(9, 0), LocalTime.of(10, 0)));
    }

    @Test
    @DisplayName("Deve listar todas as agendas e converter para VO")
    void listarTodas() {
        // Simula o repositório retornando uma lista de entidades.
        when(agendaRepository.findAll()).thenReturn(Collections.singletonList(agenda));

        // Chama o método do serviço.
        List<AgendaVO> resultado = agendaService.listarTodas();

        // Verifica se a lista de VOs foi retornada corretamente.
        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve buscar por ID e retornar um Optional de VO")
    void buscarPorId_quandoEncontrado() {
        // Simula o repositório encontrando a entidade.
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));

        // Chama o método do serviço.
        Optional<AgendaVO> resultado = agendaService.buscarPorId(1L);

        // Verifica se o VO correto foi retornado dentro do Optional.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().dataAgendada()).isEqualTo(LocalDate.of(2025, 10, 20));
    }

    @Test
    @DisplayName("Deve salvar a partir de um DTO e retornar um VO")
    void salvar() {
        // Simula a ação de salvar do repositório, que retorna a entidade com ID.
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        // Chama o método do serviço passando o DTO.
        AgendaVO resultado = agendaService.salvar(agendaRequestDTO);

        // Verifica se o VO retornado contém os dados esperados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve deletar uma agenda existente e retornar true")
    void deletar_quandoEncontrado() {
        // Simula que a agenda existe.
        when(agendaRepository.existsById(1L)).thenReturn(true);
        // Configura a chamada ao método 'deleteById'.
        doNothing().when(agendaRepository).deleteById(1L);

        // Chama o método do serviço.
        boolean resultado = agendaService.deletar(1L);

        // Verifica se o resultado é 'true' e se a exclusão foi chamada.
        assertThat(resultado).isTrue();
        verify(agendaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar agenda inexistente")
    void deletar_quandoNaoEncontrado() {
        // Simula que a agenda NÃO existe.
        when(agendaRepository.existsById(99L)).thenReturn(false);

        // Chama o método do serviço.
        boolean resultado = agendaService.deletar(99L);

        // Verifica se o resultado é 'false'.
        assertThat(resultado).isFalse();
        // Confirma que 'deleteById' NUNCA foi chamado.
        verify(agendaRepository, never()).deleteById(anyLong());
    }
}