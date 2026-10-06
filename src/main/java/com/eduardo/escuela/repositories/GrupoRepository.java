package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Grupo;

@Repository 
public interface GrupoRepository extends JpaRepository<Grupo, Long>{
    boolean existsByMaestroId(Long idMaestro);

    boolean existsByAulaId(Long idAula);

    boolean existsByCursoId(Long idCurso);
}
