package com.eduardo.escuela.dto.calificaciones;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos para registrar una calificación")
public record CalificacionRequest(

    @Schema(description = "Id de la inscripcion", example = "2")
    @NotNull(message = "El id de la inscripcion es necesario")
    @Positive(message = "El id de la inscripcion debe ser positivo")
    Long idInscripcion,

    @Schema(description = "Valor de la calificación", example = "6.7")
    @NotNull(message = "La calificación es necesaria")
    @Positive(message = "El valor de la calificacion debe ser positivo")
    BigDecimal calificacion
) {

}
