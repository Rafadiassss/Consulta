package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.ProtuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ProtuarioRepository prontuarioRepository;

    // Listar todas as consultas
    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    // Buscar consulta por ID
    public Optional<Consulta> buscarPorId(Long id) {
        return consultaRepository.findById(id);
    }

    // Deletar consulta
    public void deletar(Long id) {
        consultaRepository.deleteById(id);
    }

    // Salvar ou atualizar consulta com seus dados
    @Transactional
    public Consulta salvar(Consulta consulta) {
        // Salvar o médico, se ele estiver presente na consulta
        Medico medico = consulta.getMedico();

        if (medico != null) {
            // Se o médico tem ID, tenta buscar no banco de dados
            if (medico.getId() != null) {
                Medico medicoExistente = medicoRepository.findById(medico.getId())
                        .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + medico.getId()));
                consulta.setMedico(medicoExistente);
            } else {
                // Se o médico não tem ID, ele é salvo antes de associá-lo à consulta
                Medico medicoSalvo = medicoRepository.save(medico);
                consulta.setMedico(medicoSalvo);
            }
        } else {
            throw new RuntimeException("Médico inválido: é necessário fornecer um médico para a consulta");
        }

        
        if (consulta.getProntuario() != null) {
            Prontuario prontuario = consulta.getProntuario();

           
            if (prontuario.getId() != null) {
                prontuario = prontuarioRepository.save(prontuario); 
            }
            consulta.setProntuario(prontuario);
        }
        return consultaRepository.save(consulta);
    }

}
