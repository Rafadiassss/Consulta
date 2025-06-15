package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para receber os dados de criação/atualização de um Exame.
 */
public record ExameRequestDTO(
        @NotBlank(message = "O nome do exame não pode ser vazio") String nome,
        String resultado,
        String observacoes,
        @NotNull(message = "O ID da consulta é obrigatório") Long prontuarioId) {

}