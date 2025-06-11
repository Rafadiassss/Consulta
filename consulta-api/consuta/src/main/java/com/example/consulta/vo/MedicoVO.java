package com.example.consulta.vo;

import java.time.LocalDate;

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
                LocalDate dataNascimento,
                String crm,
                String nomeEspecialidade) {
}