package com.example.consulta.service;

import com.example.consulta.model.Especialidade;
import com.example.consulta.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    public List<Especialidade> listarTodas() {
        return especialidadeRepository.findAll();
    }

    public Optional<Especialidade> buscarPorId(Long id) {
        return especialidadeRepository.findById(id);
    }

    public Especialidade salvar(Especialidade especialidade) {
        return especialidadeRepository.save(especialidade);
    }

    public boolean deletar(Long id) {
        if (especialidadeRepository.existsById(id)) {
            especialidadeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Especialidade> atualizar(Long id, Especialidade dadosNovos) {
        return especialidadeRepository.findById(id)
                .map(especialidadeExistente -> {
                    especialidadeExistente.setNome(dadosNovos.getNome());
                    // Adicione outros campos para atualizar se houver
                    return especialidadeRepository.save(especialidadeExistente);
                });
    }
}
