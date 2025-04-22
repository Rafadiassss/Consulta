package com.example.consuta.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.consuta.model.Consulta;
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
}


