package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para receber os dados de criação/atualização de um Pagamento.
 */
public record PagamentoRequestDTO(
        @NotNull(message = "A data do pagamento não pode ser nula") LocalDate dataPagamento,

        @NotNull(message = "O valor pago não pode ser nulo") @Positive(message = "O valor pago deve ser positivo") BigDecimal valorPago,

        @NotBlank(message = "A forma de pagamento não pode ser vazia") String formaPagamento,

        @NotBlank(message = "O status não pode ser vazio") String status,

        @NotNull(message = "O ID da consulta é obrigatório") Long consultaId) {
}