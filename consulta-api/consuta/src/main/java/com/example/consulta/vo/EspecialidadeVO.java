package com.example.consulta.vo;

/**
 * VO (Value Object) - Representa o valor de uma Especialidade
 * na camada de negócio. É um objeto de dados puro e imutável.
 */
public record EspecialidadeVO(
    Long id,
    String nome,
    String descricao
) {}