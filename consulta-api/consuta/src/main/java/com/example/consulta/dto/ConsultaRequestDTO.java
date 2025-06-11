package com.example.consulta.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

// DTO para receber os dados de criação/atualização de uma Consulta.
// Note que recebemos apenas os IDs das entidades relacionadas.
public record ConsultaRequestDTO(
        @NotNull(message = "A data não pode ser nula") @Future(message = "A data da consulta deve ser no futuro") LocalDateTime data,

        @NotBlank(message = "O status não pode ser vazio") String status,

        @NotBlank(message = "O nome da consulta não pode ser vazio") String nomeConsulta,

        @NotNull(message = "O ID do paciente é obrigatório") Long pacienteId,

        @NotNull(message = "O ID do médico é obrigatório") Long medicoId,

        Long pagamentoId,

        Long prontuarioId) {
}