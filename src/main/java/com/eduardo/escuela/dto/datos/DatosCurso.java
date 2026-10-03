package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un curso")
public record DatosCurso(
    @Schema(description = "Nombre del curso", example = "Matemáticas II")
    String nombre,

    @Schema(description = "Descripcion del curso", example = "Curso de calculo integral")
    String descripcion,

    @Schema(description = "Créditos del curso", example = "5")
    Integer creditos
) {

}
