package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un Aula")
public record DatosAula(
    @Schema(description = "Nombre del aula")
    String nombre,

    @Schema(description = "Capacidad del aula", example = "30")
    Integer capacidad
) {

}
