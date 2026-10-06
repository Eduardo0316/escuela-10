package com.eduardo.escuela.dto.alumnos;

import java.math.BigDecimal;
import java.util.List;

import com.eduardo.escuela.entities.Calificacion;

public record AlumnoResponse(
    Long id,

    String nombre,

    String email,

    String matricula,
    
    String fechaIngreso,

    List<Calificacion> calificaciones,

    BigDecimal promedio
) {

}
