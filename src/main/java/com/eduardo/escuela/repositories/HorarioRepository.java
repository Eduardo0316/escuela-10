package com.eduardo.escuela.repositories;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;

@Repository 
public interface HorarioRepository extends JpaRepository<Horario, Long>{
    boolean existsByGrupoId(Long idGrupo);

    @Query("""
        SELECT COUNT(h) > 0 FROM Horario h
        WHERE h.grupo.id = :idGrupo AND h.dia = :dia
        AND h.horaInicio < :fin AND h.horaFin > :inicio
    """)
    boolean existeTraslape(Long idGrupo, DiaSemana dia, LocalTime inicio, LocalTime fin);
}
