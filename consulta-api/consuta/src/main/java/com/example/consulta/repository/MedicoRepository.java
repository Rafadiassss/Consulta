package com.example.consulta.repository;

import com.example.consulta.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    // Adiciona uma verificação para saber se tem algum medico usando alguma
    // especialidade
    boolean existsByEspecialidadeId(Long especialidadeId);
}