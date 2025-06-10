package com.example.consulta.vo;

/**
 * VO (Value Object) - Representa um Médico na camada de negócio.
 * Contém os dados puros, sem anotações de persistência.
 */
public record MedicoVO(
        Long id,
        String nome,
        String username,
        String email,
        String telefone,
        String crm,
        String especialidade) {
}