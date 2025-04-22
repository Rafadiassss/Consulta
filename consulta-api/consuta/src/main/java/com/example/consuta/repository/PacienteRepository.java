package com.example.consuta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.consuta.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

}

    

