package com.eduardo.escuela.dto.alumnos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un alumno")
public record AlumnoRequest(
    @Schema(description = "Nombre del alumno", example = "Eduardo")
    @NotBlank(message = "El nombre es requerido")
    @Size(message = "El nombre debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    String nombre,

    @Schema(description = "Apellido paterno del alumno", example = "Garcia")
    @NotBlank(message = "El apellido paterno es requerido")
    @Size(message = "El apellido paterno debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    String apellidoPaterno,

    @Schema(description = "Apellido paterno del alumno", example = "Mendoza")
    @NotBlank(message = "El apellido materno es requerido")
    @Size(message = "El apellido materno debe tener entre 1 y 50 caracteres", min = 1, max = 50)
    String apellidoMaterno
) {

}
