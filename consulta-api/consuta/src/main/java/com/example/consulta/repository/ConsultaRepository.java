package com.example.consulta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

} 