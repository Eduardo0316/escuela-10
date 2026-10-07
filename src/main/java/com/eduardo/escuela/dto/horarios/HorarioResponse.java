package com.eduardo.escuela.dto.horarios;

import com.eduardo.escuela.dto.datos.DatosGrupo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos del horario")
public record HorarioResponse(
    @Schema(description = "Id del horario", example = "1")
    Long id,

    @Schema(description = "Datos del grupo del horario")
    DatosGrupo grupo,

    @Schema(description = "Horario completo", example = "Lunes 08:00 - 10:00")
    String horario
) {}