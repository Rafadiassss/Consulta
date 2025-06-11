package com.example.consulta.repository;
import org.springframework.stereotype.Repository;

import com.example.consulta.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
