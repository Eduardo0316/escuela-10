package com.eduardo.escuela.dto.inscripciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para registrar una inscripcion")
public record InscripcionRequest(
    @Schema(description = "ID de un alumno", example = "6")
    @NotNull(message = "El ID del alumno es requerido")
    @Positive(message = "El ID del alumno debe ser positivo")
    Long idAlumno,

    @Schema(description = "ID de un grupo", example = "7")
    @NotNull(message = "El ID del grupo es requerido")
    @Positive(message = "El ID del grupo debe ser positivo")
    Long idGrupo
) {

}
