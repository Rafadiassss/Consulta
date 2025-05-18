package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.consulta.model.Consulta;
import com.example.consulta.model.Medico;
import com.example.consulta.model.Paciente;
import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.MedicoRepository;
import com.example.consulta.repository.PacienteRepository;
import com.example.consulta.repository.PagamentoRepository;

import java.util.List;
import java.util.Optional;

import com.example.consulta.model.Prontuario;
import com.example.consulta.repository.ProntuarioRepository;

@Service
public class ConsultaService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

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
        if (consulta.getPagamento() != null && consulta.getPagamento().getId() != null) {
            // Busca pagamento gerenciado do banco
            Pagamento pagamentoGerenciado = pagamentoRepository.findById(consulta.getPagamento().getId())
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

            consulta.setPagamento(pagamentoGerenciado);
        }
         if (consulta.getProntuario() != null && consulta.getProntuario().getId() != null) {
        // Busca Prontuario gerenciado do banco
        Prontuario prontuarioGerenciado = prontuarioRepository.findById(consulta.getProntuario().getId())
            .orElseThrow(() -> new RuntimeException("Prontuário não encontrado"));

        consulta.setProntuario(prontuarioGerenciado);
    }

      if (consulta.getMedico() != null && consulta.getMedico().getId() != null) {
        Medico medicoGerenciado = medicoRepository.findById(consulta.getMedico().getId())
            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        consulta.setMedico(medicoGerenciado);
    }

    if (consulta.getPaciente() != null && consulta.getPaciente().getId() != null) {
        Paciente pacienteGerenciado = pacienteRepository.findById(consulta.getPaciente().getId())
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        consulta.setPaciente(pacienteGerenciado);
    }

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
