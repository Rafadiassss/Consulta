package com.example.consulta.vo;

import java.util.List;

// Objeto de negócio que representa um Prontuário e suas entradas.
public record ConsultaVO(
        Long id,
        String numero,
        Long agendaId,
        List<EntradaConsultaVO> entradas // Usa o VO da Entrada
) {
}