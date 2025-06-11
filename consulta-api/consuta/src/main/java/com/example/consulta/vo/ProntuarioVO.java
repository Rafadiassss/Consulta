package com.example.consulta.vo;

import java.util.List;

// Objeto de negócio que representa um Prontuário e suas entradas.
public record ProntuarioVO(
        Long id,
        String numero,
        List<EntradaProntuarioVO> entradas // Usa o VO da Entrada
) {
}