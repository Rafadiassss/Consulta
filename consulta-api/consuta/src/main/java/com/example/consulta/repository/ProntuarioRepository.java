package com.example.consulta.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Prontuario;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    @EntityGraph(attributePaths = {"paciente", "medico", "pagamento", "prontuario"})
    Optional<Prontuario> findById(Long id);

}
