package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Procedimento;
import com.example.consulta.repository.ProcedimentoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedimentoService {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    @Cacheable(value = "procedimentos") // Cacheia a lista de procedimentos
    public List<Procedimento> listarTodos() {
        return procedimentoRepository.findAll();
    }

    @Cacheable(value = "procedimento", key = "#id") // Cacheia um procedimento pelo ID
    public Optional<Procedimento> buscarPorId(Long id) {
        return procedimentoRepository.findById(id);
    }

    @CacheEvict(value = "procedimentos", allEntries = true) // Limpa o cache na criação
    public Procedimento salvar(Procedimento procedimento) {
        return procedimentoRepository.save(procedimento);
    }

    @CacheEvict(value = { "procedimentos", "procedimento" }, allEntries = true) // Limpa caches relevantes na exclusão
    public void deletar(Long id) {
        procedimentoRepository.deleteById(id);
    }
}
