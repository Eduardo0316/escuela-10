package com.eduardo.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un curso")
public record CursoResponse(
    @Schema(description = "ID del curso", example = "2")
    Long id,

    @Schema(description = "Nombre del curso")
    String nombre,

    @Schema(description = "Descripcion del curso", example = "Curso para obtener A2 de inglés")
    String descripcion,
    
    @Schema(description = "Créditos del curso", example = "6")
    Integer creditos
) {
}
