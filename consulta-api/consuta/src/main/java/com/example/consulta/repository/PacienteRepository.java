package com.example.consulta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

}

    

