package com.example.consulta.service;

import com.example.consulta.dto.ExameRequestDTO;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Exame;
import com.example.consulta.repository.ConsultaRepository;
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
    private ConsultaRepository consultaRepository;

    // Guarda a lista completa de exames no cache "exames".
    @Cacheable("exames")
    public List<ExameVO> listarTodos() {
        return exameRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
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
        // Converte o DTO para uma entidade, buscando a Consulta associada.
        Exame exame = toEntity(dto);
        // Salva a nova entidade no banco de dados.
        Exame exameSalvo = exameRepository.save(exame);
        // Converte a entidade salva para VO e a retorna.
        return toVO(exameSalvo);
    }

    // Agrupa operações de cache: limpa a lista e atualiza o item individual.
    @Caching(evict = { @CacheEvict(value = "exames", allEntries = true) }, put = {
            @CachePut(value = "exame", key = "#id") })
    public Optional<ExameVO> atualizar(Long id, ExameRequestDTO dto) {
        // Busca o exame existente pelo ID.
        return exameRepository.findById(id)
                .map(exameExistente -> {
                    // Busca a entidade Consulta pelo ID fornecido no DTO.
                    Consulta consulta = consultaRepository.findById(dto.consultaId())
                            .orElseThrow(() -> new RuntimeException("Consulta não encontrada para o exame."));

                    // Atualiza os campos do exame existente com os dados do DTO.
                    exameExistente.setNome(dto.nome());
                    exameExistente.setResultado(dto.resultado());
                    exameExistente.setObservacoes(dto.observacoes());
                    exameExistente.setConsulta(consulta);

                    // Salva a entidade atualizada.
                    Exame exameAtualizado = exameRepository.save(exameExistente);
                    // Converte e retorna o VO da entidade atualizada.
                    return toVO(exameAtualizado);
                });
    }

    // Agrupa operações de cache: limpa a lista e o item individual.
    @Caching(evict = {
            @CacheEvict(value = "exames", allEntries = true),
            @CacheEvict(value = "exame", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o exame com o ID fornecido existe.
        if (exameRepository.existsById(id)) {
            // Se existir, deleta e retorna true.
            exameRepository.deleteById(id);
            return true;
        }
        // Se não existir, retorna false.
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Exame' para um 'ExameVO'.
    private ExameVO toVO(Exame exame) {
        Long consultaId = (exame.getConsulta() != null) ? exame.getConsulta().getId() : null;
        return new ExameVO(
                exame.getId(),
                exame.getNome(),
                exame.getResultado(),
                exame.getObservacoes(),
                consultaId);
    }

    // Converte um 'ExameRequestDTO' para uma Entidade 'Exame'.
    private Exame toEntity(ExameRequestDTO dto) {
        // Busca a entidade Consulta pelo ID fornecido no DTO.
        Consulta consulta = consultaRepository.findById(dto.consultaId())
                .orElseThrow(() -> new RuntimeException("Consulta com ID " + dto.consultaId() + " não encontrada."));

        Exame exame = new Exame();
        exame.setNome(dto.nome());
        exame.setResultado(dto.resultado());
        exame.setObservacoes(dto.observacoes());
        exame.setConsulta(consulta);
        return exame;
    }
}