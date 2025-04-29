package com.example.consulta.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CadastroInicialDTO {
    private String nomePaciente;
    private String cpfPaciente;
    private String emailPaciente;
    private String telefonePaciente;

    private String nomeSecretaria;
    private String cpfSecretaria;
    private String telefoneSecretaria;
    private String emailSecretaria;

    private String nomeMedico;
    private String usernameMedico;
    private String senhaMedico;
    private String tipoMedico;
    private String cpfMedico;
    private String crmMedico;
    private String especialidadeMedico;

    private String horarioConsulta;
    private LocalDate dataConsulta;
}
