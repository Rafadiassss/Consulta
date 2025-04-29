package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Secretaria;
import com.example.consulta.repository.SecretariaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SecretariaService {

    @Autowired
    private SecretariaRepository secretariaRepository;

    public List<Secretaria> listarTodas() {
        return secretariaRepository.findAll();
    }

    public Optional<Secretaria> buscarPorId(Long id) {
        return secretariaRepository.findById(id);
    }

    public Secretaria salvar(Secretaria secretaria) {
        return secretariaRepository.save(secretaria);
    }

    public void deletar(Long id) {
        secretariaRepository.deleteById(id);
    }
}
