package com.example.consulta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para receber dados na criação/atualização de uma Especialidade.
 * Não inclui o 'id', pois ele é controlado pelo sistema.
 * Adiciona validações para garantir a qualidade dos dados de entrada.
 */
public record EspecialidadeRequestDTO(
    
    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    String nome,

    String descricao
) {}