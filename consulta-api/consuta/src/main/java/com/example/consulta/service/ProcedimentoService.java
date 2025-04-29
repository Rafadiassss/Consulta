package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Procedimento;
import com.example.consulta.repository.ProcedimentoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProcedimentoService {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    public List<Procedimento> listarTodos() {
        return procedimentoRepository.findAll();
    }

    public Optional<Procedimento> buscarPorId(Long id) {
        return procedimentoRepository.findById(id);
    }

    public Procedimento salvar(Procedimento procedimento) {
        return procedimentoRepository.save(procedimento);
    }

    public void deletar(Long id) {
        procedimentoRepository.deleteById(id);
    }
}
