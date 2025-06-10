package com.example.consulta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes da Entidade Pagamento")
class PagamentoTest {

    @Test
    @DisplayName("Deve alterar o status para 'Confirmado' ao chamar confirmarPagamento")
    void confirmarPagamento_deveAlterarStatus() {
        // Cria uma instância do Pagamento com o status inicial 'PENDENTE'.
        Pagamento pagamento = new Pagamento(
                LocalDate.now(),
                new BigDecimal("150.00"),
                "PIX",
                "PENDENTE");

        // Chama o método
        pagamento.confirmarPagamento();

        // Verifica se o estado do campo 'status' foi alterado como esperado.
        assertThat(pagamento.getStatus()).isEqualTo("Confirmado");
    }
}