package com.eduardo.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Datos de un aula")
public record AulaResponse(
    @Schema(description = "ID del aula", example = "2")
    Long id,
    
    @Schema(description = "Nombre del aula", example = "Aula 505")
    String nombre,
    
    @Schema(description = "Capacidad del aula", example = "30")
    Integer capacidad
) {

}
