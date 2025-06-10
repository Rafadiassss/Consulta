package com.example.consulta.dto;

/**
 * DTO para enviar dados de uma Especialidade como resposta da API.
 * Inclui o 'id' para que o cliente saiba qual é o identificador do recurso.
 */
public record EspecialidadeResponseDTO(
    Long id,
    String nome,
    String descricao
) {}