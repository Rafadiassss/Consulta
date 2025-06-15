package com.example.consulta.service;

import com.example.consulta.dto.ExameRequestDTO;
import com.example.consulta.model.Prontuario;
import com.example.consulta.model.Exame;
import com.example.consulta.repository.ProntuarioRepository;
import com.example.consulta.repository.ExameRepository;
import com.example.consulta.vo.ExameVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExameService {

    @Autowired
    private ExameRepository exameRepository;

    @Autowired
    private ProntuarioRepository prontuarioRepository;

    // Guarda a lista completa de exames no cache "exames".
    @Cacheable("exames")
    public List<ExameVO> listarTodos() {
        return exameRepository.findAll()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // Guarda um exame individual no cache "exame", usando o ID como chave.
    @Cacheable(value = "exame", key = "#id")
    public Optional<ExameVO> buscarPorId(Long id) {
        return exameRepository.findById(id).map(this::toVO);
    }

    // Agrupa operações de cache: limpa a lista e adiciona/atualiza o item salvo.
    @Caching(evict = { @CacheEvict(value = "exames", allEntries = true) }, put = {
            @CachePut(value = "exame", key = "#result.id()") })
    public ExameVO salvar(ExameRequestDTO dto) {
        Exame exame = toEntity(dto);
        Exame exameSalvo = exameRepository.save(exame);
        return toVO(exameSalvo);
    }

    // Agrupa operações de cache: limpa a lista e atualiza o item individual.
    @Caching(evict = { @CacheEvict(value = "exames", allEntries = true) }, put = {
            @CachePut(value = "exame", key = "#id") })
    public Optional<ExameVO> atualizar(Long id, ExameRequestDTO dto) {
        return exameRepository.findById(id)
                .map(exameExistente -> {
                    Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                            .orElseThrow(() -> new RuntimeException("Prontuario não encontrada para o exame."));

                    exameExistente.setNome(dto.nome());
                    exameExistente.setResultado(dto.resultado());
                    exameExistente.setObservacoes(dto.observacoes());
                    exameExistente.setConsulta(prontuario);

                    Exame exameAtualizado = exameRepository.save(exameExistente);
                    return toVO(exameAtualizado);
                });
    }

    // Agrupa operações de cache: limpa a lista e o item individual.
    @Caching(evict = {
            @CacheEvict(value = "exames", allEntries = true),
            @CacheEvict(value = "exame", key = "#id")
    })
    public boolean deletar(Long id) {
        if (exameRepository.existsById(id)) {
            exameRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    private ExameVO toVO(Exame exame) {
        Long consultaId = (exame.getConsulta() != null) ? exame.getConsulta().getId() : null;
        return new ExameVO(
                exame.getId(),
                exame.getNome(),
                exame.getResultado(),
                exame.getObservacoes(),
                consultaId);
    }

    private Exame toEntity(ExameRequestDTO dto) {
        Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                .orElseThrow(() -> new RuntimeException("Consulta com ID " + dto.prontuarioId() + " não encontrada."));

        Exame exame = new Exame();
        exame.setNome(dto.nome());
        exame.setResultado(dto.resultado());
        exame.setObservacoes(dto.observacoes());
        exame.setConsulta(prontuario);
        return exame;
    }
}
