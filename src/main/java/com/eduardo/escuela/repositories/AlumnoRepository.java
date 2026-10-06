package com.eduardo.escuela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eduardo.escuela.entities.Alumno;

@Repository 
public interface AlumnoRepository extends JpaRepository<Alumno, Long>{
    
    @Query(nativeQuery = true, value = """
        SELECT GENERAR_MATRICULA(:nombre, :paterno, :materno) FROM DUAL
    """)
    String generarMatricula(
        @Param("nombre") String nombre, 
        @Param("paterno") String apellidoPaterno,
        @Param("materno") String apellidoMaterno);

    @Query(nativeQuery = true, value = """
        SELECT GENERAR_EMAIL(:nombre, :paterno, :materno) FROM DUAL
    """)
    String generarEmail(
        @Param("nombre") String nombre, 
        @Param("paterno") String apellidoPaterno,
        @Param("materno") String apellidoMaterno);
}
