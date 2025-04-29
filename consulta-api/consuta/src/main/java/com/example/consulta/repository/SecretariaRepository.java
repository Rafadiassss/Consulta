package com.example.consulta.repository;

import org.springframework.stereotype.Repository;

import com.example.consulta.model.Secretaria;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {

    
} 
