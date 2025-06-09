package com.example.consulta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.consulta.model.Pagamento;
import com.example.consulta.repository.PagamentoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Cacheable(value = "pagamentos") // Cacheia a lista completa de pagamentos
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @Cacheable(value = "pagamento", key = "#id") // Cacheia um pagamento individual pelo ID
    public Optional<Pagamento> buscarPorId(Long id) {
        return pagamentoRepository.findById(id);
    }

    @CacheEvict(value = "pagamentos", allEntries = true) // Limpa o cache "pagamentos" na criação/atualização
    @CachePut(value = "pagamento", key = "#pagamento.id") // Atualiza/adiciona o pagamento no cache "pagamento"
    public Pagamento salvar(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    @CacheEvict(value = { "pagamentos", "pagamento" }, allEntries = true) // Limpa caches relevantes na exclusão
    public void deletar(Long id) {
        pagamentoRepository.deleteById(id);
    }
}
