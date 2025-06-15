package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;

// DTO para receber os dados de uma nova entrada no prontuário.
public record EntradaConsultaRequestDTO(
        @NotBlank(message = "O diagnóstico não pode ser vazio") String diagnostico,
        String tratamento,
        String observacoes) {
}