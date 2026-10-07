package com.eduardo.escuela.dto.horarios;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para registrar un horario")
public record HorarioRequest(
    @Schema(description = "Id del grupo", example = "1")
    @NotNull(message = "El Id del grupo es requerido")
    @Positive(message = "El Id del grupo debe ser positivo")
    Long idGrupo,

    @Schema(
        description = "Día de la semana, con o sin acentos y en mayúsculas o minúsculas",
        example = "Lunes",
        allowableValues = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"}
    )
    @NotBlank(message = "El día es requerido")
    String dia,

    @Schema(description = "Hora de inicio (HH:mm)", example = "08:00", type = "string")
    @NotNull(message = "La hora de inicio es requerida")
    @JsonFormat(pattern = "HH:mm")
    LocalTime horaInicio,

    @Schema(description = "Hora de fin (HH:mm)", example = "10:00", type = "string")
    @NotNull(message = "La hora de fin es requerida")
    @JsonFormat(pattern = "HH:mm")
    LocalTime horaFin
) {}
