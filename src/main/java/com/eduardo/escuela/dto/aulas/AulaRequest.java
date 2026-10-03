package com.eduardo.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para registrar un aula")
public record AulaRequest(
    @Schema(description = "Nombre del aula", example = "Aula 67")
    @NotBlank(message = "El nombre del aula es requerido")
    @Size(message = "El nombre debe tener entre 1 y 100 caracteres", min = 1, max = 100)
    String nombre, 

    @Schema(description = "Capacidad del aula", example = "30")
    @NotNull(message = "La capacidad del aula es requerida")
    @Positive(message = "La capacidad debe ser positiva")
    Integer capacidad
) {

}
