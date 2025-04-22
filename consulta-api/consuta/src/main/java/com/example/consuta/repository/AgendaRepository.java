package com.example.consuta.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.consuta.model.Agenda;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
