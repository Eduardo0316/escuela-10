package com.eduardo.escuela.dto.calificaciones;

import java.math.BigDecimal;
import com.eduardo.escuela.dto.datos.DatosInscripcion;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de una calificación")
public record CalificacionResponse(
    @Schema(description = "Id de la califiación", example = "1")
    Long id,
    
    @Schema(description = "Datos de la inscripcion")
    DatosInscripcion inscripcion,
    
    @Schema(description = "Valor de la calificación", example = "6.7")
    BigDecimal calificacion,
    
    @Schema(description = "Día de registro de la calificación", example = "07/10/2026")
    String fechaRegistro
) {

}
