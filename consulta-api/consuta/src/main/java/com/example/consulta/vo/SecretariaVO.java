package com.example.consulta.vo;

// Este objeto representa uma Secretaria na camada de negócio.
public record SecretariaVO(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        String usuario) {
}