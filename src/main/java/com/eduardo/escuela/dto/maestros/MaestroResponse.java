package com.eduardo.escuela.dto.maestros;

import java.util.List;

import com.eduardo.escuela.dto.datos.DatosCurso;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un maestro")
public record MaestroResponse(
    @Schema(description = "ID del maestro", example = "1")
    Long id,

    @Schema(description = "Nombre completo del maestro", example = "Máximo")
    String nombreCompleto,

    @Schema(description = "Correo electronico del maestro", example = "test@test.com")
    String email,
    
    @Schema(description = "Número telefónico paterno del maestro", example = "2223334455")
    String telefono,
    
    @Schema(description = "Datos de los cursos del maestro")
    List<DatosCurso> cursos
) {

}
