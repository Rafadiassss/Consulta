package com.example.consulta.vo;

/**
 * VO (Value Object) - Representa um Exame na camada de negócio.
 * É o objeto de troca entre o Service e o Controller.
 */
public record ExameVO(
        Long id,
        String nome,
        String resultado,
        String observacoes,
        Long consultaId // Apenas o ID da consulta relacionada
) {
}