package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Paciente;
import com.example.consulta.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente atualizar(Long id, Paciente paciente) {
    Optional<Paciente> pacienteExistente = pacienteRepository.findById(id);
    
    if (pacienteExistente.isPresent()) {
        Paciente pacienteAtualizado = pacienteExistente.get();
        pacienteAtualizado.setCpf(paciente.getCpf());
        return pacienteRepository.save(pacienteAtualizado);
    } else {
        throw new RuntimeException("Paciente não encontrado");
    }
}

    public void deletar(Long id) {
        pacienteRepository.deleteById(id);
    }
}
