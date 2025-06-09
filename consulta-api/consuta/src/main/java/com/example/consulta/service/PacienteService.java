package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Paciente;
import com.example.consulta.repository.PacienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Cacheable(value = "pacientes") // Cacheia a lista completa de pacientes
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Cacheable(value = "paciente", key = "#id") // Cacheia um paciente individual pelo ID
    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    @CacheEvict(value = "pacientes", allEntries = true) // Limpa o cache "pacientes" na criação
    @CachePut(value = "paciente", key = "#paciente.id") // Adiciona/atualiza o paciente no cache "paciente"
    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @CacheEvict(value = "pacientes", allEntries = true) // Limpa o cache de todos os pacientes
    @CachePut(value = "paciente", key = "#id") // Atualiza o cache de um paciente específico
    public Paciente atualizar(Long id, Paciente paciente) {
        Optional<Paciente> pacienteExistente = pacienteRepository.findById(id);

        if (pacienteExistente.isPresent()) {
            Paciente pacienteAtualizado = pacienteExistente.get();
            pacienteAtualizado.setCpf(paciente.getCpf());
            pacienteAtualizado.setConsultas(paciente.getConsultas());
            return pacienteRepository.save(pacienteAtualizado);
        } else {
            throw new RuntimeException("Paciente não encontrado");
        }
    }

    @CacheEvict(value = { "pacientes", "paciente" }, allEntries = true) // Limpa caches na exclusão
    public void deletar(Long id) {
        pacienteRepository.deleteById(id);
    }
}
