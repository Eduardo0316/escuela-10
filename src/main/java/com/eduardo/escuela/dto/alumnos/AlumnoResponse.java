package com.eduardo.escuela.dto.alumnos;

import java.math.BigDecimal;
import java.util.List;

import com.eduardo.escuela.dto.datos.DatosCalificacion;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un alumno")
public record AlumnoResponse(
    @Schema(description = "ID del alumno", example = "1")
    Long id,
    
    @Schema(description = "Nombre del alumno", example = "Juanito Pérez López")
    String nombre,
    
    @Schema(description = "Email del alumno", example = "test@test.com")
    String email,
    
    @Schema(description = "Matricula del alumno", example = "1A2B3C4D5E")
    String matricula,
    
    @Schema(description = "Fecha de ingreso del alumno", example = "12/09/2026")
    String fechaIngreso,
    
    @Schema(description = "Calificaciones del alumno")
    List<DatosCalificacion> calificaciones,
    
    @Schema(description = "Promedio del alumno", example = "9.9")
    BigDecimal promedio
) {

}
