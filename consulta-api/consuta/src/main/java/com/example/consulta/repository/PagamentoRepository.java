package com.example.consulta.repository;

import org.springframework.stereotype.Repository;

import com.example.consulta.model.Pagamento;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
}
