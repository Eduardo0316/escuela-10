package com.eduardo.escuela.dto.grupo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para registrar un grupo")
public record GrupoRequest(
    @Schema(description = "Id del curso", example = "1")
    @NotNull(message = "El Id del curso es requerido")
    @Positive(message = "El Id del curso debe ser positivo")
    Long idCurso,

    @Schema(description = "Id del maestro", example = "1")
    @NotNull(message = "El Id del maestro es requerido")
    @Positive(message = "El Id del maestro debe ser positivo")
    Long idMaestro,

    @Schema(description = "Id del aula", example = "1")
    @NotNull(message = "El Id del aula es requerido")
    @Positive(message = "El Id del aula debe ser positivo")
    Long idAula,

    @Schema(description = "Periodo del grupo", example = "2026-01")
    @NotBlank(message = "El periodo es requerido")
    @Pattern(
        regexp = "^\\d{4}-(0[1-9]|1[0-2])$", 
        message = "El período debe tener el formato AAAA-PP (ej. 2026-01)"
    )
    String periodo
) {

}
