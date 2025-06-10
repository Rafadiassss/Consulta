package com.example.consulta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para receber os dados de criação/atualização de um Médico.
 */
public record MedicoRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio") String nome,

        @NotBlank(message = "O nome de usuário não pode ser vazio") String username,

        @NotBlank(message = "A senha não pode ser vazia") String senha,

        @Email(message = "O e-mail deve ser válido") String email,

        String telefone,

        @NotBlank(message = "O CRM não pode ser vazio") String crm,

        @NotBlank(message = "A especialidade não pode ser vazia") String especialidade) {
}