package com.example.consulta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

/**
 * DTO para receber os dados de criação/atualização de um Paciente.
 */
public record PacienteRequestDTO(
                @NotBlank(message = "O nome não pode ser vazio") String nome,

                @NotBlank(message = "O nome de usuário não pode ser vazio") String username,

                @NotBlank(message = "A senha não pode ser vazia") String senha,

                @Email(message = "O e-mail deve ser válido") String email,

                String telefone,

                @Past(message = "A data de nascimento deve ser no passado") LocalDate dataNascimento,

                @NotBlank(message = "O CPF não pode ser vazio") @CPF(message = "O CPF fornecido é inválido") String cpf,
                String cartaoSus) {
}