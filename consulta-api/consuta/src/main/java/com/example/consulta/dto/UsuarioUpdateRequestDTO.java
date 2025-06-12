package com.example.consulta.dto;

import jakarta.validation.constraints.Email;

/**
 * DTO para receber dados na atualização de um Usuário.
 * Contém apenas os campos que podem ser modificados por um usuário.
 */
public record UsuarioUpdateRequestDTO(
        String nome,
        String username,
        @Email(message = "O e-mail deve ser válido") String email,
        String telefone) {
}