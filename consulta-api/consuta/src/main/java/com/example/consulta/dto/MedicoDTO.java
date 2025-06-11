package com.example.consulta.dto;

// DTO que contém apenas as informações essenciais de um Médico para a resposta da Consulta.
public record MedicoDTO(
        Long id,
        String nome,
        String especialidade) {
}