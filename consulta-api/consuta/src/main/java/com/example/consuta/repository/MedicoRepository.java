package com.example.consuta.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.consuta.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

}
