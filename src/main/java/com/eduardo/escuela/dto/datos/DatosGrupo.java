package com.eduardo.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un grupo")
public record DatosGrupo(
    @Schema(example = "Matemáticas I")
    String curso,
    
    @Schema(example = "Laura Martínez Martínez") 
    String maestro,
    
    @Schema(example = "Aula 101") 
    String aula,
    
    @Schema(example = "2025-01") 
    String periodo
) {}
