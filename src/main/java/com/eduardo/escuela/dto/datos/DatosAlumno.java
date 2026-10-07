package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un alumno")
public record DatosAlumno(
    @Schema(description = "Nombre del alumno", example = "Juanito Pérez López")
    String nombre,
    
    @Schema(description = "Matricula del alumno", example = "1A2B3C4D5E")
    String matricula,
    
    @Schema(description = "Email del alumno", example = "test@test.com")
    String email,
    
    @Schema(description = "Fecha de ingreso del alumno", example = "12/09/2026")
    String fechaIngreso
) {

}
