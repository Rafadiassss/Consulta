package com.example.consulta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Este DTO define os dados que a API recebe para criar/atualizar.
public record SecretariaRequestDTO(
        @NotBlank String nome,
        @NotBlank String cpf,
        String telefone,
        @Email @NotBlank String email,
        @NotBlank String usuario,
        @NotBlank String senha) {
}