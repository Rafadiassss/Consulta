package com.example.consuta.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.consuta.model.Secretaria;
@Repository
public interface SecretariaRepository extends JpaRepository<Secretaria, Long> {

    
} 
