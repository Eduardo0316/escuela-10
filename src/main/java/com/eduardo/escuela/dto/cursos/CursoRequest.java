package com.eduardo.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar un curso")
public record CursoRequest(
    @Schema(description = "Nombre del curso", example = "Inglés 2")
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    String nombre,
    
    @Schema(description = "Descripcion del curso", example = "Curso para obtener A2 de inglés")
    @Size(min = 1, max = 200, message = "La descripción debe tener entre 1 y 200 caracteres")
    String descripcion,

    @Schema(description = "Créditos del curso", example = "6")
    @NotNull(message = "Los créditos son requeridos")
    @Positive(message = "Los créditos deben ser positivos")
    Integer creditos
) {

}
