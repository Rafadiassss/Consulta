package com.example.consulta.vo;

import java.time.LocalDate;

/**
 * VO (Value Object) - Representa um Paciente na camada de negócio.
 */
public record PacienteVO(
                Long id,
                String nome,
                String username,
                String email,
                String telefone,
                LocalDate dataNascimento,
                String cpf,
                String cartaoSus) {
}