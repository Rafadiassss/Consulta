package com.example.consuta.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.consuta.model.Usuario;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
