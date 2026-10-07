package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de horario")
public record DatosHorario(
    @Schema(description = "Horario completo", example = "Lunes 08:00 - 10:00")
    String horariCompleto
) {

}
