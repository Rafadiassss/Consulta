package com.example.consulta.service;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.ConsultaRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Camada de Serviço de Pagamentos")
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;
    private Consulta consulta;
    private PagamentoRequestDTO pagamentoRequestDTO;

    @BeforeEach
    void setUp() {
        // Prepara uma consulta de base para associação.
        consulta = new Consulta();
        ReflectionTestUtils.setField(consulta, "id", 1L);

        // Prepara um pagamento de base, já associado à consulta.
        pagamento = new Pagamento(LocalDate.now(), new BigDecimal("150.00"), "PIX", "CONFIRMADO");
        pagamento.setConsulta(consulta);
        ReflectionTestUtils.setField(pagamento, "id", 1L);

        // Prepara um DTO para as requisições.
        pagamentoRequestDTO = new PagamentoRequestDTO(LocalDate.now(), new BigDecimal("150.00"), "PIX", "PENDENTE", 1L);
    }

    @Test
    @DisplayName("Deve salvar um pagamento com sucesso")
    void salvar_comSucesso() {
        // Simula o repositório encontrando a consulta associada.
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        // Simula a ação de salvar, que retorna a entidade com ID.
        when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);

        // Chama o método de serviço com o DTO.
        PagamentoVO resultado = pagamentoService.salvar(pagamentoRequestDTO);

        // Verifica se o VO retornado tem os dados esperados.
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.consultaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se a consulta associada não existir")
    void salvar_quandoConsultaNaoExiste() {
        // Simula o repositório NÃO encontrando a consulta.
        when(consultaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica se a chamada ao serviço lança a exceção esperada.
        assertThrows(RuntimeException.class, () -> {
            pagamentoService.salvar(pagamentoRequestDTO);
        });

        // Garante que, com o erro, o pagamento nunca foi salvo.
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("Deve deletar um pagamento existente e retornar true")
    void deletar_quandoEncontrado() {
        // Simula que o pagamento existe.
        when(pagamentoRepository.existsById(1L)).thenReturn(true);
        // Configura a chamada ao método 'deleteById'.
        doNothing().when(pagamentoRepository).deleteById(1L);

        // Chama o método de serviço.
        boolean resultado = pagamentoService.deletar(1L);

        // Verifica o resultado e a chamada ao repositório.
        assertThat(resultado).isTrue();
        verify(pagamentoRepository).deleteById(1L);
    }
}