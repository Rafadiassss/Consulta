package com.example.consulta.vo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AgendaVO(
        Long id,
        LocalDate dataAgendada,
        List<LocalTime> horarios) {
}