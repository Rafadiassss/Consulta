package com.example.consulta.controller;

import com.example.consulta.dto.CadastroInicialDTO;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.repository.SecretariaRepository;
import com.example.consulta.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cadastro-inicial")
@RequiredArgsConstructor
public class CadastroInicialController {

    private final PacienteRepository pacienteRepository;
    private final SecretariaRepository secretariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository;

    @PostMapping
    @Operation(summary = "Cadastra paciente, secretária, médico e consulta de uma vez")
    public ResponseEntity<String> cadastrarTudo(@RequestBody CadastroInicialDTO dto) {
        try {
            // Paciente
            Paciente paciente = new Paciente();
            paciente.setNome(dto.getNomePaciente());
            paciente.setCpf(dto.getCpfPaciente());
            paciente.setEmail(dto.getEmailPaciente());
            paciente.setTelefone(dto.getTelefonePaciente());
            pacienteRepository.save(paciente);

            // Secretária
            Secretaria secretaria = new Secretaria();
            secretaria.setNome(dto.getNomeSecretaria());
            secretaria.setCpf(dto.getCpfSecretaria());
            secretaria.setTelefone(dto.getTelefoneSecretaria());
            secretaria.setEmail(dto.getEmailSecretaria());
            secretariaRepository.save(secretaria);

           Medico medico = new Medico();
            medico.setNome(dto.getNomeMedico());
            medico.setUsername(dto.getUsernameMedico());
            medico.setSenha(dto.getSenhaMedico());
            medico.setTipo(dto.getTipoMedico());
            medico.setCpf(dto.getCpfMedico());
            medico.setCrm(dto.getCrmMedico());
            medico.setEspecialidade(dto.getEspecialidadeMedico());
            usuarioRepository.save(medico); // Supondo que o repositório seja genérico e aceite Medico


            // Consulta
            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setMedico(medico);
            consultaRepository.save(consulta);

            return ResponseEntity.ok("Cadastro completo realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao cadastrar: " + e.getMessage());
        }
    }
}
