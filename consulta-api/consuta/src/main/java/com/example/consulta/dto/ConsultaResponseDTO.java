package com.example.consulta.dto;

import java.time.LocalDateTime;

// DTO para enviar a resposta da API.
// Usa os DTOs (MedicoDTO e PacienteDTO) para as entidades relacionadas.
public record ConsultaResponseDTO(
        Long id,
        LocalDateTime data,
        String status,
        String nomeConsulta,
        PacienteDTO paciente,
        MedicoDTO medico,
        Long pagamentoId // Enviando apenas o ID do pagamento associado
) {
}