package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;

// DTO para criar a "casca" de um novo Prontuário.
public record ProntuarioRequestDTO(
        @NotBlank(message = "O número do prontuário é obrigatório") String numero) {
}