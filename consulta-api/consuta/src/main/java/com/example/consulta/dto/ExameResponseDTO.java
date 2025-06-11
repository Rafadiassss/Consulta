package com.example.consulta.dto;

/**
 * DTO para enviar os dados de um Exame como resposta da API.
 */
public record ExameResponseDTO(
        Long id,
        String nome,
        String resultado,
        String observacoes,
        Long consultaId) {
}