package com.example.consulta.service;

import com.example.consulta.model.Procedimento;
import com.example.consulta.repository.ProcedimentoRepository;
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
@DisplayName("Testes da Camada de Serviço de Procedimentos")
class ProcedimentoServiceTest {

    @Mock
    private ProcedimentoRepository procedimentoRepository;

    @InjectMocks
    private ProcedimentoService procedimentoService;

    private Procedimento procedimento;

    @BeforeEach
    void setUp() {
        procedimento = new Procedimento();
        procedimento.setId(1L);
        procedimento.setNome("Consulta Padrão");
        procedimento.setValor(250.00);
    }

    @Test
    @DisplayName("Deve listar todos os procedimentos")
    void listarTodos() {
        // Simula o retorno do repositório para o método findAll.
        when(procedimentoRepository.findAll()).thenReturn(Collections.singletonList(procedimento));

        // Chama o método do serviço a ser testado.
        List<Procedimento> resultado = procedimentoService.listarTodos();

        // Verifica se o resultado retornado está correto.
        assertThat(resultado).isNotNull().hasSize(1);
        // Confirma que o método do repositório foi invocado.
        verify(procedimentoRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar um procedimento por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Simula o repositório encontrando o procedimento pelo ID.
        when(procedimentoRepository.findById(1L)).thenReturn(Optional.of(procedimento));

        // Chama o método de busca do serviço.
        Optional<Procedimento> resultado = procedimentoService.buscarPorId(1L);

        // Verifica se o procedimento foi encontrado.
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Simula o repositório não encontrando o procedimento.
        when(procedimentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método de busca do serviço.
        Optional<Procedimento> resultado = procedimentoService.buscarPorId(99L);

        // Verifica se o resultado está vazio, como esperado.
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar um procedimento com sucesso")
    void salvar() {
        // Simula o repositório salvando e retornando o procedimento.
        when(procedimentoRepository.save(any(Procedimento.class))).thenReturn(procedimento);

        // Chama o serviço para salvar um novo procedimento.
        Procedimento resultado = procedimentoService.salvar(new Procedimento());

        // Verifica se o procedimento salvo foi retornado corretamente.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Consulta Padrão");
    }

    @Test
    @DisplayName("Deve deletar um procedimento por ID")
    void deletar() {
        // Configura o mock para a chamada do método 'deleteById', que não retorna nada.
        doNothing().when(procedimentoRepository).deleteById(1L);

        // Chama o serviço para deletar o procedimento.
        procedimentoService.deletar(1L);

        // Confirma que o método 'deleteById' do repositório foi chamado uma vez com o
        // ID correto.
        verify(procedimentoRepository, times(1)).deleteById(1L);
    }
}