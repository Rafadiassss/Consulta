package com.example.consulta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Prontuario;

@Repository
public interface ProtuarioRepository extends JpaRepository<Prontuario, Long> {
    
} 