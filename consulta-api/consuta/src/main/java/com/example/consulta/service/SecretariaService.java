package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.SecretariaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SecretariaService {

    @Autowired
    private SecretariaRepository secretariaRepository;

    @Cacheable(value = "secretarias") // Cacheia a lista completa de secretarias
    public List<Secretaria> listarTodas() {
        return secretariaRepository.findAll();
    }

    @Cacheable(value = "secretaria", key = "#id") // Cacheia uma secretaria individual pelo ID
    public Optional<Secretaria> buscarPorId(Long id) {
        return secretariaRepository.findById(id);
    }

    @CacheEvict(value = "secretarias", allEntries = true) // Limpa o cache "secretarias" na criação/atualização
    @CachePut(value = "secretaria", key = "#secretaria.id") // Atualiza/adiciona a secretaria no cache "secretaria"
    public Secretaria salvar(Secretaria secretaria) {
        return secretariaRepository.save(secretaria);
    }

    @CacheEvict(value = { "secretarias", "secretaria" }, allEntries = true) // Limpa caches relevantes na exclusão
    public void deletar(Long id) {
        secretariaRepository.deleteById(id);
    }
}
