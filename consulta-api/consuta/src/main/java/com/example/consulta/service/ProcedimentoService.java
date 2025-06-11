package com.example.consulta.service;

import com.example.consulta.dto.ProcedimentoRequestDTO;
import com.example.consulta.model.Procedimento;
import com.example.consulta.repository.ProcedimentoRepository;
import com.example.consulta.vo.ProcedimentoVO;
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
public class ProcedimentoService {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    // Guarda o resultado deste método no cache "procedimentos".
    @Cacheable("procedimentos")
    public List<ProcedimentoVO> listarTodos() {
        // Busca todas as entidades do banco.
        return procedimentoRepository.findAll().stream()
                // Converte cada entidade para o seu VO correspondente.
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    // Guarda o resultado no cache "procedimento", usando o ID como chave única.
    @Cacheable(value = "procedimento", key = "#id")
    public Optional<ProcedimentoVO> buscarPorId(Long id) {
        // Busca a entidade pelo ID e a converte para VO, se encontrada.
        return procedimentoRepository.findById(id).map(this::toVO);
    }

    // Agrupa operações de cache: invalida a lista e adiciona/atualiza o item salvo.
    @Caching(evict = { @CacheEvict(value = "procedimentos", allEntries = true) }, put = {
            @CachePut(value = "procedimento", key = "#result.id()") })
    public ProcedimentoVO salvar(ProcedimentoRequestDTO dto) {
        // Converte o DTO recebido para uma entidade.
        Procedimento procedimento = toEntity(dto);
        // Salva a nova entidade no banco.
        Procedimento procedimentoSalvo = procedimentoRepository.save(procedimento);
        // Converte a entidade salva para um VO e a retorna.
        return toVO(procedimentoSalvo);
    }

    // Agrupa as operações de cache para a atualização.
    @Caching(evict = { @CacheEvict(value = "procedimentos", allEntries = true) }, put = {
            @CachePut(value = "procedimento", key = "#id") })
    public Optional<ProcedimentoVO> atualizar(Long id, ProcedimentoRequestDTO dto) {
        // Busca o procedimento existente pelo ID.
        return procedimentoRepository.findById(id)
                .map(procedimentoExistente -> {
                    // Se encontrado, atualiza seus campos com os dados do DTO.
                    procedimentoExistente.setNome(dto.nome());
                    procedimentoExistente.setDescricao(dto.descricao());
                    procedimentoExistente.setValor(dto.valor());
                    // Salva a entidade atualizada.
                    Procedimento procedimentoAtualizado = procedimentoRepository.save(procedimentoExistente);
                    // Converte e retorna o VO da entidade atualizada.
                    return toVO(procedimentoAtualizado);
                });
    }

    // Agrupa as operações de limpeza de cache para a exclusão.
    @Caching(evict = {
            // Remove todas as entradas do cache da lista de procedimentos.
            @CacheEvict(value = "procedimentos", allEntries = true),
            // Remove do cache "procedimento" apenas o item com o ID que está sendo
            // deletado.
            @CacheEvict(value = "procedimento", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o registro com o ID fornecido existe.
        if (procedimentoRepository.existsById(id)) {
            // Se existir, deleta e retorna true.
            procedimentoRepository.deleteById(id);
            return true;
        }
        // Se não existir, retorna false.
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Procedimento' para um 'ProcedimentoVO'.
    private ProcedimentoVO toVO(Procedimento procedimento) {
        return new ProcedimentoVO(
                procedimento.getId(),
                procedimento.getNome(),
                procedimento.getDescricao(),
                procedimento.getValor());
    }

    // Converte um 'ProcedimentoRequestDTO' para uma Entidade 'Procedimento'.
    private Procedimento toEntity(ProcedimentoRequestDTO dto) {
        Procedimento procedimento = new Procedimento();
        procedimento.setNome(dto.nome());
        procedimento.setDescricao(dto.descricao());
        procedimento.setValor(dto.valor());
        return procedimento;
    }
}
