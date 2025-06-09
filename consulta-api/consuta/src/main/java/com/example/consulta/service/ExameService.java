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

    public boolean deletar(Long id) {
        if (exameRepository.existsById(id)) {
            exameRepository.deleteById(id);
            return true;
        }
        return false;
    }

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
