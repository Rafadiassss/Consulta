package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consulta.model.Consulta;
import com.example.consulta.repository.ConsultaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    @Transactional
    public void deletar(Long id) {
        consultaRepository.deleteById(id);
    }

    @Transactional
    public Consulta salvar(Consulta consulta) {
        return consultaRepository.save(consulta);
    }

    @Transactional
    public Consulta atualizar(Long id, Consulta consultaAtualizada) {
        Optional<Consulta> consultaExistente = consultaRepository.findById(id);
        if (consultaExistente.isPresent()) {
            Consulta consulta = consultaExistente.get();
            consulta.setData(consultaAtualizada.getData());
            consulta.setPaciente(consultaAtualizada.getPaciente());
            consulta.setMedico(consultaAtualizada.getMedico());
            return consultaRepository.save(consulta);
        }
        return null;
    }
}
