package com.example.consuta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.consuta.model.Prontuario;

@Repository
public interface ProtuarioRepository extends JpaRepository<Prontuario, Long> {
   
} 