package com.example.consulta.service;

import com.example.consulta.dto.PagamentoRequestDTO;
import com.example.consulta.model.Consulta;
import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.ConsultaRepository;
import com.example.consulta.repository.PagamentoRepository;
import com.example.consulta.vo.PagamentoVO;
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
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;

    // Guarda o resultado deste método no cache "pagamentos".
    @Cacheable("pagamentos")
    public List<PagamentoVO> listarTodos() {
        // Busca todas as entidades do banco e as converte para VOs.
        return pagamentoRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    // Guarda um pagamento individual no cache "pagamento", usando o ID como chave.
    @Cacheable(value = "pagamento", key = "#id")
    public Optional<PagamentoVO> buscarPorId(Long id) {
        // Busca a entidade pelo ID e, se encontrar, a converte para VO.
        return pagamentoRepository.findById(id).map(this::toVO);
    }

    // Agrupa operações de cache: limpa a lista e adiciona/atualiza o item salvo.
    @Caching(evict = { @CacheEvict(value = "pagamentos", allEntries = true) }, put = {
            @CachePut(value = "pagamento", key = "#result.id()") })
    public PagamentoVO salvar(PagamentoRequestDTO dto) {
        // Converte o DTO recebido para uma entidade.
        Pagamento pagamento = toEntity(dto);
        // Salva a nova entidade no banco.
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        // Converte a entidade salva para VO e a retorna.
        return toVO(pagamentoSalvo);
    }

    // Agrupa operações de cache: limpa a lista e atualiza o item individual.
    @Caching(evict = { @CacheEvict(value = "pagamentos", allEntries = true) }, put = {
            @CachePut(value = "pagamento", key = "#id") })
    public Optional<PagamentoVO> atualizar(Long id, PagamentoRequestDTO dto) {
        // Busca o pagamento existente pelo ID.
        return pagamentoRepository.findById(id)
                .map(pagamentoExistente -> {
                    // Busca a entidade Consulta pelo ID fornecido no DTO.
                    Consulta consulta = consultaRepository.findById(dto.consultaId())
                            .orElseThrow(() -> new RuntimeException("Consulta não encontrada para o pagamento."));

                    // Atualiza os campos do pagamento existente com os dados do DTO.
                    pagamentoExistente.setDataPagamento(dto.dataPagamento());
                    pagamentoExistente.setValorPago(dto.valorPago());
                    pagamentoExistente.setFormaPagamento(dto.formaPagamento());
                    pagamentoExistente.setStatus(dto.status());
                    pagamentoExistente.setConsulta(consulta);

                    // Salva a entidade atualizada.
                    Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamentoExistente);
                    // Converte e retorna o VO da entidade atualizada.
                    return toVO(pagamentoAtualizado);
                });
    }

    // Agrupa operações de cache: limpa a lista e o item individual.
    @Caching(evict = {
            @CacheEvict(value = "pagamentos", allEntries = true),
            @CacheEvict(value = "pagamento", key = "#id")
    })
    public boolean deletar(Long id) {
        // Verifica se o registro com o ID fornecido existe.
        if (pagamentoRepository.existsById(id)) {
            // Se existir, deleta e retorna true.
            pagamentoRepository.deleteById(id);
            return true;
        }
        // Se não existir, retorna false.
        return false;
    }

    // --- MÉTODOS DE MAPEAMENTO ---

    // Converte uma Entidade 'Pagamento' para um 'PagamentoVO'.
    private PagamentoVO toVO(Pagamento pagamento) {
        // Obtém o ID da consulta associada de forma segura, evitando
        // NullPointerException.
        Long consultaId = (pagamento.getConsulta() != null) ? pagamento.getConsulta().getId() : null;
        // Retorna o novo VO.
        return new PagamentoVO(
                pagamento.getId(),
                pagamento.getDataPagamento(),
                pagamento.getValorPago(),
                pagamento.getFormaPagamento(),
                pagamento.getStatus(),
                consultaId);
    }

    // Converte um 'PagamentoRequestDTO' para uma Entidade 'Pagamento'.
    private Pagamento toEntity(PagamentoRequestDTO dto) {
        // Busca a entidade Consulta pelo ID fornecido no DTO para criar a associação.
        Consulta consulta = consultaRepository.findById(dto.consultaId())
                .orElseThrow(() -> new RuntimeException("Consulta com ID " + dto.consultaId() + " não encontrada."));

        // Cria uma nova instância da entidade.
        Pagamento pagamento = new Pagamento();
        // Define os campos da entidade com os valores do DTO.
        pagamento.setDataPagamento(dto.dataPagamento());
        pagamento.setValorPago(dto.valorPago());
        pagamento.setFormaPagamento(dto.formaPagamento());
        pagamento.setStatus(dto.status());
        pagamento.setConsulta(consulta);
        // Retorna a entidade pronta para ser salva.
        return pagamento;
    }
}