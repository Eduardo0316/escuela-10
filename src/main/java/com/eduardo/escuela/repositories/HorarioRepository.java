package com.eduardo.escuela.repositories;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Horario;
import com.eduardo.escuela.enums.DiaSemana;

@Repository 
public interface HorarioRepository extends JpaRepository<Horario, Long>{
    boolean existsByGrupoId(Long idGrupo);

    @Query("""
        SELECT COUNT(h) > 0 FROM Horario h
        WHERE h.grupo.id = :grupoId
        AND h.dia = :dia
        AND h.horaInicio < :horaFin
        AND :horaInicio < h.horaFin
    """)
    boolean existeTraslape(
        @Param("grupoId") Long grupoId,
        @Param("dia") DiaSemana dia,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin);

    // Para actualizar (excluye el propio)
    @Query("""
        SELECT COUNT(h) > 0 FROM Horario h
        WHERE h.grupo.id = :grupoId
        AND h.dia = :dia
        AND h.id <> :id
        AND h.horaInicio < :horaFin
        AND :horaInicio < h.horaFin
    """)
    boolean existeTraslapeExcepto(
        @Param("grupoId") Long grupoId,
        @Param("dia") DiaSemana dia,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("horaFin") LocalTime horaFin,
        @Param("id") Long id);
}
