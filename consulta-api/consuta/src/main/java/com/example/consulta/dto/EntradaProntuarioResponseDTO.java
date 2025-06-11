package com.example.consulta.dto;

import java.time.LocalDateTime;

// DTO para a resposta da API de uma entrada do prontuário.
public record EntradaProntuarioResponseDTO(
        Long id,
        LocalDateTime dataEntrada,
        String diagnostico,
        String tratamento,
        String observacoes) {
}