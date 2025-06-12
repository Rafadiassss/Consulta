package com.example.consulta.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

/**
 * VO (Value Object) - Representa um Usuário na camada de negócio.
 * Inclui campos de todas as subclasses (Medico, Paciente) que serão nulos
 * se o usuário não for daquele tipo.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Não inclui campos nulos no JSON
public record UsuarioVO(
                Long id,
                String nome,
                String username,
                String email,
                String telefone,
                String tipo,
                LocalDate dataNascimento,
                // Campos específicos
                String crm,
                String especialidade,
                String cpf,
                String cartaoSus) {
}