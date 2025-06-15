package com.example.consulta.vo;

import java.time.LocalDateTime;

// Objeto de negócio que representa uma entrada no histórico.
public record EntradaConsultaVO(
        Long id,
        LocalDateTime dataEntrada,
        String diagnostico,
        String tratamento,
        String observacoes) {
}