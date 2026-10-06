package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Maestro;

@Repository 
public interface MaestroRepository extends JpaRepository<Maestro, Long>{
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdNot(String telefono, Long id);
}
