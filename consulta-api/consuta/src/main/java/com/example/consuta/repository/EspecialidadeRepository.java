package com.example.consuta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.consuta.model.Especialidade;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    // Aqui você pode adicionar métodos personalizados, se necessário
    
}
