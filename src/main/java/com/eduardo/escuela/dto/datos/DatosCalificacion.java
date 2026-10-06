package com.eduardo.escuela.dto.datos;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de una calificaion")
public record DatosCalificacion(
    @Schema(description = "Nombre del curso", example = "Matemáticas 1")
    String curso, 
    
    @Schema(description = "Periodo del curso", example = "2026-09")
    String periodo, 
    
    @Schema(description = "Valor de la calificación", example = "9.9")
    BigDecimal calificacion
) {

}
