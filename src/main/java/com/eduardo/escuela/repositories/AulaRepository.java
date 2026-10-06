package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Aula;

@Repository 
public interface AulaRepository extends JpaRepository<Aula, Long> {
    boolean existsByNombre(String nombre);
}
