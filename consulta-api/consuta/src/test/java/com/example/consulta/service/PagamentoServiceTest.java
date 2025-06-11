package com.example.consulta.service;

import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Pagamentos")
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        pagamento = new Pagamento(
                LocalDate.of(2025, 6, 10),
                new BigDecimal("250.00"),
                "Cartão de Crédito",
                "CONFIRMADO");
        // Supondo que a entidade tenha um setId para facilitar os testes.
        // pagamento.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void listarTodos() {
        // Simula o repositório retornando uma lista com um pagamento.
        when(pagamentoRepository.findAll()).thenReturn(Collections.singletonList(pagamento));

        // Chama o método de serviço.
        List<Pagamento> resultado = pagamentoService.listarTodos();

        // Verifica se o resultado está correto.
        assertThat(resultado).isNotNull().hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar um pagamento por ID existente")
    void buscarPorId_quandoEncontrado() {
        // Simula o repositório encontrando o pagamento pelo ID.
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        // Chama o método de serviço.
        Optional<Pagamento> resultado = pagamentoService.buscarPorId(1L);

        // Verifica se o pagamento foi encontrado.
        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao buscar por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Simula o repositório não encontrando o pagamento.
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método de serviço.
        Optional<Pagamento> resultado = pagamentoService.buscarPorId(99L);

        // Verifica se o resultado está vazio.
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar um pagamento com sucesso")
    void salvar() {
        // Simula o repositório salvando e retornando o pagamento.
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        // Chama o serviço para salvar.
        Pagamento resultado = pagamentoService.salvar(new Pagamento());

        // Verifica se o pagamento foi retornado.
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um pagamento por ID")
    void deletar() {
        // Configura o mock para a chamada de 'deleteById'.
        doNothing().when(pagamentoRepository).deleteById(1L);

        // Chama o serviço para deletar.
        pagamentoService.deletar(1L);

        // Confirma que o método do repositório foi invocado.
        verify(pagamentoRepository, times(1)).deleteById(1L);
    }
}