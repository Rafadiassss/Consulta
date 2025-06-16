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
        // Configura o mock para retornar uma lista com um único procedimento
        when(procedimentoRepository.findAll()).thenReturn(Collections.singletonList(procedimento));
        // Chama o método listarTodos() do service e armazena o resultado
        List<ProcedimentoVO> resultado = procedimentoService.listarTodos();
        // Verifica se o resultado não é nulo e contém exatamente 1 item
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar um procedimento por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Configura o mock para retornar um procedimento quando buscado pelo ID 1
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        // Executa a busca por ID através do service
        Optional<ProcedimentoVO> resultado = procedimentoService.buscarPorId(1L);
        // Verifica se o resultado está presente (não é vazio)
        assertThat(resultado).isPresent();
        // Verifica se o nome do procedimento retornado é o esperado
        assertThat(resultado.get().nome()).isEqualTo("Consulta de Rotina");
    }

    @Test
    @DisplayName("Deve salvar um procedimento com sucesso")
    void salvar() {
        // Configura o mock para simular o salvamento de qualquer procedimento e
        // retornar o procedimento de teste
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        // Executa o método de salvar passando o DTO de request
        ProcedimentoVO resultado = procedimentoService.salvar(procedimentoRequestDTO);

        // Verifica se o resultado não é nulo
        assertThat(resultado).isNotNull();
        // Verifica se o ID do procedimento salvo é igual a 1
        assertThat(resultado.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve atualizar um procedimento existente com sucesso")
    void atualizar_quandoEncontrado() {
        // Configura o mock para retornar o procedimento quando buscar pelo ID 1
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));
        // Configura o mock para retornar o procedimento ao salvar qualquer procedimento
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        // Executa a atualização do procedimento com ID 1 usando os dados do DTO
        Optional<ProcedimentoVO> resultado = procedimentoService.atualizar(1L, procedimentoRequestDTO);

        // Verifica se o resultado contém um procedimento
        assertThat(resultado).isPresent();
        // Verifica se o método save foi chamado uma vez com qualquer procedimento
        verify(procedimentoRepository).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao tentar atualizar procedimento inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Configura o mock para retornar Optional vazio ao buscar ID inexistente
        when(procedimentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Tenta atualizar procedimento com ID inexistente
        Optional<ProcedimentoVO> resultado = procedimentoService.atualizar(99L, procedimentoRequestDTO);

        // Verifica se o resultado é vazio
        assertThat(resultado).isEmpty();
        // Verifica se o método save nunca foi chamado
        verify(procedimentoRepository, never()).save(any(Procedimento.class));
    }

    @Test
    @DisplayName("Deve deletar um procedimento existente e retornar true")
    void deletar_quandoEncontrado() {
        // Configura o mock para retornar true quando verificar se existe procedimento
        // com ID 1
        when(procedimentoRepository.existsById(1L)).thenReturn(true);
        // Configura o mock para não fazer nada ao deletar procedimento com ID 1
        doNothing().when(procedimentoRepository).deleteById(1L);

        // Executa a deleção do procedimento e armazena o resultado
        boolean resultado = procedimentoService.deletar(1L);

        // Verifica se o resultado é verdadeiro
        assertThat(resultado).isTrue();
        // Verifica se o método deleteById foi chamado uma vez com ID 1
        verify(procedimentoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar procedimento inexistente")
    void deletar_quandoNaoEncontrado() {
        // Configura o mock para retornar false ao verificar ID inexistente
        when(procedimentoRepository.existsById(99L)).thenReturn(false);

        // Tenta deletar procedimento com ID inexistente
        boolean resultado = procedimentoService.deletar(99L);

        // Verifica se o resultado é falso
        assertThat(resultado).isFalse();
        // Confirma que o método deleteById nunca foi chamado
        verify(procedimentoRepository, never()).deleteById(anyLong());
    }
}