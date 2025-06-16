package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para criar a "casca" de um novo Prontuário.
public record ConsultaRequestDTO(
        @NotBlank(message = "O número do Cuonsulta é obrigatório") String numero,
        @NotNull(message = "O ID da agenda é obrigatório") Long agendaId ){
}