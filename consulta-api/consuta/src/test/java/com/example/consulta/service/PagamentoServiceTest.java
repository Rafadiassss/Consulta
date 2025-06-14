package com.example.consulta.service;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.PagamentoRepository;
import com.example.consulta.vo.PagamentoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
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
    private PagamentoRequestDTO pagamentoRequestDTO;

    @BeforeEach
    void setUp() {

        pagamento = new Pagamento(LocalDate.now(), new BigDecimal("150.00"), "PIX", "CONFIRMADO");
        // Define o ID usando ReflectionTestUtils, como se fosse gerado pelo banco de
        // dados
        ReflectionTestUtils.setField(pagamento, "id", 1L);

        // Inicializa o DTO de requisição usando o construtor do record
        pagamentoRequestDTO = new PagamentoRequestDTO(
                LocalDate.now(),
                new BigDecimal("150.00"),
                "PIX",
                "PENDENTE");
    }

    @Test
    @DisplayName("Deve salvar um pagamento com sucesso")
    void salvar_comSucesso() {
        // Simula o save do repositório retornando a entidade 'pagamento' com ID
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        // Chama o método do serviço
        PagamentoVO resultado = pagamentoService.salvar(pagamentoRequestDTO);

        // Verifica se o resultado é válido e tem o ID esperado
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.dataPagamento()).isEqualTo(pagamentoRequestDTO.dataPagamento());
        assertThat(resultado.valorPago()).isEqualTo(pagamentoRequestDTO.valorPago());
        assertThat(resultado.formaPagamento()).isEqualTo(pagamentoRequestDTO.formaPagamento());
        assertThat(resultado.status()).isEqualTo(pagamento.getStatus());

        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void buscarPorId_comSucesso() {
        // Simula o findById do repositório retornando a entidade 'pagamento'
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));

        // Chama o método do serviço
        Optional<PagamentoVO> resultado = pagamentoService.buscarPorId(1L);

        // Verifica se o resultado está presente e tem o ID correto
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
        // Verifica se o método findById do repositório foi chamado uma vez
        verify(pagamentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar pagamento por ID inexistente")
    void buscarPorId_quandoNaoEncontrado() {
        // Simula o findById retornando Optional vazio
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço
        Optional<PagamentoVO> resultado = pagamentoService.buscarPorId(99L);

        // Verifica se o resultado está vazio
        assertThat(resultado).isNotPresent();
        // Verifica se o método findById do repositório foi chamado uma vez
        verify(pagamentoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos com sucesso")
    void listarTodos_comSucesso() {
        // Cria uma segunda entidade para a lista
        Pagamento pagamento2 = new Pagamento(LocalDate.now().minusDays(1), BigDecimal.valueOf(50.00), "Boleto",
                "Pendente");
        ReflectionTestUtils.setField(pagamento2, "id", 2L);

        // Simula o findAll do repositório retornando uma lista de entidades
        when(pagamentoRepository.findAll()).thenReturn(Arrays.asList(pagamento, pagamento2));

        // Chama o método do serviço
        List<PagamentoVO> resultados = pagamentoService.listarTodos();

        // Verifica o tamanho e os IDs dos resultados
        assertThat(resultados).isNotEmpty();
        assertThat(resultados).hasSize(2);
        assertThat(resultados.get(0).id()).isEqualTo(1L);
        assertThat(resultados.get(1).id()).isEqualTo(2L);
        // Verifica se o método findAll do repositório foi chamado uma vez
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver pagamentos")
    void listarTodos_quandoVazio() {
        // Simula o findAll retornando uma lista vazia
        when(pagamentoRepository.findAll()).thenReturn(Collections.emptyList());

        // Chama o método do serviço
        List<PagamentoVO> resultados = pagamentoService.listarTodos();

        // Verifica se a lista está vazia
        assertThat(resultados).isEmpty();
        // Verifica se o método findAll do repositório foi chamado uma vez
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve deletar um pagamento existente e retornar true")
    void deletar_comSucesso() {
        // Simula que o pagamento existe
        when(pagamentoRepository.existsById(1L)).thenReturn(true);
        // Simula a deleção (não faz nada)
        doNothing().when(pagamentoRepository).deleteById(1L);

        // Chama o método do serviço
        boolean resultado = pagamentoService.deletar(1L);

        // Verifica se a deleção foi bem-sucedida
        assertThat(resultado).isTrue();
        // Verifica se os métodos do repositório foram chamados
        verify(pagamentoRepository, times(1)).existsById(1L);
        verify(pagamentoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar false ao tentar deletar pagamento inexistente")
    void deletar_quandoNaoEncontrado() {
        // Simula que o pagamento não existe
        when(pagamentoRepository.existsById(99L)).thenReturn(false);

        // Chama o método do serviço
        boolean resultado = pagamentoService.deletar(99L);

        // Verifica se a deleção falhou (não encontrou)
        assertThat(resultado).isFalse();
        // Verifica se o método existsById foi chamado, mas deleteById não
        verify(pagamentoRepository, times(1)).existsById(99L);
        verify(pagamentoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve atualizar um pagamento com sucesso")
    void atualizar_comSucesso() {
        // DTO com dados para atualização
        PagamentoRequestDTO updatedDto = new PagamentoRequestDTO(
                LocalDate.now().plusDays(1),
                new BigDecimal("200.00"),
                "Cartao Credito",
                "Concluido");

        // Simula a entidade que seria retornada após a atualização
        Pagamento updatedPagamentoEntity = new Pagamento(updatedDto.dataPagamento(), updatedDto.valorPago(),
                updatedDto.formaPagamento(), updatedDto.status());
        ReflectionTestUtils.setField(updatedPagamentoEntity, "id", 1L);

        // Simula o findById retornando o pagamento original
        when(pagamentoRepository.findById(1L)).thenReturn(Optional.of(pagamento));
        // Simula o save retornando a entidade atualizada
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(updatedPagamentoEntity);

        // Chama o método do serviço
        Optional<PagamentoVO> resultado = pagamentoService.atualizar(1L, updatedDto);

        // Verifica se o resultado está presente e se os dados foram atualizados
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
        assertThat(resultado.get().valorPago()).isEqualTo(updatedDto.valorPago());
        assertThat(resultado.get().formaPagamento()).isEqualTo(updatedDto.formaPagamento());
        assertThat(resultado.get().status()).isEqualTo(updatedDto.status());
        // Verifica se os métodos do repositório foram chamados
        verify(pagamentoRepository, times(1)).findById(1L);
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve retornar vazio ao tentar atualizar pagamento inexistente")
    void atualizar_quandoNaoEncontrado() {
        // Simula que o pagamento não existe
        when(pagamentoRepository.findById(99L)).thenReturn(Optional.empty());

        // Chama o método do serviço
        Optional<PagamentoVO> resultado = pagamentoService.atualizar(99L, pagamentoRequestDTO);

        // Verifica se o resultado está vazio
        assertThat(resultado).isNotPresent();
        // Verifica se findById foi chamado, mas save não
        verify(pagamentoRepository, times(1)).findById(99L);
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }
}