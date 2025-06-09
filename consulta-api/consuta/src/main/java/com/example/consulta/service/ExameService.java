package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Exame;
import com.example.consulta.repository.ExameRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExameService {

    @Autowired
    private ExameRepository exameRepository;

    @Cacheable(value = "exames") // Cacheia a lista completa de exames
    public List<Exame> listarTodos() {
        return exameRepository.findAll();
    }

    @Cacheable(value = "exame", key = "#id") // Cacheia um exame individual pelo ID
    public Optional<Exame> buscarPorId(Long id) {
        return exameRepository.findById(id);
    }

    @CacheEvict(value = "exames", allEntries = true) // Limpa o cache "exames" na criação/atualização
    @CachePut(value = "exame", key = "#exame.id") // Atualiza/adiciona o exame no cache "exame"
    public Exame salvar(Exame exame) {
        return exameRepository.save(exame);
    }

    @CacheEvict(value = { "exames", "exame" }, allEntries = true) // Limpa caches relevantes na exclusão
    public boolean deletar(Long id) {
        if (exameRepository.existsById(id)) {
            exameRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @CacheEvict(value = "exames", allEntries = true) // Limpa o cache de todos os exames
    @CachePut(value = "exame", key = "#id") // Atualiza o cache de um exame específico
    public Optional<Exame> atualizar(Long id, Exame dadosNovos) {
        return exameRepository.findById(id)
                .map(exameExistente -> {
                    // Copia os campos do objeto de entrada para o objeto do banco
                    exameExistente.setNome(dadosNovos.getNome());
                    exameExistente.setResultado(dadosNovos.getResultado());
                    exameExistente.setObservacoes(dadosNovos.getObservacoes());
                    exameExistente.setConsulta(dadosNovos.getConsulta()); // Atualiza a referência à consulta

                    return exameRepository.save(exameExistente);
                });
    }
}
