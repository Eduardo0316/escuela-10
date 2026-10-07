package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Inscripcion;

@Repository 
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long>{
    boolean existsByAlumnoId(Long idAlumno);

    boolean existsByGrupoId(Long idGrupo);

    boolean existsByAlumnoIdAndGrupoId(Long idAlumno, Long idGrupo);
}  
