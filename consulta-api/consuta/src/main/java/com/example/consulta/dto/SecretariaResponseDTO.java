package com.example.consulta.dto;

// Este DTO define os dados que a API envia como resposta.
// Note que ele não inclui dados sensíveis como a senha.
public record SecretariaResponseDTO(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        String usuario) {
}