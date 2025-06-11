package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO para receber os dados de criação/atualização de um Procedimento.
 */
public record ProcedimentoRequestDTO(
        @NotBlank(message = "O nome do procedimento não pode ser vazio") String nome,

        String descricao,

        @PositiveOrZero(message = "O valor não pode ser negativo") double valor) {
}