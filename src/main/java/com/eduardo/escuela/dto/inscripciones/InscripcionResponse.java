package com.eduardo.escuela.dto.inscripciones;

import java.math.BigDecimal;

import com.eduardo.escuela.dto.datos.DatosAlumno;
import com.eduardo.escuela.dto.datos.DatosGrupo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de la inscripción")
public record InscripcionResponse(
    @Schema(description = "Id de la inscripción", example = "1")
    Long id,
    
    @Schema(description = "Datos del alumno inscrito")
    DatosAlumno alumno,
    
    @Schema(description = "Datos del grupo")
    DatosGrupo grupo,
    
    @Schema(description = "Calificación de la inscripción", example = "8.8")
    BigDecimal calificacion,
    
    @Schema(description = "Fecha de la inscripción", example = "07/10/2026")
    String fechaInscripcion
) {

}
