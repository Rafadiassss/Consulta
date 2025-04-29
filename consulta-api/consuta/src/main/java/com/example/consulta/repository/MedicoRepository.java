package com.example.consulta.repository;

import org.springframework.stereotype.Repository;

import com.example.consulta.model.Medico;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

}
