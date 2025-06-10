package com.example.consulta.dto;

// DTO que contém apenas as informações essenciais de um Paciente para a resposta da Consulta.
public record PacienteDTO(
        Long id,
        String nome) {
}