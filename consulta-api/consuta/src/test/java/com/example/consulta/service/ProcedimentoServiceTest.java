package com.example.consulta.service;

import com.example.consulta.dto.ProcedimentoRequestDTO;
import com.example.consulta.model.Procedimento;
import com.example.consulta.repository.ProcedimentoRepository;
import com.example.consulta.vo.ProcedimentoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Procedimentos")
class ProcedimentoServiceTest {

    @Mock
    private ProcedimentoRepository procedimentoRepository;

    @InjectMocks
    private ProcedimentoService procedimentoService;

    private Procedimento procedimento;
    private ProcedimentoRequestDTO procedimentoRequestDTO;

    @BeforeEach
    void setUp() {
        procedimento = new Procedimento();
        procedimento.setNome("Consulta de Rotina");
        procedimento.setValor(150.0);
        ReflectionTestUtils.setField(procedimento, "id", 1L);

        procedimentoRequestDTO = new ProcedimentoRequestDTO("Eletrocardiograma", "Exame do coração", 200.0);
    }

    @Test
    @DisplayName("Deve listar todos os procedimentos")
    void listarTodos() {
        when(procedimentoRepository.findAll()).thenReturn(Collections.singletonList(procedimento));
        List<ProcedimentoVO> resultado = procedimentoService.listarTodos();
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar um procedimento por ID existente")
    void buscarPorId_quandoEncontrado() {
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        Optional<ProcedimentoVO> resultado = procedimentoService.buscarPorId(1L);
        assertThat(resultado).isPresent();
        assertThat(resultado.get().nome()).isEqualTo("Consulta de Rotina");
    }

    @Test
    @DisplayName("Deve salvar um procedimento com sucesso")
    void salvar() {
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        ProcedimentoVO resultado = procedimentoService.salvar(procedimentoRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve atualizar um procedimento existente com sucesso")
    void atualizar_quandoEncontrado() {
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        Optional<ProcedimentoVO> resultado = procedimentoService.atualizar(1L, procedimentoRequestDTO);

        assertThat(resultado).isPresent();
        verify(procedimentoRepository).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar procedimento inexistente")
    void atualizar_quandoNaoEncontrado() {
        when(procedimentoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProcedimentoVO> resultado = procedimentoService.atualizar(99L, procedimentoRequestDTO);

        assertThat(resultado).isEmpty();
        verify(procedimentoRepository, never()).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve deletar um procedimento existente e retornar true")
    void deletar_quandoEncontrado() {
        when(procedimentoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(procedimentoRepository).deleteById(1L);

        boolean resultado = procedimentoService.deletar(1L);

        assertThat(resultado).isTrue();
        verify(procedimentoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar procedimento inexistente")
    void deletar_quandoNaoEncontrado() {
        when(procedimentoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = procedimentoService.deletar(99L);

        assertThat(resultado).isFalse();
        verify(procedimentoRepository, never()).deleteById(anyLong());
    }
}