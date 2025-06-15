package com.example.consulta.dto;

import java.util.List;

// DTO para a resposta da API, mostrando o Prontuário e seu histórico.
public record ConsultaResponseDTO(
        Long id,
        String numero,
        List<EntradaConsultaResponseDTO> entradas // Usa o DTO de resposta da Entrada
) {
}