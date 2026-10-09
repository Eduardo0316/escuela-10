package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un maestro")
public record DatosMaestro(
    @Schema(description = "Nombre del maestro", example = "Mauricio")
    String nombre,

    @Schema(description = "Email del maestro", example = "test@test.com")
    String email,

    @Schema(description = "Teléfono del maestro", example = "2223334455")
    String telefono
) {

}
