package com.eduardo.escuela.repositories;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Grupo;
import com.eduardo.escuela.enums.DiaSemana;

@Repository 
public interface GrupoRepository extends JpaRepository<Grupo, Long>{
    boolean existsByMaestroId(Long idMaestro);

    boolean existsByAulaId(Long idAula);

    boolean existsByCursoId(Long idCurso);

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
        Long idCurso, 
        Long idMaestro, 
        Long idAula, 
        String periodo
    );

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
        Long cursoId, Long maestroId, Long aulaId, String periodo, Long id);
}
