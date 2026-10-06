package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Curso;

@Repository 
public interface CursoRepository extends JpaRepository<Curso, Long>{
    boolean existsByNombre(String nombre);
}
