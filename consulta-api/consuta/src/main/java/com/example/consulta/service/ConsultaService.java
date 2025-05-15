package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.ProntuarioRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

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
        Medico medico = consulta.getMedico();

        if (medico != null) {
            if (medico.getId() != null) {
                Medico medicoExistente = medicoRepository.findById(medico.getId())
                        .orElseThrow(() -> new RuntimeException("Médico não encontrado com ID: " + medico.getId()));
                consulta.setMedico(medicoExistente);
            } else {
                Medico medicoSalvo = medicoRepository.save(medico);
                consulta.setMedico(medicoSalvo);
            }
        } else {
            throw new RuntimeException("Médico inválido: é necessário fornecer um médico para a consulta");
        }

        // Persistir o prontuário se presente
        final Prontuario originalProntuario = consulta.getProntuario();

        if (originalProntuario != null) {
            Prontuario prontuarioParaSalvar;

            if (originalProntuario.getId() == null) {
                prontuarioParaSalvar = prontuarioRepository.save(originalProntuario);
            } else {
                Prontuario prontuarioExistente = prontuarioRepository.findById(originalProntuario.getId())
                        .orElseThrow(() -> new RuntimeException("Prontuário não encontrado com ID: " + originalProntuario.getId()));

                prontuarioExistente.setNumero(originalProntuario.getNumero());
                prontuarioExistente.setDiagnostico(originalProntuario.getDiagnostico());
                prontuarioExistente.setTratamento(originalProntuario.getTratamento());
                prontuarioExistente.setObservacoes(originalProntuario.getObservacoes());

                prontuarioParaSalvar = prontuarioRepository.save(prontuarioExistente);
            }

            consulta.setProntuario(prontuarioParaSalvar);
        }

        return consultaRepository.save(consulta);
    }
}
