package com.example.consuta.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.consuta.model.Exame;
@Repository
public interface ExameRepository extends JpaRepository<Exame, Long> {
}
