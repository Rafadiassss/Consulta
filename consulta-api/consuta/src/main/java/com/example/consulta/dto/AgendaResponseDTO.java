package com.example.consulta.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para enviar os dados de uma Agenda como resposta da API.
 * Inclui o ID para que o cliente possa identificar o recurso.
 */
public record AgendaResponseDTO(
        Long id,
        LocalDate dataAgendada,
        List<LocalTime> horarios) {
}