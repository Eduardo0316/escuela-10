package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Calificacion;

@Repository 
public interface CalificacionRepository extends JpaRepository<Calificacion, Long>{
    boolean existsByInscripcionId(Long idInscripcion);
}
