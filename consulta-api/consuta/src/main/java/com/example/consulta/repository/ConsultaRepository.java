package com.example.consulta.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Consulta;
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    @EntityGraph(attributePaths = {"paciente", "medico", "pagamento", "prontuario"})
    Optional<Consulta> findById(Long id);
}


