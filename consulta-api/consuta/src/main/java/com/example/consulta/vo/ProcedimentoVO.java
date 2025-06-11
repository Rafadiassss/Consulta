package com.example.consulta.vo;

/**
 * VO (Value Object) - Representa um Procedimento na camada de negócio.
 */
public record ProcedimentoVO(
        Long id,
        String nome,
        String descricao,
        double valor) {
}