package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de inscripción")
public record DatosInscripcion(
    @Schema(description = "Datos del alumno")
    DatosAlumno alumno,
    
    @Schema(description = "Datos del grupo")
    DatosGrupo grupo,
    
    @Schema(description = "Fecha de inscripción", example = "07/10/2026")
    String fechaInscripcion
) {

}
