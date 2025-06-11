package com.example.consulta.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * VO (Value Object) - Representa um Pagamento na camada de negócio.
 */
public record PagamentoVO(
        Long id,
        LocalDate dataPagamento,
        BigDecimal valorPago,
        String formaPagamento,
        String status,
        Long consultaId) {
}