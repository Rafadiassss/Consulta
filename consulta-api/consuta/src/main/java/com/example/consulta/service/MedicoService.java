package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Medico;
import com.example.consulta.repository.MedicoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Cacheable(value = "medicos") // Cacheia a lista completa de médicos
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    @Cacheable(value = "medico", key = "#id") // Cacheia um médico individual pelo ID
    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    @CacheEvict(value = "medicos", allEntries = true) // Limpa o cache de "medicos" quando um novo médico é salvo
    @CachePut(value = "medico", key = "#medico.id") // Atualiza o cache de "medico" para o ID específico
    public Medico salvar(Medico medico) {
        return medicoRepository.save(medico);
    }

    @CacheEvict(value = { "medicos", "medico" }, allEntries = true) // Limpa caches relevantes na exclusão
    public void deletar(Long id) {
        medicoRepository.deleteById(id);
    }
}
