package com.example.consulta.service;

import com.example.consulta.dto.AgendaRequestDTO;
import com.example.consulta.vo.AgendaVO;
import com.example.consulta.model.Agenda;
import com.example.consulta.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    // Guarda o resultado deste método no cache "agendas".
    @Cacheable("agendas")
    public List<AgendaVO> listarTodas() {
        // Busca todas as entidades 'Agenda' do banco de dados.
        return agendaRepository.findAll()
                // Converte cada entidade 'Agenda' da lista para o seu 'AgendaVO'
                // correspondente.
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // Guarda o resultado no cache "agenda" usando o ID do parâmetro como chave
    // única.
    @Cacheable(value = "agenda", key = "#id")
    public Optional<AgendaVO> buscarPorId(Long id) {
        // Busca a entidade pelo ID e, se encontrar, a converte para VO.
        return agendaRepository.findById(id)
                .map(this::toVO);
    }

    // Agrupa múltiplas operações de cache para serem executadas após o método.
    @Caching(evict = {
            // Limpa/invalida todas as entradas do cache "agendas", pois a lista mudou.
            @CacheEvict(value = "agendas", allEntries = true)
    }, put = {
            // Adiciona ou atualiza o resultado deste método no cache "agenda".
            // Usa o ID do objeto retornado (#result.id()) como chave.
            @CachePut(value = "agenda", key = "#result.id()")
    })
    public AgendaVO salvar(AgendaRequestDTO dto) {
        // Converte o DTO recebido para uma entidade.
        Agenda agenda = toEntity(dto);
        // Salva a entidade no banco.
        Agenda agendaSalva = agendaRepository.save(agenda);
        // Converte a entidade salva para um VO e a retorna.
        return toVO(agendaSalva);
    }

    // Agrupa as operações de cache para a atualização.
    @Caching(evict = { @CacheEvict(value = "agendas", allEntries = true) }, put = {
            @CachePut(value = "agenda", key = "#id") })
    public Optional<AgendaVO> atualizar(Long id, AgendaRequestDTO dto) {
        // Busca a agenda existente pelo ID.
        return agendaRepository.findById(id)
                .map(agendaExistente -> {
                    // Se a agenda for encontrada, atualiza seus campos com os dados do DTO.
                    agendaExistente.setDataAgendada(dto.dataAgendada());
                    agendaExistente.setHorarios(dto.horarios());
                    // Salva a entidade atualizada no banco.
                    Agenda agendaAtualizada = agendaRepository.save(agendaExistente);
                    // Converte e retorna o VO da entidade atualizada.
                    return toVO(agendaAtualizada);
                });
    }

    // Agrupa as operações de limpeza de cache para a exclusão.
    @Caching(evict = {
            // Remove todas as entradas do cache da lista de agendas.
            @CacheEvict(value = "agendas", allEntries = true),
            // Remove do cache "agenda" apenas o item com o ID que está sendo deletado.
            @CacheEvict(value = "agenda", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o registro com o ID fornecido existe.
        if (agendaRepository.existsById(id)) {
            // Se existir, deleta o registro e retorna true.
            agendaRepository.deleteById(id);
            return true;
        }
        // Se não existir, retorna false.
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Agenda' para um 'AgendaVO' (Value Object).
    private AgendaVO toVO(Agenda agenda) {
        return new AgendaVO(
                agenda.getId(),
                agenda.getDataAgendada(),
                agenda.getHorarios());
    }

    // Converte um 'AgendaRequestDTO' (Data Transfer Object) para uma Entidade
    // 'Agenda'.
    private Agenda toEntity(AgendaRequestDTO dto) {
        Agenda agenda = new Agenda();
        agenda.setDataAgendada(dto.dataAgendada());
        agenda.setHorarios(dto.horarios());
        return agenda;
    }
}
