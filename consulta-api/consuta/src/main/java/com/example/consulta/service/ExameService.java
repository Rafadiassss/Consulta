package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Exame;
import com.example.consulta.repository.ExameRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExameService {

    @Autowired
    private ExameRepository exameRepository;

    public List<Exame> listarTodos() {
        return exameRepository.findAll();
    }

    public Optional<Exame> buscarPorId(Long id) {
        return exameRepository.findById(id);
    }

    public Exame salvar(Exame exame) {
        return exameRepository.save(exame);
    }

    public void deletar(Long id) {
        exameRepository.deleteById(id);
    }
}
