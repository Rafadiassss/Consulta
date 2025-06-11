package com.example.consulta.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para receber os dados de criação de uma nova Agenda.
 * Contém validações para garantir a integridade dos dados de entrada.
 */
public record AgendaRequestDTO(
        @NotNull(message = "A data agendada não pode ser nula.") @FutureOrPresent(message = "A data agendada não pode ser no passado.") LocalDate dataAgendada,

        @NotEmpty(message = "A lista de horários não pode ser vazia.") List<LocalTime> horarios) {
}