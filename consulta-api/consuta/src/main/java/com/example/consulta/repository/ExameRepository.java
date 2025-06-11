package com.example.consulta.repository;

import org.springframework.stereotype.Repository;

import com.example.consulta.model.Exame;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface ExameRepository extends JpaRepository<Exame, Long> {
}
